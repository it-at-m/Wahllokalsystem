import type { App } from "vue";

import Vue3Toastify from "vue3-toastify";

import pinia from "@/plugins/pinia";
import router from "@/plugins/router";
import vuetify from "@/plugins/vuetify";

export function registerPlugins(app: App) {
  app.use(pinia).use(vuetify).use(router).use(Vue3Toastify);
}
