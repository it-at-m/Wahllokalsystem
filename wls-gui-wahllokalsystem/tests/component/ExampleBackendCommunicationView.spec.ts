import { enableAutoUnmount, mount, VueWrapper } from "@vue/test-utils";
import { createPinia } from "pinia";
import {
  afterEach,
  beforeAll,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";

import pinia from "@/plugins/pinia";
import ExampleBackendCommunicationView from "@/views/ExampleBackendCommunicationView.vue";

// Mock für die Composable-Funktionen
const mockGetMessage = vi.fn();
const mockPostMessage = vi.fn();
vi.mock(
  "@/composables/wlsClients/broadcastService/useBroadcastService",
  () => ({
    useBroadcastService: () => ({
      getMessage: mockGetMessage,
      postMessage: mockPostMessage,
    }),
  })
);
describe("ExampleBackendCommunicationView.vue", () => {
  let vuetify: ReturnType<typeof createVuetify>;
  // damit der wrapper nicht in jedem test erneut definiert werden muss, sondern nur einmal in beforeEach, wird er hier global angelegt
  let wrapper: VueWrapper;

  beforeAll(() => {
    createPinia();
    createVuetify();
  });

  beforeEach(() => {
    vuetify = createVuetify({
      components,
      directives,
    });
    // hier mount statt shallowMount, damit die einzelnen Dom-Elemente später in den test gefunden werden können
    // shallowmount würde die elemente innerhalb der zu testenden komponente "stubben" => es werden stattdessen placeholder gerendert
    wrapper = mount(ExampleBackendCommunicationView, {
      global: { plugins: [pinia, vuetify] },
    });
    // löscht die mocking-history
    vi.clearAllMocks();
  });

  // ruft nach jedem test wrapper.destroy() auf
  enableAutoUnmount(afterEach);

  describe("GetBroadcastMessage", () => {
    it("should_executeGetMessageFunction_when_buttonClicked", async () => {
      // mocked den rückgabewert der composable methode
      mockGetMessage.mockResolvedValueOnce({
        message: "Sample message",
        error: null,
      });

      // dom-elemente können in der vue komponente mit `data-test="name"` benannt
      // und im test anschließend mit `wrapper.findComponent('[data-test="name"]')` gefunden werden
      await wrapper
        .findComponent('[data-test="getMessageBtn"]') // findComponent, weil `v-btn` eine Vue Componente ist
        .trigger("click"); // löst das click-event aus

      expect(mockGetMessage).toHaveBeenCalled();
    });

    it("should_returnAndRenderBroadcastMessage_when_functionCallWasSuccessful", async () => {
      const mockMessage = "Neue Testnachricht";
      mockGetMessage.mockResolvedValueOnce({
        message: mockMessage,
        error: null,
      });

      await wrapper
        .findComponent('[data-test="getMessageBtn"]')
        .trigger("click");

      // hier wird `find` statt `findComponent` verwendet, weil das dom-element <pre> keine vue komponente ist
      expect(wrapper.find('[data-test="messageToShow"]').exists()).toBe(true);
      expect(wrapper.find('[data-test="messageToShow"]').html()).toContain(
        mockMessage
      );
    });

    it("should_returnAndRenderError_when_functionCallFailed", async () => {
      const mockError = "Fehler beim Abrufen der Nachricht";
      mockGetMessage.mockResolvedValueOnce({ message: "", error: mockError });

      await wrapper
        .findComponent('[data-test="getMessageBtn"]')
        .trigger("click");

      expect(wrapper.find('[data-test="errorToShow"]').exists()).toBe(true);
      expect(wrapper.find('[data-test="errorToShow"]').html()).toContain(
        mockError
      );
    });
  });

  describe("PostBroadcastMessage", () => {
    it("should_executePostMessageFunction_when_buttonClicked", async () => {
      mockPostMessage.mockResolvedValueOnce({ error: null });
      await wrapper
        .findComponent('[data-test="postMessageBtn"]')
        .trigger("click");

      expect(mockPostMessage).toHaveBeenCalled();
    });

    it("should_clearMessageInput_when_messageHasBeenSent", async () => {
      await wrapper
        .findComponent('[data-test="messageInput"]')
        .setValue("Test Message");

      mockPostMessage.mockResolvedValueOnce({ error: null });
      await wrapper
        .findComponent('[data-test="postMessageBtn"]')
        .trigger("click");

      expect(
        wrapper.findComponent('[data-test="messageInput"]').html()
      ).not.toContain("Test Message");
    });

    it("should_displayErrorMessage_when_postingMessageFailed", async () => {
      const mockError = "Failed to post message";

      mockPostMessage.mockResolvedValueOnce({ error: mockError });
      await wrapper
        .findComponent('[data-test="postMessageBtn"]')
        .trigger("click");

      expect(wrapper.find('[data-test="errorToShow"]').exists()).toBe(true);
      expect(wrapper.find('[data-test="errorToShow"]').html()).toContain(
        mockError
      );
    });
  });
});
