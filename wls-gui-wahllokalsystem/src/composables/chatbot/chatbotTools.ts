import {storeToRefs} from "pinia";
import {useInfomanagementStore} from "@/stores/infomanagementStore.ts";

export function useChatbotTools() {

  const { konfigurationsparameter } = storeToRefs(useInfomanagementStore());

  const CHAT_WIDGET_ENDPOINT = "/chat/widget/";
  const CHAT_WIDGET_SCRIPT_ID = "ut-widget";

  function injectChatWidget() {
    const src = "https://bot-test.muenchen.ubitec.bot/widget/widget/";

    if (!src) return;
    if (document.getElementById(CHAT_WIDGET_SCRIPT_ID)) return;
    if (typeof navigator !== "undefined" && navigator && !navigator.onLine)
      return;

    const script = document.createElement("script");
    script.async = true;
    script.id = CHAT_WIDGET_SCRIPT_ID;
    script.src = src;
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
