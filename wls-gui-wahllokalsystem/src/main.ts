import { createApp } from "vue";

import "vue3-toastify/dist/index.css";
import "@/styles/custom.css";

import App from "@/App.vue";
import { registerPlugins } from "@/plugins";

const app = createApp(App);

registerPlugins(app);

app.mount("#app");
