import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import { useBeschlussfassungViewUtils } from "@/composables/dse/beschlussfassung/beschlussfassungViewUtils.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/persistedStimmzettel/StimmzettelGueltigkeitEnum.ts";

const mockDefinitions = await vi.hoisted(async () => {
  const activatedCallbacks: (() => Promise<void> | void)[] = [];

  return {
    registerActivated: (cb: () => Promise<void> | void) =>
      activatedCallbacks.push(cb),
    runActivatedCallbacks: async () => {
      const cbs = activatedCallbacks.splice(0, activatedCallbacks.length);
      for (const cb of cbs) {
        await cb();
      }
    },
    clearActivatedCallbacks: () => activatedCallbacks.splice(0),
    getStimmzettel: vi.fn(),
    loadTeamStatusListe: vi.fn(),
    teamstatusList: [
      { teamID: "A", status: "REGISTRIERT" },
      { teamID: "B", status: "REGISTRIERT" },
    ],
  };
});

vi.mock("vue", async (importOriginal) => {
  const actual = (await importOriginal()) as object;
  return {
    ...actual,
    onActivated: (cb: () => Promise<void> | void) =>
      mockDefinitions.registerActivated(cb),
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

vi.mock(
  "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusState.ts",
  () => ({
    useStimmzettelerfassungStatusState: () => ({
      workflowStatus: ref([]),
    }),
  })
);

vi.mock("@/composables/dse/stimmzettelerfassung/stimmzettelService.ts", () => ({
  useStimmzettelService: () => ({
    getStimmzettel: mockDefinitions.getStimmzettel,
  }),
}));

describe("beschlussfassungViewUtils.ts", () => {
  const { preparePersistedStimmzettel } = useStimmzettelTestDataFactory();

  const wahlID = "W1";
  const wahlbezirkID = "WB1";

  let unitUnderTest: ReturnType<typeof useBeschlussfassungViewUtils>;

  beforeEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
    mockDefinitions.clearActivatedCallbacks();

    unitUnderTest = useBeschlussfassungViewUtils(wahlID, wahlbezirkID);
  });

  it("should_haveInitialState_when_created", () => {
    expect(unitUnderTest.stimmzettelForBeschlussfassung.value).toEqual([]);
    expect(unitUnderTest.completedStimmzettelForBeschlussfassung.value).toEqual(
      []
    );
    expect(unitUnderTest.isStimmzettelForBeschlussLoading.value).toBe(false);
    expect(unitUnderTest.isBeschlussfassungBeendenButtonDisabled.value).toBe(
      false
    );
  });

  describe("onActivated", async () => {
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

      const spy = vi.spyOn(
        unitUnderTest.isStimmzettelForBeschlussLoading,
        "value",
        "set"
      );

      const loadingPromise = mockDefinitions.runActivatedCallbacks();
      expect(unitUnderTest.isStimmzettelForBeschlussLoading.value).toBe(true);
      await loadingPromise;

      const expectedResult = [
        stimmzettelForBeschlussfassungTeamA,
        stimmzettelForBeschlussfassungTeamB,
      ];

      expect(mockDefinitions.loadTeamStatusListe).toHaveBeenCalled();
      expect(mockDefinitions.getStimmzettel).toHaveBeenCalledTimes(2);
      expect(unitUnderTest.isStimmzettelForBeschlussLoading.value).toBe(false);
      expect(unitUnderTest.stimmzettelForBeschlussfassung.value).toEqual(
        expectedResult
      );
      expect(spy.mock.calls).toStrictEqual([[true], [false]]);
      spy.mockReset();
    });

    it("should_notPopulateStimmzettelForBeschlussfassung_when_getStimmzettelReturnsEmptyList", async () => {
      mockDefinitions.getStimmzettel.mockResolvedValueOnce([]);
      mockDefinitions.getStimmzettel.mockResolvedValueOnce([]);

      await mockDefinitions.runActivatedCallbacks();

      expect(mockDefinitions.getStimmzettel).toHaveBeenCalledTimes(2);
      expect(unitUnderTest.stimmzettelForBeschlussfassung.value).toEqual([]);
    });
  });
});
