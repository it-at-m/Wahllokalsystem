import { defineStore, storeToRefs } from "pinia";
import { ref, watch } from "vue";

import { useMonitoringService } from "@/composables/monitoring/monitoringService.ts";
import { useDataSyncStore } from "@/stores/dataSyncStore.ts";
import { useUserStore } from "@/stores/userStore.ts";

const storeID = "onlineOffline";

export const useOnlineOfflineStore = defineStore(storeID, () => {
  const { postLastSeen } = useMonitoringService();
  const { synchronizeOfflineData } = useDataSyncStore();
  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());

  const isCheckingStatus = ref<boolean>(false);
  const isOnline = ref<boolean>(true); //because when u can load the application you are online
  const isOfflineCacheReady = ref<null | boolean>(null);

  async function checkConnectionState() {
    isCheckingStatus.value = true;
    try {
      await postLastSeen(currentUserWahlbezirkID.value);
      isOnline.value = true;
    } catch {
      isOnline.value = false;
    } finally {
      isCheckingStatus.value = false;
    }
  }

  /*
    should only trigger, when the state switches from Offline to Online
   */
  watch(isOnline, async (newIsOnline, oldIsOnline) => {
    if (!oldIsOnline && newIsOnline) {
      await synchronizeOfflineData();
    }
  });

  return {
    isCheckingStatus,
    isOnline,
    isOfflineCacheReady,
    checkConnectionState,
  };
});
