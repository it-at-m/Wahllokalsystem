import { createTestingPinia } from "@pinia/testing";
import { useStatusTestDataFactory } from "@tests/utils/ergebnismeldung/common/statusTestDataFactory.ts";
import { flushPromises } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useStatusStore } from "@/stores/statusStore.ts";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/common/MeldungValidierungsstatusEnum.ts";

const { createStatus, prepareStatus } = useStatusTestDataFactory();

const mockDefinitions = vi.hoisted(() => ({
  getStatus: vi.fn(),
  postStatus: vi.fn(),
}));

vi.mock(
  import("@/composables/ergebnismeldung/common/statusService.ts"),
  () => ({
    useStatusService: () => ({
      getStatus: mockDefinitions.getStatus,
      postStatus: mockDefinitions.postStatus,
    }),
  })
);

describe("statusStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useStatusStore>;

  const wahlID = "wahlID";
  const wahlbezirkID = "wahlbezirkID";

  beforeEach(() => {
    const testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
    unitUnderTest = useStatusStore(testPinia);
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  describe("loadStatus", () => {
    it("should_addStatus_when_loadStatusIsCalledWithValidWahlIdAndWahlbezirkId", async () => {
      const status = createStatus();
      mockDefinitions.getStatus.mockReturnValue(status);

      unitUnderTest.loadStatus(wahlID, wahlbezirkID);

      await flushPromises();

      expect(mockDefinitions.getStatus).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        true
      );
      expect(unitUnderTest.status).toStrictEqual([status]);
    });

    it("should_addStatus_when_loadStatusIsCalledWithValidWahlIdAndWahlbezirkIdAndStatusArrayIsNotEmpty", async () => {
      const status = createStatus();
      mockDefinitions.getStatus.mockReturnValue(status);

      const existingStatus = createStatus();
      unitUnderTest.status = [existingStatus];

      unitUnderTest.loadStatus(wahlID, wahlbezirkID);

      await flushPromises();

      expect(mockDefinitions.getStatus).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        true
      );
      expect(unitUnderTest.status).toStrictEqual([existingStatus, status]);
    });

    it("should_addDefaultStatus_when_loadStatusReturnsNoStatus", async () => {
      mockDefinitions.getStatus.mockReturnValue(null);

      const defaultStatus = {
        bezirkUndWahlID: { wahlID, wahlbezirkID },
        schnellmeldung: {
          validierungsstatus: MeldungValidierungsstatusEnum.NichtValidiert,
          gedruckt: false,
          uebermittelt: undefined,
          sendeuhrzeit: undefined,
        },
        niederschrift: {
          validierungsstatus: MeldungValidierungsstatusEnum.NichtValidiert,
          gedruckt: false,
          uebermittelt: undefined,
          sendeuhrzeit: undefined,
        },
      };

      unitUnderTest.loadStatus(wahlID, wahlbezirkID);

      await flushPromises();

      expect(mockDefinitions.getStatus).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        true
      );
      expect(unitUnderTest.status).toStrictEqual([defaultStatus]);
    });

    it.each([{ sendNotification: true }, { sendNotification: false }])(
      "should_callServiceWithSendNotification$sendNotification_when_notificationParameterIsUsed",
      async (argument) => {
        await unitUnderTest.loadStatus(
          wahlID,
          wahlbezirkID,
          argument.sendNotification
        );

        expect(mockDefinitions.getStatus.mock.calls).toStrictEqual([
          [wahlID, wahlbezirkID, argument.sendNotification],
        ]);
      }
    );
  });

  describe("saveStatus", () => {
    it("should_saveAllStatusEntries_when_saveStatusIsCalled", async () => {
      const statusEntry1 = createStatus();
      const statusEntry2 = createStatus();
      unitUnderTest.status = [statusEntry1, statusEntry2];

      await unitUnderTest.saveStatus(wahlID, wahlbezirkID);

      expect(mockDefinitions.postStatus).toHaveBeenCalledTimes(2);
      expect(mockDefinitions.postStatus).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        statusEntry1,
        true
      );
      expect(mockDefinitions.postStatus).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        statusEntry2,
        true
      );
    });

    it("should_handleError_when_postStatusFails", async () => {
      const statusEntry = createStatus();
      unitUnderTest.status = [statusEntry];

      // Mock the postStatus function to throw an error
      mockDefinitions.postStatus.mockRejectedValue(new Error("Network error"));

      await expect(
        unitUnderTest.saveStatus(wahlID, wahlbezirkID)
      ).rejects.toThrow(
        `Fehler beim Speichern des Status für WahlID: ${wahlID}`
      );

      expect(mockDefinitions.postStatus).toHaveBeenCalledTimes(1);
    });

    it("should_toggleIsStatusSavingFromTrueToFalse_when_saving", async () => {
      const statusEntry = createStatus();
      unitUnderTest.status = [statusEntry];

      mockDefinitions.postStatus.mockResolvedValue(undefined);

      const saveStatusPromise = unitUnderTest.saveStatus(wahlID, wahlbezirkID);

      expect(unitUnderTest.isStatusSaving).toBe(true);

      await saveStatusPromise;

      expect(unitUnderTest.isStatusSaving).toBe(false);
    });
  });

  describe("getStatusEntry", () => {
    it("should_returnExistingStatus_when_entryExists", () => {
      const existingStatus = prepareStatus()
        .bezirkUndWahlID({ wahlID, wahlbezirkID })
        .build();
      unitUnderTest.status = [existingStatus];

      const result = unitUnderTest.getStatusEntry(wahlID, wahlbezirkID);

      expect(result).toStrictEqual(existingStatus);
      expect(unitUnderTest.status).toHaveLength(1);
    });

    it("should_addDefaultStatus_when_noExistingEntry", () => {
      unitUnderTest.status = [];

      const result = unitUnderTest.getStatusEntry(wahlID, wahlbezirkID);

      expect(result).toStrictEqual(unitUnderTest.status[0]);
      expect(result.bezirkUndWahlID).toStrictEqual({ wahlID, wahlbezirkID });
      expect(result).toStrictEqual({
        schnellmeldung: {
          validierungsstatus: MeldungValidierungsstatusEnum.NichtValidiert,
          gedruckt: false,
          uebermittelt: undefined,
          sendeuhrzeit: undefined,
        },
        niederschrift: {
          validierungsstatus: MeldungValidierungsstatusEnum.NichtValidiert,
          gedruckt: false,
          uebermittelt: undefined,
          sendeuhrzeit: undefined,
        },
        bezirkUndWahlID: {
          wahlID,
          wahlbezirkID,
        },
      });
    });

    it("should_addNewEntry_when_onlyNonMatchingEntryExists", () => {
      const otherStatus = prepareStatus()
        .bezirkUndWahlID({ wahlID: "otherWahl", wahlbezirkID: "otherBez" })
        .build();
      unitUnderTest.status = [otherStatus];

      unitUnderTest.getStatusEntry(wahlID, wahlbezirkID);

      expect(unitUnderTest.status).toHaveLength(2);
      expect(unitUnderTest.status).toContainEqual({
        schnellmeldung: {
          validierungsstatus: MeldungValidierungsstatusEnum.NichtValidiert,
          gedruckt: false,
          uebermittelt: undefined,
          sendeuhrzeit: undefined,
        },
        niederschrift: {
          validierungsstatus: MeldungValidierungsstatusEnum.NichtValidiert,
          gedruckt: false,
          uebermittelt: undefined,
          sendeuhrzeit: undefined,
        },
        bezirkUndWahlID: {
          wahlID,
          wahlbezirkID,
        },
      });
    });

    it("should_returnCorrectEntry_when_multipleEntriesExist", () => {
      const statusOne = prepareStatus()
        .bezirkUndWahlID({ wahlID, wahlbezirkID: "bezirkA" })
        .build();
      const statusTwo = prepareStatus()
        .bezirkUndWahlID({ wahlID, wahlbezirkID })
        .build();
      unitUnderTest.status = [statusOne, statusTwo];

      const result = unitUnderTest.getStatusEntry(wahlID, wahlbezirkID);

      expect(result).toStrictEqual(statusTwo);
      expect(unitUnderTest.status).toHaveLength(2);
    });
  });
});
