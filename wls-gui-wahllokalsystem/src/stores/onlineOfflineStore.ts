import { defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useMonitoringService } from "@/composables/monitoring/monitoringService.ts";
import { useUserStore } from "@/stores/userStore.ts";

const storeID = "onlineOffline";

export const useOnlineOfflineStore = defineStore(storeID, () => {
  const { postLastSeen } = useMonitoringService();
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

  return {
    isCheckingStatus,
    isOnline,
    checkConnectionState,
  };
});
