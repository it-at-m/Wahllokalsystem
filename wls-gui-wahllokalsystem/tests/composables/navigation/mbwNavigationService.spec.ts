import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useCommonErgebnismeldungTestDataFactory } from "@tests/utils/ergebnismeldung/common/commonErgebnismeldungTestDataFactory.ts";
import { useWorkflowTestDataFactory } from "@tests/utils/navigation/NavigationTestDataFactory.ts";
import { assertThatRequiredRoutesAreReturned } from "@tests/utils/navigation/navigationTestUtils.ts";
import { flushPromises } from "@vue/test-utils";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useMbwNavigationService } from "@/composables/navigation/mbwNavigationService.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { MbwStepsEnum } from "@/types/navigation/MbwStepsEnum.ts";

const { generateRandomString } = useCommonTestDataFactory();
const { prepareElectionWorkflow } = useWorkflowTestDataFactory();
const { prepareBezirkUndWahlID } = useCommonErgebnismeldungTestDataFactory();

vi.mock(import("@/plugins/router.ts"), () => {
  return {};
});

describe("mbwNavigationService.ts", () => {
  const mbwStepsStapelerfassung = {
    MBW_AUSZAEHLUNG_STIMMZETTEL: MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL,
    MBW_STAPEL_E: MbwStepsEnum.MBW_STAPEL_E,
    MBW_STAPEL_D_UNGUELTIG: MbwStepsEnum.MBW_STAPEL_D_UNGUELTIG,
    MBW_STAPEL_A_AND_B: MbwStepsEnum.MBW_STAPEL_A_AND_B,
    MBW_SCHNELLMELDUNG: MbwStepsEnum.MBW_SCHNELLMELDUNG,
    MBW_STAPEL_BC: MbwStepsEnum.MBW_STAPEL_BC,
    MBW_NIEDERSCHRIFT: MbwStepsEnum.MBW_NIEDERSCHRIFT,
  };
  const mbwStepsWithDseForSchriftfuehrung = {
    MBW_AUSZAEHLUNG_STIMMZETTEL: MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL,
    MBW_DSE_STIMMZETTELERFASSUNG: MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG,
    MBW_DSE_MONITORING: MbwStepsEnum.MBW_DSE_MONITORING,
    MBW_DSE_BESCHLUSSFASSUNG: MbwStepsEnum.MBW_DSE_BESCHLUSSFASSUNG,
    MBW_SCHNELLMELDUNG: MbwStepsEnum.MBW_SCHNELLMELDUNG,
    MBW_NIEDERSCHRIFT: MbwStepsEnum.MBW_NIEDERSCHRIFT,
  };
  const mbwStepsWithDseForErfassungsteam = {
    MBW_DSE_STIMMZETTELERFASSUNG: MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG,
  };
  const wahlID = generateRandomString(10);
  const wahlbezirkID = generateRandomString(10);

  beforeEach(() => {
    createTestingPinia({
      createSpy: vi.fn,
    });
  });

  describe("navigation", () => {
    it("should_returnEmptyArray_when_noStatusExists", () => {
      const unitUnderTest = useMbwNavigationService(wahlID, wahlbezirkID);

      const navigation = unitUnderTest.navigation;
      expect(navigation.value).toStrictEqual([]);
    });

    it("should_returnNavigationForStapelerfassung_when_statusExistsAndDseIsInactive", () => {
      useWorkflowStore().electionWorkflowsStates = [
        prepareElectionWorkflow()
          .bezirkUndWahlID(
            prepareBezirkUndWahlID()
              .wahlID(wahlID)
              .wahlbezirkID(wahlbezirkID)
              .build()
          )
          .build(),
      ];
      // @ts-expect-error: cannot set readonly
      useUserStore().hasRoleSchriftfuehrung = true;
      useInfomanagementStore().konfigurationsparameter = [
        { schluessel: "DSE_AKTIV", wert: "false" },
      ];

      const unitUnderTest = useMbwNavigationService(wahlID, wahlbezirkID);

      const navigation = unitUnderTest.navigation;

      assertThatRequiredRoutesAreReturned(
        navigation,
        wahlID,
        wahlbezirkID,
        mbwStepsStapelerfassung
      );
    });

    it("should_returnNavigationWithAllDseSteps_when_statusIsSetAfterInitAndUserIsSchriftfuehrung", async () => {
      // @ts-expect-error: cannot set readonly
      useUserStore().hasRoleSchriftfuehrung = true;
      useInfomanagementStore().konfigurationsparameter = [
        { schluessel: "DSE_AKTIV", wert: "true" },
      ];
      const unitUnderTest = useMbwNavigationService(wahlID, wahlbezirkID);
      const navigation = unitUnderTest.navigation;

      expect(navigation.value.length).toStrictEqual(0);

      useWorkflowStore().electionWorkflowsStates = [
        prepareElectionWorkflow()
          .bezirkUndWahlID(
            prepareBezirkUndWahlID()
              .wahlID(wahlID)
              .wahlbezirkID(wahlbezirkID)
              .build()
          )
          .build(),
      ];
      await flushPromises();

      assertThatRequiredRoutesAreReturned(
        navigation,
        wahlID,
        wahlbezirkID,
        mbwStepsWithDseForSchriftfuehrung
      );
    });

    it("should_returnOnlyStimmzettelerfassungNavigation_when_dseIsActiveAndUserIsErfassungsteam", () => {
      useWorkflowStore().electionWorkflowsStates = [
        prepareElectionWorkflow()
          .bezirkUndWahlID(
            prepareBezirkUndWahlID()
              .wahlID(wahlID)
              .wahlbezirkID(wahlbezirkID)
              .build()
          )
          .build(),
      ];
      // @ts-expect-error: cannot set readonly
      useUserStore().hasRoleSchriftfuehrung = false;
      useInfomanagementStore().konfigurationsparameter = [
        { schluessel: "DSE_AKTIV", wert: "true" },
      ];

      const unitUnderTest = useMbwNavigationService(wahlID, wahlbezirkID);

      assertThatRequiredRoutesAreReturned(
        unitUnderTest.navigation,
        wahlID,
        wahlbezirkID,
        mbwStepsWithDseForErfassungsteam
      );
    });
  });

  describe("dse workflow", () => {
    const dseStepsForSchriftfuehrung = [
      MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL,
      MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG,
      MbwStepsEnum.MBW_DSE_MONITORING,
      MbwStepsEnum.MBW_DSE_BESCHLUSSFASSUNG,
      MbwStepsEnum.MBW_SCHNELLMELDUNG,
      MbwStepsEnum.MBW_NIEDERSCHRIFT,
    ];

    beforeEach(() => {
      // @ts-expect-error: cannot set readonly
      useUserStore().hasRoleSchriftfuehrung = true;
      useInfomanagementStore().konfigurationsparameter = [
        { schluessel: "DSE_AKTIV", wert: "true" },
      ];
    });

    it.each([
      {
        stepsDone: {},
        enabledSteps: [MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL],
        expectedRoute: MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL,
      },
      {
        stepsDone: { [MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL]: true },
        enabledSteps: [
          MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL,
          MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG,
        ],
        expectedRoute: MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG,
      },
      {
        stepsDone: {
          [MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL]: true,
          [MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG]: true,
        },
        enabledSteps: [
          MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL,
          MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG,
          MbwStepsEnum.MBW_DSE_MONITORING,
        ],
        expectedRoute: MbwStepsEnum.MBW_DSE_MONITORING,
      },
      {
        stepsDone: {
          [MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL]: true,
          [MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG]: true,
          [MbwStepsEnum.MBW_DSE_MONITORING]: true,
        },
        enabledSteps: [
          MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL,
          MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG,
          MbwStepsEnum.MBW_DSE_MONITORING,
          MbwStepsEnum.MBW_DSE_BESCHLUSSFASSUNG,
        ],
        expectedRoute: MbwStepsEnum.MBW_DSE_BESCHLUSSFASSUNG,
      },
      {
        stepsDone: {
          [MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL]: true,
          [MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG]: true,
          [MbwStepsEnum.MBW_DSE_MONITORING]: true,
          [MbwStepsEnum.MBW_DSE_BESCHLUSSFASSUNG]: true,
        },
        enabledSteps: [
          MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL,
          MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG,
          MbwStepsEnum.MBW_DSE_MONITORING,
          MbwStepsEnum.MBW_DSE_BESCHLUSSFASSUNG,
          MbwStepsEnum.MBW_SCHNELLMELDUNG,
        ],
        expectedRoute: MbwStepsEnum.MBW_SCHNELLMELDUNG,
      },
      {
        stepsDone: {
          [MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL]: true,
          [MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG]: true,
          [MbwStepsEnum.MBW_DSE_MONITORING]: true,
          [MbwStepsEnum.MBW_DSE_BESCHLUSSFASSUNG]: true,
          [MbwStepsEnum.MBW_SCHNELLMELDUNG]: true,
        },
        enabledSteps: dseStepsForSchriftfuehrung,
        expectedRoute: MbwStepsEnum.MBW_NIEDERSCHRIFT,
      },
    ])(
      "should_correctlyHandleNavigationAndNextRoute_when_navigateToNextStep",
      ({ stepsDone, enabledSteps, expectedRoute }) => {
        setDseWorkflow(stepsDone);

        const service = useMbwNavigationService(wahlID, wahlbezirkID);

        // 1. Navigation State prüfen
        service.navigation.value.forEach((navigationItem) => {
          expect(navigationItem.disabled).toBe(
            !(enabledSteps as string[]).includes(
              navigationItem.targetRoute.name
            )
          );
        });

        // 2. Nächste Route prüfen
        const nextRoute = service.getNextRouteOrNull();
        expect(nextRoute).toStrictEqual({
          name: expectedRoute,
          params: { wahlId: wahlID, wahlbezirkId: wahlbezirkID },
        });
      }
    );

    it("should_returnStimmzettelerfassung_when_dseIsActiveAndUserIsErfassungsteam", () => {
      // @ts-expect-error: cannot set readonly
      useUserStore().hasRoleSchriftfuehrung = false;
      setDseWorkflow({});

      const result = useMbwNavigationService(
        wahlID,
        wahlbezirkID
      ).getNextRouteOrNull();

      expect(result).toStrictEqual({
        name: MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG,
        params: { wahlId: wahlID, wahlbezirkId: wahlbezirkID },
      });
    });

    function setDseWorkflow(stepsDone: Partial<Record<string, boolean>>) {
      useWorkflowStore().electionWorkflowsStates = [
        prepareElectionWorkflow()
          .bezirkUndWahlID(
            prepareBezirkUndWahlID()
              .wahlID(wahlID)
              .wahlbezirkID(wahlbezirkID)
              .build()
          )
          .stepsDone(stepsDone as Record<string, boolean>)
          .build(),
      ];
    }
  });
});
