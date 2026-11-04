import { storeToRefs } from "pinia";

import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";

export function useChatbotTools() {
  const { chatbotUrl } = storeToRefs(useInfomanagementStore());

  const CHAT_WIDGET_ENDPOINT = "/chat/widget/";
  const CHAT_WIDGET_SCRIPT_ID = "ut-widget";

  function injectChatWidget() {
    if (!chatbotUrl.value) return;
    if (document.getElementById(CHAT_WIDGET_SCRIPT_ID)) return;
    if (typeof navigator !== "undefined" && navigator && !navigator.onLine)
      return;

    const script = document.createElement("script");
    script.async = true;
    script.id = CHAT_WIDGET_SCRIPT_ID;
    script.src = chatbotUrl.value;
    script.setAttribute("data-chat-endpoint", CHAT_WIDGET_ENDPOINT);
    document.head.appendChild(script);
  }

  function updateChatWidgetVisibility(isVisible: boolean) {
    document.documentElement.classList.toggle("chatbot-hidden", !isVisible);
  }

  return {
    injectChatWidget,
    updateChatWidgetVisibility,
  };
}
