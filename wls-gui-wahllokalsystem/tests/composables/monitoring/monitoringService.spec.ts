import type { WaehleranzahlDTO } from "@/api/wls-clients/generated-monitoring-api";
import type { Waehleranzahl } from "@/types/monitoring/Waehleranzahl.ts";

import { beforeEach, describe, expect, it, vi } from "vitest";

import { useMonitoringService } from "@/composables/monitoring/monitoringService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  toDto: vi.fn(),
  postWahlbeteiligung: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-monitoring-api", () => ({
  WaehleranzahlControllerApi: vi.fn().mockImplementation(() => ({
    postWahlbeteiligung: mockDefinitions.postWahlbeteiligung,
  })),
  Configuration: vi.fn(),
}));
vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));
vi.mock("@/composables/monitoring/wahlbeteiligungMapper.ts", () => ({
  useWahlbeteiligungMapper: () => ({
    toDto: mockDefinitions.toDto,
  }),
}));

const { postWahlbeteiligung } = useMonitoringService();
const mockedNow = new Date();

describe("monitoringService.ts", () => {
  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();

    vi.useFakeTimers({
      now: mockedNow,
    });
  });

  describe("postWahlbeteiligung", () => {
    it("should_callNotificationService_when_sendingWahlbeteiligungFailed", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const wahlID = "wahlID";

      const waehleranzah: Waehleranzahl = {
        anzahlWaehler: 2,
        uhrzeit: mockedNow,
      };
      const mockedWaehleranzahlDTO: WaehleranzahlDTO = {
        anzahlWaehler: 2,
        uhrzeit: mockedNow.toISOString(),
      };

      mockDefinitions.toDto.mockReturnValue(mockedWaehleranzahlDTO);
      mockDefinitions.postWahlbeteiligung.mockRejectedValue(
        new Error("mocked api call failed")
      );

      await postWahlbeteiligung(wahlID, wahlbezirkID, waehleranzah);

      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
      expect(mockDefinitions.toDto.mock.calls).toStrictEqual([[waehleranzah]]);
    });
  });
});
