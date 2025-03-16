import type { ToastContainerOptions } from "vue3-toastify";

import { createApp } from "vue";
import Vue3Toastify from "vue3-toastify";

import "vue3-toastify/dist/index.css";

import App from "@/App.vue";
import { registerPlugins } from "@/plugins";

const app = createApp(App).use(Vue3Toastify, {
  autoClose: 4000,
} as ToastContainerOptions);

registerPlugins(app);

app.mount("#app");
