// .vitepress/theme/index.ts
import type { Theme } from "vitepress";

import DefaultTheme from "vitepress/theme";

import vuetify from "./plugins/vuetify";

import "./custom.css";
import "vuetify/styles";

import status from "../components/adr/status.vue";
import statusOverview from "../components/adr/status/overview.vue";
import architectureTheServiceRelationVisualizerDiv from "../components/architecture/TheServiceRelationVisualizerDiv.vue";
import MermaidDiagram from "../components/MermaidDiagram.vue";

export default {
  extends: DefaultTheme,
  enhanceApp({ app }) {
    app.use(vuetify);

    // register your custom global components
    app.component("adrStatus", status);
    app.component("statusOverview", statusOverview);
    app.component(
      "architectureTheServiceRelationVisualizerDiv",
      architectureTheServiceRelationVisualizerDiv
    );
    app.component("mermaidDiagram", MermaidDiagram);
  },
} satisfies Theme;
