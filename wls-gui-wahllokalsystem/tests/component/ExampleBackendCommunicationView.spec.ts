import { mount } from "@vue/test-utils";
import { createPinia } from "pinia";
import { beforeAll, beforeEach, describe, expect, it, vi } from "vitest";
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";

import pinia from "@/plugins/pinia";
import ExampleBackendCommunicationView from "@/views/ExampleBackendCommunicationView.vue";

// Mock für die Composable-Funktionen
const mockGetMessage = vi.fn();
vi.mock(
  "@/composables/wlsClients/broadcastService/useBroadcastService",
  () => ({
    useBroadcastService: () => ({
      getMessage: mockGetMessage,
    }),
  })
);

describe("GetBroadcastMessage", () => {
  let vuetify: ReturnType<typeof createVuetify>;
  // damit der wrapper nicht in jedem test erneut definiert werden muss, sondern nur einmal in beforeEach,
  // muss er hier global angelegt werden, damit die tests ihn auch erkennen
  let globalWrapper: any;

  beforeAll(() => {
    createPinia();
    createVuetify();
  });

  beforeEach(() => {
    vuetify = createVuetify({
      components,
      directives,
    });
    // hier mount statt shallowMount, weil sonst einzelne buttons nicht gefunden werden können!
    globalWrapper = mount(ExampleBackendCommunicationView, {
      global: { plugins: [pinia, vuetify] },
    });
    vi.clearAllMocks();
  });

  it("should_executeGetMessageFunction_when_buttonClicked", async () => {
    mockGetMessage.mockResolvedValueOnce({
      message: "Sample message",
      error: null,
    });
    // findet den gewünschten button in der komponente und löst das click-event aus
    // der button kann in der vue komponente mit `class="name"` benannt und anschließend mit `wrapper.find(".name")` gefunden werden
    await globalWrapper.find(".get-message-btn").trigger("click");

    expect(mockGetMessage).toHaveBeenCalled();
  });

  it("should_returnAndRenderBroadcastMessage_when_functionCallWasSuccessful", async () => {
    const mockMessage = "Neue Testnachricht";
    mockGetMessage.mockResolvedValueOnce({ message: mockMessage, error: null });

    await globalWrapper.find(".get-message-btn").trigger("click");

    expect(globalWrapper.find("pre").text()).toBe(mockMessage);
    expect(globalWrapper.find("p").exists()).toBe(false); // Kein Fehler sollte angezeigt werden
  });

  it("should_returnAndRenderError_when_functionCallFailed", async () => {
    const mockError = "Fehler beim Abrufen der Nachricht";
    mockGetMessage.mockResolvedValueOnce({ message: "", error: mockError });

    await globalWrapper.find(".get-message-btn").trigger("click");

    expect(globalWrapper.find("p").text()).toBe(mockError);
    expect(globalWrapper.find("pre").exists()).toBe(false); // Keine Nachricht sollte angezeigt werden
  });
});
