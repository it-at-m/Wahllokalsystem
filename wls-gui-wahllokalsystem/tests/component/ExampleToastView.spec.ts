import { mount } from "@vue/test-utils";
import { createPinia } from "pinia";
import { beforeEach, describe, expect, test } from "vitest";
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";

import { useUserNotificationStore } from "@/stores/useUserNotificationStore.ts";
import { UserNotificationCategoryEnum } from "@/types/wlsTypes/UserNotificationCategoryEnum.ts";
import ExampleToastView from "@/views/ExampleToastView.vue";

describe("ExampleToastView.ts", () => {
  let vuetify: ReturnType<typeof createVuetify>;
  let pinia = createPinia();

  beforeEach(() => {
    pinia = createPinia();
    vuetify = createVuetify({
      components,
      directives,
    });
  });

  test("should_addErfolgMessageToStore_when_erfolgButtonIsClicked", () => {
    const wrapper = mount(ExampleToastView, {
      global: {
        plugins: [pinia, vuetify],
      },
    });
    wrapper.find("#success-button").trigger("click");
    const store = useUserNotificationStore();
    expect(store.userNotifications.length).toStrictEqual(1);
    expect(store.userNotifications[0]).toStrictEqual(
      expect.objectContaining({
        message: "Erfolg",
        category: UserNotificationCategoryEnum.ERFOLG,
        id: expect.any(String),
      })
    );
  });

  test("should_addWarnungMessageToStore_when_warnungButtonIsClicked", () => {
    const wrapper = mount(ExampleToastView, {
      global: {
        plugins: [pinia, vuetify],
      },
    });
    wrapper.find("#warning-button").trigger("click");
    const store = useUserNotificationStore();
    expect(store.userNotifications.length).toStrictEqual(1);
    expect(store.userNotifications[0]).toStrictEqual(
      expect.objectContaining({
        message: "Warnung",
        category: UserNotificationCategoryEnum.WARNUNG,
        id: expect.any(String),
      })
    );
  });

  test("should_addMultipleMessagesToStore_when_multipleButtonsAreClicked", () => {
    const wrapper = mount(ExampleToastView, {
      global: {
        plugins: [pinia, vuetify],
      },
    });
    wrapper.find("#error-button").trigger("click");
    const store = useUserNotificationStore();
    expect(store.userNotifications.length).toStrictEqual(1);
    expect(store.userNotifications[0]).toStrictEqual(
      expect.objectContaining({
        message: "Fehler",
        category: UserNotificationCategoryEnum.FEHLER,
        id: expect.any(String),
      })
    );
  });

  test("should_addFehlerMessageToStore_when_fehlerButtonIsClicked", () => {
    const wrapper = mount(ExampleToastView, {
      global: {
        plugins: [pinia, vuetify],
      },
    });
    wrapper.find("#error-button").trigger("click");
    wrapper.find("#error-button").trigger("click");
    wrapper.find("#warning-button").trigger("click");
    wrapper.find("#success-button").trigger("click");
    const store = useUserNotificationStore();
    expect(store.userNotifications.length).toStrictEqual(4);
  });
});
