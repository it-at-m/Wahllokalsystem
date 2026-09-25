import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { StimmzettelerfassungStatus } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatus.ts";

import { usePersistedStimmzettelTestDataFactory } from "@tests/utils/dse/PersistedStimmzettelTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import { useBeschlussfassungViewUtils } from "@/composables/dse/beschlussfassung/beschlussfassungViewUtils.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatusEnum.ts";

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
    isBeschlussRequired: vi.fn(),
    loadStimmzettelOfWahlbezirk: vi.fn(),
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
const stimmzettelOfWahlbezirkMockedRef = ref<PersistedStimmzettel[]>([]);
vi.mock(
  import("@/composables/dse/allStimmzettelOfWahlbezirkState.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useAllStimmzettelOfWahlbezirkState: () => ({
        ...mod.useAllStimmzettelOfWahlbezirkState("", ""),
        stimmzettelOfWahlbezirk: stimmzettelOfWahlbezirkMockedRef,
        loadStimmzettelOfWahlbezirk: vi.fn().mockImplementation(() => {
          stimmzettelOfWahlbezirkMockedRef.value =
            mockDefinitions.loadStimmzettelOfWahlbezirk();
        }),
      }),
    };
  }
);

vi.mock(
  import("@/composables/dse/stimmzettelerfassung/stimmzettelTools"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useStimmzettelTools: () => ({
        ...mod.useStimmzettelTools(),
        isBeschlussRequired: mockDefinitions.isBeschlussRequired,
      }),
    };
  }
);

