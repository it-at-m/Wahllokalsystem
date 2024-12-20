import { shallowMount } from "@vue/test-utils";
import { createPinia } from "pinia";
import { beforeAll, beforeEach, describe, expect, it } from "vitest";
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";

import ExampleBackendCommunicationView from "@/views/ExampleBackendCommunicationView.vue";

const pinia = createPinia();

describe("BackendCommunication", () => {
  let vuetify: ReturnType<typeof createVuetify>;

  // damit der wrapper nicht in jedem test erneut definiert werden muss, sondern nur einmal in beforeEach, muss er hier global angelegt werden, damit die tests ihn auch erkennen
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
    globalWrapper = shallowMount(ExampleBackendCommunicationView, {
      global: { plugins: [pinia, vuetify] },
    });
  });

  // test api calls: https://test-utils.vuejs.org/guide/advanced/http-requests
  // test api calls with jest: https://stackoverflow.com/questions/53799460/mocking-methods-on-a-vue-instance-during-tdd/53799803#53799803
  // test api calls with fetch: https://stackoverflow.com/questions/65354366/vue-test-utils-mock-fetch-response-from-another-component
  it("should_renderBroadcastMessage_when_messageFound", () => {
    const mockedMessage = "hello world";

    expect(globalWrapper.vm.message).toBe("");

    globalWrapper.vm.messageInput = "hello world"; // todo: klappt nicht
    globalWrapper.vm.postMessage("wbz-1");
    globalWrapper.vm.getMessage("wbz-1");

    expect(globalWrapper.vm.message).toBe(mockedMessage);
    //expect(globalWrapper.html()).toContain(message);
    //expect(globalWrapper.vm.message).toBe("message");
  });
});
