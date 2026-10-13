import { beforeEach, describe, expect, it, vi } from "vitest";

import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useMonitoringService } from "@/composables/monitoring/monitoringService.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  postLastSeen: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-monitoring-api", () => ({
  WahllokalZustandControllerApi: class {
    postLastSeen = mockDefinitions.postLastSeen;
  },
  Configuration: vi.fn(),
}));
vi.mock(
  import("@/composables/userNotification/userNotificationService.ts"),
  () => ({
    useUserNotificationService: () => ({
      addNotification: mockDefinitions.addNotification,
    }),
  })
);

const { postLastSeen } = useMonitoringService();
const { axiosConfigWrapper } = useCommonApiUtils();

const mockedNow = new Date();

describe("monitoringService.ts", () => {
  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();

    vi.useFakeTimers({
      now: mockedNow,
    });
  });

  describe("postLastSeen", () => {
    it("should_throwNoError_when_requestSucceeded", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const teamID = "teamID";

      mockDefinitions.postLastSeen.mockResolvedValue(null);

      await postLastSeen(wahlbezirkID, teamID);

      expect(mockDefinitions.postLastSeen.mock.calls).toStrictEqual([
        [wahlbezirkID, teamID, axiosConfigWrapper().requestAsOnlineOnly()],
      ]);
    });

    it("should_throwNewError_when_requestFailed", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const teamID = "teamID";

      const mockedApiError = new Error("mocked api call failed");
      mockDefinitions.postLastSeen.mockRejectedValue(mockedApiError);

      await expect(postLastSeen(wahlbezirkID, teamID)).rejects.toThrow(
        "postLastSeen failed"
      );
      expect(mockDefinitions.postLastSeen.mock.calls).toStrictEqual([
        [wahlbezirkID, teamID, axiosConfigWrapper().requestAsOnlineOnly()],
      ]);
    });
  });
});