describe("beschlussfassungViewUtils.ts", () => {
  const { preparePersistedStimmzettel } =
    usePersistedStimmzettelTestDataFactory();

  const wahlID = "W1";
  const wahlbezirkID = "WB1";

  let unitUnderTest: ReturnType<typeof useBeschlussfassungViewUtils>;
  let workflowStore: ReturnType<typeof useWorkflowStore>;

  beforeEach(() => {
    setActivePinia(createPinia());
    stimmzettelOfWahlbezirkMockedRef.value = [];
    vi.clearAllMocks();
    vi.resetAllMocks();
    mockDefinitions.clearActivatedCallbacks();

    if (mockedWorkflowStatusRef) {
      mockedWorkflowStatusRef.value = null;
    }

    workflowStore = useWorkflowStore();
    workflowStore.electionWorkflowsStates = [];
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
    it("should_loadStimmzettelForBeschlussfassung_when_onActivatedSuccess", async () => {
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
      const someStimmzettelThatDoesNotNeedBeschluss =
        preparePersistedStimmzettel()
          .teamID("X")
          .gueltigkeit(StimmzettelGueltigkeitEnum.Valid)
          .build();

      mockDefinitions.loadStimmzettelOfWahlbezirk.mockReturnValue([
        stimmzettelForBeschlussfassungTeamA,
        someStimmzettelThatDoesNotNeedBeschluss,
        stimmzettelForBeschlussfassungTeamB,
      ]);
      mockDefinitions.isBeschlussRequired.mockImplementation(
        (stimmzettel: PersistedStimmzettel) =>
          stimmzettel.teamID !== someStimmzettelThatDoesNotNeedBeschluss.teamID
      );

      await mockDefinitions.runActivatedCallbacks();

      const expectedResult = [
        stimmzettelForBeschlussfassungTeamA,
        stimmzettelForBeschlussfassungTeamB,
      ];
      expect(unitUnderTest.stimmzettelForBeschlussfassung.value).toEqual(
        expectedResult
      );
    });

    it("should_notPopulateStimmzettelForBeschlussfassung_when_getStimmzettelReturnsEmptyList", async () => {
      mockDefinitions.loadStimmzettelOfWahlbezirk.mockReturnValue([]);
      mockDefinitions.isBeschlussRequired.mockReturnValue(true);

      await mockDefinitions.runActivatedCallbacks();

      expect(unitUnderTest.stimmzettelForBeschlussfassung.value).toEqual([]);
    });
  });

  describe("completedStimmzettelForBeschlussfassung", () => {
    it("should_returnOnlyStimmzettelWithGueltigkeitNotBeschlussAusstehend_when_givenStimmzettelWithMixedGueltigkeiten", async () => {
      const stZettBeschlussAusstehend = preparePersistedStimmzettel()
        .teamID("A")
        .gueltigkeit(StimmzettelGueltigkeitEnum.BeschlussAusstehend)
        .build();

      const stZettValid = preparePersistedStimmzettel()
        .teamID("B")
        .gueltigkeit(StimmzettelGueltigkeitEnum.Valid)
        .build();

      const stZettInvalid = preparePersistedStimmzettel()
        .teamID("C")
        .gueltigkeit(StimmzettelGueltigkeitEnum.Invalid)
        .build();

      mockDefinitions.isBeschlussRequired.mockReturnValue(true);

      stimmzettelOfWahlbezirkMockedRef.value = [
        stZettBeschlussAusstehend,
        stZettValid,
        stZettInvalid,
      ];

      expect(
        unitUnderTest.completedStimmzettelForBeschlussfassung.value
      ).toEqual([stZettValid, stZettInvalid]);
    });

    it("should_returnEmptyList_when_allStimmzettelHaveGueltigkeitBeschlussAusstehend", () => {
      const stZettA = preparePersistedStimmzettel()
        .teamID("A")
        .gueltigkeit(StimmzettelGueltigkeitEnum.BeschlussAusstehend)
        .build();

      const stZettB = preparePersistedStimmzettel()
        .teamID("B")
        .gueltigkeit(StimmzettelGueltigkeitEnum.BeschlussAusstehend)
        .build();

      mockDefinitions.isBeschlussRequired.mockReturnValue(true);
      stimmzettelOfWahlbezirkMockedRef.value = [stZettA, stZettB];

      expect(
        unitUnderTest.completedStimmzettelForBeschlussfassung.value
      ).toEqual([]);
    });
  });

  describe("isBeschlussfassungBeendenButtonDisabled", () => {
    it("should_returnTrue_when_notAllStimmzettelForBeschlussAreCompleted", () => {
      const stZettBeschlussAusstehend = preparePersistedStimmzettel()
        .teamID("A")
        .gueltigkeit(StimmzettelGueltigkeitEnum.BeschlussAusstehend)
        .build();

      const stZettCompleted = preparePersistedStimmzettel()
        .teamID("B")
        .gueltigkeit(StimmzettelGueltigkeitEnum.Valid)
        .build();

      mockDefinitions.isBeschlussRequired.mockReturnValue(true);
      stimmzettelOfWahlbezirkMockedRef.value = [
        stZettBeschlussAusstehend,
        stZettCompleted,
      ];

      expect(unitUnderTest.isBeschlussfassungBeendenButtonDisabled.value).toBe(
        true
      );
    });

    it("should_returnFalse_when_allStimmzettelCompleted", () => {
      const stZettCompleted1 = preparePersistedStimmzettel()
        .teamID("A")
        .gueltigkeit(StimmzettelGueltigkeitEnum.Valid)
        .build();

      const stZettCompleted2 = preparePersistedStimmzettel()
        .teamID("B")
        .gueltigkeit(StimmzettelGueltigkeitEnum.Invalid)
        .build();

      mockDefinitions.isBeschlussRequired.mockReturnValue(true);
      stimmzettelOfWahlbezirkMockedRef.value = [
        stZettCompleted1,
        stZettCompleted2,
      ];

      expect(unitUnderTest.isBeschlussfassungBeendenButtonDisabled.value).toBe(
        false
      );
    });

    it("should_returnTrue_when_workflowStatusIsBeAbgeschlossen", () => {
      // @ts-expect-error: mockedWorkflowStatusRef is possibly unused
      mockedWorkflowStatusRef.value = {
        status: StimmzettelerfassungStatusEnum.BeAbgeschlossen,
      };

      mockDefinitions.isBeschlussRequired.mockReturnValue(true);
      stimmzettelOfWahlbezirkMockedRef.value = [];
      expect(unitUnderTest.isBeschlussfassungBeendenButtonDisabled.value).toBe(
        true
      );
    });

    it("should_returnTrue_when_teamStatusIsLoading", async () => {
      unitUnderTest.isStimmzettelForBeschlussLoading.value = true;
      expect(unitUnderTest.isBeschlussfassungBeendenButtonDisabled.value).toBe(
        true
      );
    });
  });

  describe("isBeschlussBearbeitenDisabled", () => {
    it("should_returnTrue_when_electionIsFinished", () => {
      workflowStore.initElectionWorkflowState(wahlID, wahlbezirkID);
      workflowStore.electionWorkflowsStates[0].isNiederschriftDone = true;

      expect(unitUnderTest.isBeschlussBearbeitenDisabled.value).toBe(true);
    });
  });
});
