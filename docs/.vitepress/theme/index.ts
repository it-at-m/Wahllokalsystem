// .vitepress/theme/index.ts
import type { Theme } from "vitepress";

import DefaultTheme from "vitepress/theme";

import vuetify from "./plugins/vuetify";

import "./custom.css";

import status from "../components/adr/status.vue";
import statusOverview from "../components/adr/status/overview.vue";
import architectureMd from "../components/architecture/md.vue";
import MermaidDiagram from "../components/MermaidDiagram.vue";

export default {
  extends: DefaultTheme,
  enhanceApp({ app }) {
    app.use(vuetify);

    // register your custom global components
    app.component("adrStatus", status);
    app.component("statusOverview", statusOverview);
    app.component("architectureMd", architectureMd);
    app.component("mermaidDiagram", MermaidDiagram);
  },
} satisfies Theme;
