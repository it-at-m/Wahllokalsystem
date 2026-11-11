import { defineStore } from "pinia";
import { ref } from "vue";

const storeID = "chatbot";

export const useChatbotStore = defineStore(storeID, () => {
  const isChatbotVisible = ref(false);

  return {
    isChatbotVisible,
  };
});
