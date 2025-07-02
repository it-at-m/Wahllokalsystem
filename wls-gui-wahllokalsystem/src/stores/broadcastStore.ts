import type { BroadcastMessage } from "@/types/broadcast/broadcastMessage.ts";

import { defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useBroadcastService } from "@/composables/broadcast/broadcastService.ts";
import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useUserStore } from "@/stores/userStore.ts";

export const broadcastStoreId = "broadcast";
const { getMessage, deleteMessage } = useBroadcastService();
const { registerStoreHMR } = useHmrUpdate();

export const useBroadcastStore = defineStore(broadcastStoreId, () => {
  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());

  const currentBroadcastNachricht = ref<BroadcastMessage | null>(null);

  async function loadLatestMessage() {
    currentBroadcastNachricht.value = await getMessage(
      currentUserWahlbezirkID.value
    );
  }

  async function markMessageAsReadAndLoadNextMessage() {
    if (currentBroadcastNachricht.value !== null) {
      await deleteMessage(currentBroadcastNachricht.value.id);
    }
    await loadLatestMessage();
  }

  return {
    currentBroadcastNachricht,
    loadLatestMessage,
    markMessageAsReadAndLoadNextMessage,
  };
});

registerStoreHMR(useBroadcastStore);
