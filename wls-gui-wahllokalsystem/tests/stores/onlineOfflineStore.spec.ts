import { createPinia, setActivePinia } from "pinia";
import {
  afterAll,
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import { nextTick } from "vue";

import { useOnlineOfflineStore } from "@/stores/onlineOfflineStore.ts";
import { useUserStore } from "@/stores/userStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  postLastSeen: vi.fn(),
  synchronizeOfflineData: vi.fn(),
}));

vi.mock("@/composables/monitoring/monitoringService.ts", () => ({
  useMonitoringService: vi.fn().mockImplementation(() => ({
    postLastSeen: mockDefinitions.postLastSeen,
  })),
}));

vi.mock("@/composables/indexDB/dataSyncer.ts", () => ({
  useDataSyncer: vi.fn().mockImplementation(() => ({
    synchronizeOfflineData: mockDefinitions.synchronizeOfflineData,
  })),
}));

describe("onlineOfflineStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useOnlineOfflineStore>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useOnlineOfflineStore();
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe("checkConnectionState", () => {
    it.each([true, false])(
      "should_setIsOnlineTrue_when_callOfLastSeenSucceededAndInitialStateWas'%s'",
      async (initialStateOfIsOnline) => {
        const wahlbezirkID = "wahlbezirkID";
        useUserStore().user.wahlbezirkID = wahlbezirkID;
        unitUnderTest.isOnline = initialStateOfIsOnline;

        mockDefinitions.postLastSeen.mockReturnValue(null);

        await unitUnderTest.checkConnectionState();

        expect(unitUnderTest.isOnline).toStrictEqual(true);
        expect(mockDefinitions.postLastSeen.mock.calls).toStrictEqual([
          [wahlbezirkID],
        ]);
      }
    );

    it.each([true, false])(
      "should_setIsOnlineFalse_when_callOfLastSeenFailedAndInitialStateWas'%s'",
      async (initialStateOfIsOnline) => {
        const wahlbezirkID = "wahlbezirkID";
        useUserStore().user.wahlbezirkID = wahlbezirkID;
        unitUnderTest.isOnline = initialStateOfIsOnline;

        mockDefinitions.postLastSeen.mockRejectedValue(
          new Error("mocked service error")
        );

        await unitUnderTest.checkConnectionState();

        expect(unitUnderTest.isOnline).toStrictEqual(false);
        expect(mockDefinitions.postLastSeen.mock.calls).toStrictEqual([
          [wahlbezirkID],
        ]);
      }
    );

    it("should_updateIsCheckingStatus_when_processingRequest", async () => {
      useUserStore().user.wahlbezirkID = "wahlbezirkID";

      expect(unitUnderTest.isCheckingStatus).toStrictEqual(false);

      const timeout = 100;
      mockDefinitions.postLastSeen.mockReturnValue(
        new Promise((resolve) => {
          setTimeout(() => {
            resolve({});
          }, timeout);
        })
      );

      const promise = unitUnderTest.checkConnectionState();
      expect(unitUnderTest.isCheckingStatus).toStrictEqual(true);

      vi.advanceTimersByTime(timeout);
      await promise;

      expect(unitUnderTest.isCheckingStatus).toStrictEqual(false);
    });

    it.each([true, false])(
      "should_endWithIsCheckingStatusFalse_when_anErrorOccurredDuringProcessingWithInitialState'%s'",
      async (initialStateOfIsOnline) => {
        useUserStore().user.wahlbezirkID = "wahlbezirkID";
        unitUnderTest.isCheckingStatus = initialStateOfIsOnline;

        mockDefinitions.postLastSeen.mockRejectedValue(
          new Error("mocked service error")
        );

        await unitUnderTest.checkConnectionState();

        expect(unitUnderTest.isCheckingStatus).toStrictEqual(false);
      }
    );
  });

  describe("triggerSync", () => {
    it("should_triggerSync_when_switchingFromOfflineToOnline", async () => {
      unitUnderTest.isOnline = false;
      await nextTick();
      unitUnderTest.isOnline = true;
      await nextTick();

      expect(
        mockDefinitions.synchronizeOfflineData.mock.calls.length
      ).toStrictEqual(1);
    });

    it("should_notTriggerSync_when_switchingFromOnlineToOffline", async () => {
      unitUnderTest.isOnline = true;
      await nextTick();
      unitUnderTest.isOnline = false;
      await nextTick();

      expect(
        mockDefinitions.synchronizeOfflineData.mock.calls.length
      ).toStrictEqual(0);
    });

    it("should_notTriggerSync_when_stayingOnlineForMultipleChecks", async () => {
      unitUnderTest.isOnline = true;
      await nextTick();
      unitUnderTest.isOnline = true;
      await nextTick();

      expect(
        mockDefinitions.synchronizeOfflineData.mock.calls.length
      ).toStrictEqual(0);
    });

    it("should_notTriggerSync_when_stayingOfflineForMultipleChecks", async () => {
      unitUnderTest.isOnline = false;
      await nextTick();

      expect(
        mockDefinitions.synchronizeOfflineData.mock.calls.length
      ).toStrictEqual(0);
    });
  });
});
