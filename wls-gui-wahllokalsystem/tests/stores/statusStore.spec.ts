import { createTestingPinia } from "@pinia/testing";
import { useStatusTestDataFactory } from "@tests/utils/ergebnismeldung/statusTestDataFactory.ts";
import { flushPromises } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useStatusStore } from "@/stores/statusStore.ts";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/MeldungValidierungsstatusEnum.ts";

const { createStatus } = useStatusTestDataFactory();

const mockDefinitions = vi.hoisted(() => ({
  getStatus: vi.fn(),
}));

vi.mock("@/composables/ergebnismeldung/statusService.ts", () => ({
  useStatusService: () => ({
    getStatus: mockDefinitions.getStatus,
  }),
}));

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
        },
        niederschrift: {
          validierungsstatus: MeldungValidierungsstatusEnum.NichtValidiert,
          gedruckt: false,
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
      'should_callServiceWithSendNotification"$sendNotification"_when_notificationParameterIsUsed',
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
});
