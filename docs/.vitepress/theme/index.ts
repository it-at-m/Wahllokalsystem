// .vitepress/theme/index.ts
import type {Theme} from "vitepress";
import DefaultTheme from "vitepress/theme";

import status from "../components/adr/status.vue";
import statusOverview from "../components/adr/status/overview.vue";

export default {
  extends: DefaultTheme,
  enhanceApp({ app }) {
    // register your custom global components
    app.component("adrStatus", status);
    app.component("statusOverview", statusOverview);
  },
} satisfies Theme;
