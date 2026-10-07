import type { Status } from "@/types/ergebnismeldung/common/Status.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";
import type { Router } from "vue-router";

import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import { useMbwNiederschriftViewUtils } from "@/composables/ergebnismeldung/MBW/mbwNiederschriftViewUtils.ts";
import { ROUTE_NOTFOUND } from "@/constants.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { MbwStepsEnum } from "@/types/navigation/MbwStepsEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  buildNiederschriftTemplateFromDataBWB: vi.fn(),
  buildNiederschriftTemplateFromDataUWB: vi.fn(),
  buildTemplate: vi.fn(),
  getAusdruckNiederschrift: vi.fn(),
  getElectionWorkflowState: vi.fn(),
  getEreignisse: vi.fn(),
  getNextRoute: vi.fn(),
  getWahlOrUndefinedById: vi.fn(),
  hasDoneVorkommnisse: vi.fn(),
  isStepDone: vi.fn(),
  loadStatusByWahlIdAndWahlbezirkId: vi.fn(),
  logError: vi.fn(),
  prepareDataForBeschlussentscheidungenDruck: vi.fn(),
  prepareDataForNiederschriftDruck: vi.fn(),
  sendAusdruckBeschlussentscheidungen: vi.fn(),
  sendAusdruckNiederschrift: vi.fn(),
  sendNiederschrift: vi.fn(),
  setStepDone: vi.fn(),
}));

const currentUserWahlbezirksArt = ref(WahlbezirksArtEnum.UWB);
const isSendingNiederschrift = ref(false);

vi.mock("vue", async (importOriginal) => {
  const module = await importOriginal<typeof import("vue")>();
  return {
    ...module,
    onActivated: (callback: () => void) => callback(),
  };
});
vi.mock("pinia", () => ({
  storeToRefs: () => ({ currentUserWahlbezirksArt }),
}));
vi.mock("@/stores/wahlenStore.ts", () => ({
  useWahlenStore: () => ({
    wahlenActions: {
      getWahlOrUndefinedById: mockDefinitions.getWahlOrUndefinedById,
    },
  }),
}));
vi.mock("@/stores/userStore.ts", () => ({ useUserStore: vi.fn() }));
vi.mock("@/stores/workflowStore.ts", () => ({
  useWorkflowStore: () => ({
    getElectionWorkflowState: mockDefinitions.getElectionWorkflowState,
    isStepDone: mockDefinitions.isStepDone,
    setStepDone: mockDefinitions.setStepDone,
  }),
}));
vi.mock("@/composables/common/logging.ts", () => ({
  useLogging: () => ({ logError: mockDefinitions.logError }),
}));
vi.mock("@/composables/ergebnismeldung/common/statusUtils.ts", () => ({
  useStatusUtils: () => ({
    loadStatusByWahlIdAndWahlbezirkId:
      mockDefinitions.loadStatusByWahlIdAndWahlbezirkId,
  }),
}));
vi.mock("@/composables/ergebnismeldung/MBW/mbwUtils.ts", () => ({
  useMbwUtils: () => ({
    getAusdruckNiederschrift: mockDefinitions.getAusdruckNiederschrift,
    isSendingNiederschrift,
    sendAusdruckNiederschrift: mockDefinitions.sendAusdruckNiederschrift,
    sendNiederschrift: mockDefinitions.sendNiederschrift,
  }),
}));
vi.mock(
  "@/composables/ergebnismeldung/MBW/mbwNiederschriftDruckService.ts",
  () => ({
    useMbwNiederschriftDruckService: () => ({
      prepareDataForNiederschriftDruck:
        mockDefinitions.prepareDataForNiederschriftDruck,
    }),
  })
);
vi.mock("@/composables/ergebnismeldung/MBW/niederschriftDruckUWB.ts", () => ({
  useNiederschriftDruckUWB: () => ({
    buildNiederschriftTemplateFromData:
      mockDefinitions.buildNiederschriftTemplateFromDataUWB,
  }),
}));
vi.mock("@/composables/ergebnismeldung/MBW/niederschriftDruckBWB.ts", () => ({
  useNiederschriftDruckBWB: () => ({
    buildNiederschriftTemplateFromData:
      mockDefinitions.buildNiederschriftTemplateFromDataBWB,
  }),
}));
vi.mock("@/composables/navigation/navigationService.ts", () => ({
  useNavigationService: () => ({ getNextRoute: mockDefinitions.getNextRoute }),
}));
vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));
vi.mock("@/composables/vorfaelleundvorkommnisse/ereignisService.ts", () => ({
  useEreignisService: () => ({ getEreignisse: mockDefinitions.getEreignisse }),
}));
vi.mock("@/composables/vorfaelleundvorkommnisse/ereignisUtils.ts", () => ({
  useEreignisUtils: () => ({
    hasDoneVorkommnisse: mockDefinitions.hasDoneVorkommnisse,
  }),
}));
vi.mock(
  "@/composables/dse/beschlussfassung/beschlussfassungViewUtils.ts",
  () => ({
    useBeschlussfassungViewUtils: () => ({
      stimmzettelForBeschlussfassung: ref([]),
    }),
  })
);
vi.mock(
  "@/composables/dse/beschlussfassung/beschlussentscheidungenDruckenTools.ts",
  () => ({
    useBeschlussentscheidungenDruckenTools: () => ({
      prepareDataForBeschlussentscheidungenDruck:
        mockDefinitions.prepareDataForBeschlussentscheidungenDruck,
      sendAusdruckBeschlussentscheidungen:
        mockDefinitions.sendAusdruckBeschlussentscheidungen,
    }),
  })
);
vi.mock(
  "@/composables/dse/beschlussfassung/beschlussentscheidungenDruckTemplateTools.ts",
  () => ({
    useBeschlussentscheidungenDruckTemplateTools: () => ({
      buildTemplate: mockDefinitions.buildTemplate,
    }),
  })
);

