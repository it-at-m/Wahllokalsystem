import { createApp } from "vue";

import App from "@/App.vue";
import { registerPlugins } from "@/plugins";

import "unfonts.css";
import "vue3-toastify/dist/index.css";

const app = createApp(App);

registerPlugins(app);

app.mount("#app");
