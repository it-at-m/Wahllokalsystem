import { defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useMonitoringService } from "@/composables/monitoring/monitoringService.ts";
import { useUserStore } from "@/stores/userStore.ts";

const storeID = "onlineOffline";

const { postLastSeen } = useMonitoringService();

export const useOnlineOfflineStore = defineStore(storeID, () => {
  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());

  const isCheckingStatus = ref<boolean>(false);
  const isOnline = ref<boolean | null>(null);

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