describe("mbwNiederschriftViewUtils", () => {
  const wahlID = "wahlID";
  const wahlbezirkID = "wahlbezirkID";
  const routerPush = vi.fn();
  const router = { push: routerPush } as unknown as Router;
  const wahl = {} as Wahl;
  const status = {
    niederschrift: { gedruckt: false, uebermittelt: true },
  } as Status;
  const workflowState = { isNiederschriftDone: false };
  const printWindow = {
    close: vi.fn(),
    document: { close: vi.fn(), writeln: vi.fn() },
    print: vi.fn(),
  };

  beforeEach(() => {
    mockDefinitions.getWahlOrUndefinedById.mockReturnValue(wahl);
    mockDefinitions.getElectionWorkflowState.mockReturnValue(workflowState);
    mockDefinitions.getNextRoute.mockReturnValue({ name: "nextRoute" });
    mockDefinitions.hasDoneVorkommnisse.mockReturnValue(true);
    mockDefinitions.isStepDone.mockReturnValue(false);
    mockDefinitions.sendAusdruckNiederschrift.mockResolvedValue(undefined);
    mockDefinitions.sendAusdruckBeschlussentscheidungen.mockResolvedValue(
      undefined
    );
    routerPush.mockResolvedValue(undefined);
    currentUserWahlbezirksArt.value = WahlbezirksArtEnum.UWB;
    vi.spyOn(window, "open").mockReturnValue(printWindow as unknown as Window);
  });

  afterEach(() => {
    vi.restoreAllMocks();
    vi.clearAllMocks();
    workflowState.isNiederschriftDone = false;
  });

  async function createComposable() {
    const unitUnderTest = useMbwNiederschriftViewUtils(
      wahlID,
      wahlbezirkID,
      router
    );
    await Promise.resolve();
    await Promise.resolve();
    return unitUnderTest;
  }

  it("should_navigateToNotFound_when_wahlDoesNotExist", async () => {
    mockDefinitions.getWahlOrUndefinedById.mockReturnValue(undefined);

    await createComposable();

    expect(routerPush).toHaveBeenCalledWith({ name: ROUTE_NOTFOUND });
  });

  describe("onSendenClicked", () => {
    it("should_showOfflineSyncDialog_when_sendenIsClicked", async () => {
      const unitUnderTest = await createComposable();

      unitUnderTest.onSendenClicked();

      expect(unitUnderTest.isOfflineSyncDialogVisible.value).toBe(true);
    });

    it("should_sendNiederschriftAndReloadStatus_when_syncSucceeded", async () => {
      const reloadedStatus = { niederschrift: {} } as Status;
      mockDefinitions.loadStatusByWahlIdAndWahlbezirkId.mockResolvedValue(
        reloadedStatus
      );
      const unitUnderTest = await createComposable();
      unitUnderTest.onSendenClicked();

      await unitUnderTest.onSyncSuccess();

      expect(unitUnderTest.isOfflineSyncDialogVisible.value).toBe(false);
      expect(mockDefinitions.sendNiederschrift).toHaveBeenCalledOnce();
      expect(
        mockDefinitions.loadStatusByWahlIdAndWahlbezirkId
      ).toHaveBeenCalledWith(wahlID, wahlbezirkID);
    });

    it("should_showSyncErrorDialog_when_syncFailed", async () => {
      const unitUnderTest = await createComposable();
      unitUnderTest.onSendenClicked();

      unitUnderTest.onSyncError();

      expect(unitUnderTest.isOfflineSyncDialogVisible.value).toBe(false);
      expect(unitUnderTest.isSyncErrorDialogVisible.value).toBe(true);
    });
  });

  describe("onDruckenClicked", () => {
    it("should_printAndStoreNewNiederschrift_when_niederschriftWasNotPrinted", async () => {
      mockDefinitions.prepareDataForNiederschriftDruck.mockResolvedValue({
        data: 1,
      });
      mockDefinitions.buildNiederschriftTemplateFromDataUWB.mockReturnValue(
        '<html lang="en">niederschrift</html>'
      );
      mockDefinitions.loadStatusByWahlIdAndWahlbezirkId.mockResolvedValue(
        status
      );
      const unitUnderTest = await createComposable();

      await unitUnderTest.onDruckenClicked();

      expect(
        mockDefinitions.prepareDataForNiederschriftDruck
      ).toHaveBeenCalledWith(status, wahl);
      expect(printWindow.document.writeln).toHaveBeenCalledWith(
        '<html lang="en">niederschrift</html>'
      );
      expect(mockDefinitions.setStepDone).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        MbwStepsEnum.MBW_NIEDERSCHRIFT
      );
      expect(mockDefinitions.sendAusdruckNiederschrift).toHaveBeenCalledWith(
        MeldungsArtEnum.Niederschrift,
        '<html lang="en">niederschrift</html>'
      );
    });

    it("should_showErrorNotification_when_printingNiederschriftFails", async () => {
      mockDefinitions.isStepDone.mockReturnValue(true);
      mockDefinitions.getAusdruckNiederschrift.mockRejectedValue(
        new Error("failed")
      );
      const unitUnderTest = await createComposable();

      await unitUnderTest.onDruckenClicked();

      expect(mockDefinitions.logError).toHaveBeenCalledOnce();
      expect(mockDefinitions.addNotification).toHaveBeenCalledWith(
        "Fehler beim Drucken der Niederschrift.",
        UserNotificationCategoryEnum.ERROR
      );
      expect(unitUnderTest.isDruckenLoading.value).toBe(false);
    });

    it("should_printStoredNiederschrift_when_niederschriftWasAlreadyPrinted", async () => {
      mockDefinitions.isStepDone.mockReturnValue(true);
      mockDefinitions.getAusdruckNiederschrift.mockResolvedValue(
        '<html lang="en">stored</html>'
      );
      const unitUnderTest = await createComposable();

      await unitUnderTest.onDruckenClicked();

      expect(mockDefinitions.getAusdruckNiederschrift).toHaveBeenCalledWith(
        MeldungsArtEnum.Niederschrift
      );
      expect(printWindow.document.writeln).toHaveBeenCalledWith(
        '<html lang="en">stored</html>'
      );
      expect(
        mockDefinitions.prepareDataForNiederschriftDruck
      ).not.toHaveBeenCalled();
      expect(mockDefinitions.setStepDone).not.toHaveBeenCalled();
      expect(mockDefinitions.sendAusdruckNiederschrift).not.toHaveBeenCalled();
      expect(routerPush).toHaveBeenCalledWith({ name: "nextRoute" });
    });
  });

  describe("onBeschlussentscheidungenDruckenClicked", () => {
    it("should_printAndSendBeschlussentscheidungen_when_wahlExists", async () => {
      mockDefinitions.prepareDataForBeschlussentscheidungenDruck.mockReturnValue(
        {}
      );
      mockDefinitions.buildTemplate.mockReturnValue(
        '<html lang="en">beschluesse</html>'
      );
      const unitUnderTest = await createComposable();

      await unitUnderTest.onBeschlussentscheidungenDruckenClicked();

      expect(printWindow.document.writeln).toHaveBeenCalledWith(
        '<html lang="en">beschluesse</html>'
      );
      expect(
        mockDefinitions.sendAusdruckBeschlussentscheidungen
      ).toHaveBeenCalledWith(
        MeldungsArtEnum.Beschlussentscheidungen,
        '<html lang="en">beschluesse</html>'
      );
      expect(
        unitUnderTest.isBeschlussentscheidungenDruckenDialogVisble.value
      ).toBe(true);
    });
  });
});
