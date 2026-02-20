import { createApp } from "vue";

import "vue3-toastify/dist/index.css";

import App from "@/App.vue";
import { registerPlugins } from "@/plugins";

import "@/styles/custom.css"; //!! loading our style as late as possible, cause last set definitions win

const app = createApp(App);

registerPlugins(app);

app.mount("#app");
