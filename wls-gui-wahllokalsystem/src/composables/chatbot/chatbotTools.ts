import {storeToRefs} from "pinia";
import {useInfomanagementStore} from "@/stores/infomanagementStore.ts";

export function useChatbotTools() {

  const { konfigurationsparameter } = storeToRefs(useInfomanagementStore());

  const CHAT_WIDGET_PARAM_KEY = "CHAT_WIDGET_URL"; // wird serverseitig geliefert
  const CHAT_WIDGET_ENDPOINT = "/chat/widget/"; // fester Endpoint wie im Altcode
  const CHAT_WIDGET_SCRIPT_ID = "ut-widget"; // Script-ID aus Altcode

  function injectChatWidget() {

    const src = "https://bot-test.muenchen.ubitec.bot/widget/widget/";

    // In Testumgebung nicht laden, verhindert Seiteneffekte in Vitest/Storybook
    if (import.meta.env.MODE === "test") return;
    // Ohne sinnvolle URL nicht fortfahren
    if (!src) return;
    // Duplicate-Injektion vermeiden (z. B. HMR oder Mehrfachaufrufe)
    if (document.getElementById(CHAT_WIDGET_SCRIPT_ID)) return;
    // Optional: nur laden, wenn online
    if (typeof navigator !== "undefined" && navigator && !navigator.onLine) return;

    const script = document.createElement("script");
    script.async = true;
    script.id = CHAT_WIDGET_SCRIPT_ID;
    script.src = src;
    script.setAttribute("data-chat-endpoint", CHAT_WIDGET_ENDPOINT);
    document.head.appendChild(script);
  }

  return {
    injectChatWidget,
  };
}