import type { VueWrapper } from "@vue/test-utils";
import type { Ref } from "vue";

import { COMPONENT_EVENT_TESTS } from "@tests/utils/testutils.ts";
import { flushPromises, mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import OfflineSyncerDialog from "@/components/wlsComponents/OfflineSyncerDialog.vue";
import pinia from "@/plugins/pinia.ts";
import vuetify from "@/plugins/vuetify.ts";

const mockDefinitions = vi.hoisted(() => ({
  synchronizeOfflineData: vi.fn(),
  getSyncTasks: vi.fn(),
  isOfflineDataSyncing: undefined as Ref<boolean> | undefined,
  numberOfTasksFinished: undefined as Ref<number> | undefined,
  numberOfTasksToRun: undefined as Ref<number> | undefined,
}));

vi.mock("@/stores/dataSyncStore.ts", () => {
  return {
    useDataSyncStore: () => ({
      synchronizeOfflineData: mockDefinitions.synchronizeOfflineData,
      getSyncTasks: mockDefinitions.getSyncTasks,
      isOfflineDataSyncing: mockDefinitions.isOfflineDataSyncing,
      numberOfTasksFinished: mockDefinitions.numberOfTasksFinished,
      numberOfTasksToRun: mockDefinitions.numberOfTasksToRun,
    }),
  };
});

describe("OfflineSyncerDialog", () => {
  let wrapper: VueWrapper;

  vi.stubGlobal("visualViewport", new EventTarget());
  const ResizeObserverMock = vi.fn(() => ({
    observe: vi.fn(),
    unobserve: vi.fn(),
    disconnect: vi.fn(),
  }));
  vi.stubGlobal("ResizeObserver", ResizeObserverMock);

  beforeEach(() => {
    mockDefinitions.numberOfTasksFinished = ref(0);
    mockDefinitions.numberOfTasksToRun = ref(0);
    mockDefinitions.isOfflineDataSyncing = ref(false);
    mockDefinitions.getSyncTasks.mockReturnValue([]);
    wrapper = mount(OfflineSyncerDialog, {
      global: {
        plugins: [vuetify, pinia],
      },
      props: { isDialogVisible: false },
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_triggerOfflineDataSync_when_isDialogVisibleChangesToTrue", async () => {
      await wrapper.setProps({ isDialogVisible: true });

      mockDefinitions.synchronizeOfflineData.mockResolvedValueOnce(
        Promise.resolve()
      );

      expect(mockDefinitions.synchronizeOfflineData).toHaveBeenCalledOnce();
    });

    it("should_notTriggerOfflineDataSync_when_dialogIsVisibleAndValueIsAlreadySyncing", async () => {
      // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
      mockDefinitions.isOfflineDataSyncing!.value = true;
      await wrapper.setProps({ isDialogVisible: true });

      mockDefinitions.synchronizeOfflineData.mockResolvedValue(
        Promise.resolve()
      );

      expect(mockDefinitions.synchronizeOfflineData).not.toHaveBeenCalledOnce();
    });

    it("should_emitSyncSuccessEvent_when_noOpenTasksRemainAfterSync", async () => {
      await wrapper.setProps({ isDialogVisible: true });
      mockDefinitions.synchronizeOfflineData.mockResolvedValueOnce(
        Promise.resolve()
      );
      await flushPromises();

      expect(wrapper.emitted()).toHaveProperty("syncSuccess");
    });

    it("should_emitSyncErrorEvent_when_openTasksRemainAfterSync", async () => {
      mockDefinitions.getSyncTasks.mockResolvedValueOnce([{}]);
      await wrapper.setProps({ isDialogVisible: true });
      mockDefinitions.synchronizeOfflineData.mockResolvedValueOnce(
        Promise.resolve()
      );
      await flushPromises();

      expect(wrapper.emitted()).toHaveProperty("syncError");
    });
  });
});
