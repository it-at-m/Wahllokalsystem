import { defineStore, storeToRefs } from "pinia";
import { ref, watch } from "vue";

import { useDataSyncer } from "@/composables/indexDB/dataSyncer.ts";
import { useMonitoringService } from "@/composables/monitoring/monitoringService.ts";
import { useUserStore } from "@/stores/userStore.ts";

const storeID = "onlineOffline";

export const useOnlineOfflineStore = defineStore(storeID, () => {
  const { postLastSeen } = useMonitoringService();
  const { synchronizeOfflineData } = useDataSyncer();
  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());

  const isCheckingStatus = ref<boolean>(false);
  const isOnline = ref<boolean>(true); //because when u can load the application you are online

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
    checkConnectionState,
  };
});
