import type { StimmzettelerfassungStatus } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatus.ts";

import { usePersistedStimmzettelTestDataFactory } from "@tests/utils/dse/PersistedStimmzettelTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import { useAllStimmzettelOfWahlbezirkState } from "@/composables/dse/allStimmzettelOfWahlbezirkState.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

const mockDefinitions = await vi.hoisted(async () => {
  return {
    getStimmzettel: vi.fn(),
    loadTeamStatusListe: vi.fn(),
    teamstatusList: [
      { teamID: "A", status: "REGISTRIERT" },
      { teamID: "B", status: "REGISTRIERT" },
    ],
  };
});

vi.mock(
  "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusListState.ts",
  () => ({
    useStimmzettelerfassungTeamStatusListState: () => ({
      loadTeamStatusListe: mockDefinitions.loadTeamStatusListe,
      teamstatusList: ref(mockDefinitions.teamstatusList),
    }),
  })
);

let mockedWorkflowStatusRef: ReturnType<typeof ref> | undefined;

vi.mock(
  "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusState.ts",
  () => ({
    useStimmzettelerfassungStatusState: () => ({
      workflowStatus: (mockedWorkflowStatusRef =
        ref<StimmzettelerfassungStatus | null>(null)),
    }),
  })
);

vi.mock("@/composables/dse/stimmzettelerfassung/stimmzettelService.ts", () => ({
  useStimmzettelService: () => ({
    getStimmzettel: mockDefinitions.getStimmzettel,
  }),
}));

describe("allStimmzettelOfWahlbezirkState.ts", () => {
  const { preparePersistedStimmzettel } =
    usePersistedStimmzettelTestDataFactory();

  const wahlID = "W1";
  const wahlbezirkID = "WB1";

  let unitUnderTest: ReturnType<typeof useAllStimmzettelOfWahlbezirkState>;

  beforeEach(() => {
    unitUnderTest = useAllStimmzettelOfWahlbezirkState(wahlID, wahlbezirkID);
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();

    if (mockedWorkflowStatusRef) {
      mockedWorkflowStatusRef.value = null;
    }
  });

  describe("loadStimmzettelOfWahlbezirk", async () => {
    it("should_loadTeamStatusListeAndStimmzettel_when_onActivatedSuccess", async () => {
      const stimmzettelForBeschlussfassungTeamA = preparePersistedStimmzettel()
        .teamID("A")
        .gueltigkeit(StimmzettelGueltigkeitEnum.BeschlussAusstehend)
        .build();
      const stimmzettelForBeschlussfassungTeamB = preparePersistedStimmzettel()
        .teamID("B")
        .beschlussfassung({
          pro: 3,
          contra: 2,
          text: "beschluss wurde gefasst",
        })
        .build();
      const validStimmzettelTeamA = preparePersistedStimmzettel()
        .teamID("A")
        .gueltigkeit(StimmzettelGueltigkeitEnum.Valid)
        .beschlussfassung(null)
        .build();
      const inValidStimmzettelTeamB = preparePersistedStimmzettel()
        .teamID("B")
        .gueltigkeit(StimmzettelGueltigkeitEnum.Invalid)
        .beschlussfassung(null)
        .build();

      mockDefinitions.getStimmzettel.mockResolvedValueOnce([
        stimmzettelForBeschlussfassungTeamA,
        validStimmzettelTeamA,
      ]);
      mockDefinitions.getStimmzettel.mockResolvedValueOnce([
        stimmzettelForBeschlussfassungTeamB,
        inValidStimmzettelTeamB,
      ]);

      const spy = vi.spyOn(unitUnderTest.isLoading, "value", "set");

      const loadingPromise = unitUnderTest.loadStimmzettelOfWahlbezirk();
      expect(unitUnderTest.isLoading.value).toBe(true);
      await loadingPromise;

      const expectedResult = [
        stimmzettelForBeschlussfassungTeamA,
        validStimmzettelTeamA,
        stimmzettelForBeschlussfassungTeamB,
        inValidStimmzettelTeamB,
      ];

      expect(mockDefinitions.loadTeamStatusListe).toHaveBeenCalled();
      expect(mockDefinitions.getStimmzettel).toHaveBeenCalledTimes(2);
      expect(unitUnderTest.isLoading.value).toBe(false);
      expect(unitUnderTest.stimmzettelOfWahlbezirk.value).toEqual(
        expectedResult
      );
      expect(spy.mock.calls).toStrictEqual([[true], [false]]);
      spy.mockReset();
    });

    it("should_notPopulateStimmzettelForBeschlussfassung_when_getStimmzettelReturnsEmptyList", async () => {
      mockDefinitions.getStimmzettel.mockResolvedValueOnce([]);
      mockDefinitions.getStimmzettel.mockResolvedValueOnce([]);

      await unitUnderTest.loadStimmzettelOfWahlbezirk();

      expect(mockDefinitions.getStimmzettel).toHaveBeenCalledTimes(2);
      expect(unitUnderTest.stimmzettelOfWahlbezirk.value).toEqual([]);
    });

    it("should_toggleLoadingState_when_getStimmzettelFails", async () => {
      const mockedError = new Error("error");
      mockDefinitions.getStimmzettel.mockRejectedValue(mockedError);

      const spy = vi.spyOn(unitUnderTest.isLoading, "value", "set");

      await expect(unitUnderTest.loadStimmzettelOfWahlbezirk()).rejects.toThrow(
        mockedError
      );

      expect(unitUnderTest.isLoading.value).toBe(false);
      expect(spy.mock.calls).toStrictEqual([[true], [false]]);
      spy.mockReset();
    });
  });
});
