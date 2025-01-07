import { mount } from "@vue/test-utils";
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

import * as api from "@/api/wls-clients/broadcast-service/broadcast-client";
import ExampleBackendCommunicationView from "@/views/ExampleBackendCommunicationView.vue";

// Mock für die API-Funktionen aus dem Client
vi.mock("@/api/wls-clients/broadcast-service/broadcast-client", () => ({
  getBroadcastMessage: vi.fn(),
  broadcastMessageRead: vi.fn(),
  postBroadcastMessage: vi.fn(),
}));

const pinia = createPinia();

describe("GetMessage", () => {
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
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("should_executeGetMessageFunction_when_buttonClicked", async () => {
    // überwacht den Aufruf der "getBroadcastMessage" Funktion
    const mockedFunctionCall = vi.spyOn(api, "getBroadcastMessage");

    // findet den gewünschten button in der komponente und löst das click-event aus
    const button = globalWrapper.find(".get-message-btn"); // der button kann in der vue komponente mit `class="name"` benannt und anschließend mit `wrapper.find(".name")` gefunden werden
    await button.trigger("click");

    expect(mockedFunctionCall).toHaveBeenCalled();
  });

  it("should_returnAndRenderBroadcastMessage_when_functionCalledWithCorrectParams", async () => {
    const mockedResponse = { nachricht: "hallo welt", oid: "12345" }; // todo: die response muss glaube ich angepasst werden

    // todo: warum ist das rot?
    api.getBroadcastMessage.mockResolvedValueOnce({
      json: async () => mockedResponse,
    });

    // alternative: klappt auch nicht
    // api.getBroadcastMessage = vi.fn().mockResolvedValue({
    //   json: async () => mockedResponse,
    // });

    await globalWrapper.vm.getMessage("id");

    expect(globalWrapper.vm.message).toBe("hallo welt");
    expect(api.getBroadcastMessage).toHaveBeenCalledWith("id");
  });

  it("should_returnWlsError_when_noMessageFound", async () => {});

  it("should_throwError_when_functionCalledWithWrongOrMissingParams", () => {});

  // test api calls: https://test-utils.vuejs.org/guide/advanced/http-requests
  // test api calls with jest: https://stackoverflow.com/questions/53799460/mocking-methods-on-a-vue-instance-during-tdd/53799803#53799803
  // test api calls with fetch: https://stackoverflow.com/questions/65354366/vue-test-utils-mock-fetch-response-from-another-component
});

describe("ReadMessage", () => {});

describe("PostMessage", () => {});
