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
  // damit der wrapper nicht in jedem test erneut definiert werden muss, sondern nur einmal in beforeEach,
  // muss er hier global angelegt werden, damit die tests ihn auch erkennen
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
    // hier mount statt shallowMount, weil sonst einzelne buttons nicht gefunden werden können!
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
      mockGetMessage.mockResolvedValueOnce({
        message: "Sample message",
        error: null,
      });
      // findet den gewünschten button in der komponente und löst das click-event aus
      // der button kann in der vue komponente mit `class="name"` benannt und anschließend mit `wrapper.find(".name")` gefunden werden
      await wrapper
        .findComponent('[data-test="getMessageBtn"]')
        .trigger("click");

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

      // hier muss find statt findComponent verwendet werden, weil das dom-element <pre> keine vue komponente ist
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
      await wrapper.find('[data-test="postMessageBtn"]').trigger("click");

      expect(mockPostMessage).toHaveBeenCalled();
    });

    it("should_clearMessageInput_when_messageHasBeenSent", async () => {
      // set message input
      await wrapper
        .findComponent('[data-test="messageInput"]')
        .setValue("Test Message");

      // execute post
      mockPostMessage.mockResolvedValueOnce({ error: null });
      await wrapper
        .findComponent('[data-test="postMessageBtn"]')
        .trigger("click");

      // expect message input to be cleared
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
