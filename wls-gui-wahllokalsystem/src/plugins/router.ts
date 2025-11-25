import { storeToRefs } from "pinia";
import { createRouter, createWebHashHistory } from "vue-router";

import {
  EXAMPLE_ROUTES_NOTFOUND,
  ROUTE_AUSZAEHLUNG_STIMMZETTEL,
  ROUTE_BEGINN_STIMMABGABE,
  ROUTE_EREIGNISSE,
  ROUTE_ERFASSUNG_WAHLBRIEFE,
  ROUTE_NIEDERSCHRIFT,
  ROUTE_SCHNELLMELDUNG,
  ROUTE_STAPEL_A,
  ROUTE_STAPEL_A_AND_B,
  ROUTE_STAPEL_B,
  ROUTE_STAPEL_BC,
  ROUTE_STAPEL_C,
  ROUTE_STAPEL_D,
  ROUTE_STIMMABGABE,
  ROUTE_STIMMABGABEVERMERKE,
  ROUTE_WAHLBRIEFE_ZULASSEN,
  ROUTE_WAHLSCHEINE,
  ROUTE_WAHLUMGEBUNG,
  ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS,
  ROUTE_WAHLVORSTAND,
  ROUTES_HOME,
} from "@/constants";
import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import ErfassungStimmzettelView from "@/views/auszaehlung/ErfassungStimmzettelView.vue";
import MBWNiederschriftView from "@/views/auszaehlung/mbw/MBWNiederschriftView.vue";
import MBWSchnellmeldungView from "@/views/auszaehlung/mbw/MBWSchnellmeldungView.vue";
import MBWStapelAandBView from "@/views/auszaehlung/mbw/MBWStapelAandBView.vue";
import MBWStapelBCView from "@/views/auszaehlung/mbw/MBWStapelBCView.vue";
import MBWStapelDView from "@/views/auszaehlung/mbw/MBWStapelDView.vue";
import StapelCView from "@/views/auszaehlung/obw/StapelCView.vue";
import ObwStapelBView from "@/views/auszaehlung/OBWStapelBView.vue";
import OWBStapelAView from "@/views/auszaehlung/OWBStapelAView.vue";
import BWBWahlscheineView from "@/views/BWBWahlscheineView.vue";
import EreignisseView from "@/views/EreignisseView.vue";
import ExampleError404View from "@/views/ExampleError404View.vue";
import HomeView from "@/views/HomeView.vue";
import UWBStimmabgabevermerkeView from "@/views/UWBStimmabgabevermerkeView.vue";
import BWBWahlbriefErfassungView from "@/views/wahlhandlung/BWBWahlbriefErfassungView.vue";
import BwbWahlbriefZulassungView from "@/views/wahlhandlung/BWBWahlbriefZulassungView.vue";
import UWBStimmabgabeView from "@/views/wahlhandlung/UWBStimmabgabeView.vue";
import UWBWaehlerverzeichnisView from "@/views/wahlhandlung/UWBWaehlerverzeichnisView.vue";
import WahleroeffnungView from "@/views/wahlhandlung/WahleroeffnungView.vue";
import WahlumgebungView from "@/views/wahlhandlung/WahlumgebungView.vue";
import WahlvorstandAnwesenheitView from "@/views/WahlvorstandAnwesenheitView.vue";

const permitNavigationOnlyForWahlbezirksArtUwb = () => {
  const { isUWB } = storeToRefs(useUserStore());
  return isUWB.value;
};

const permitNavigationOnlyForWahlbezirksArtBwb = () => {
  const { isBWB } = storeToRefs(useUserStore());
  return isBWB.value;
};

const routes = [
  {
    path: "/",
    name: ROUTES_HOME,
    component: HomeView,
    meta: {},
  },
  {
    path: "/wahlvorstand",
    name: ROUTE_WAHLVORSTAND,
    component: WahlvorstandAnwesenheitView,
    meta: {},
  },
  {
    path: "/stimmabgabe",
    name: ROUTE_STIMMABGABE,
    component: UWBStimmabgabeView,
    beforeEnter: permitNavigationOnlyForWahlbezirksArtUwb,
  },
  {
    path: "/wahlumgebung",
    name: ROUTE_WAHLUMGEBUNG,
    component: WahlumgebungView,
  },
  {
    path: "/beginnStimmabgabe",
    name: ROUTE_BEGINN_STIMMABGABE,
    component: WahleroeffnungView,
  },
  {
    path: "/erfassungWahlbriefe",
    name: ROUTE_ERFASSUNG_WAHLBRIEFE,
    component: BWBWahlbriefErfassungView,
    beforeEnter: permitNavigationOnlyForWahlbezirksArtBwb,
  },
  {
    path: "/waehlerverzeichnis",
    name: ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS,
    component: UWBWaehlerverzeichnisView,
    beforeEnter: permitNavigationOnlyForWahlbezirksArtUwb,
  },
  {
    path: "/wahlbriefzulassung",
    name: ROUTE_WAHLBRIEFE_ZULASSEN,
    component: BwbWahlbriefZulassungView,
    beforeEnter: permitNavigationOnlyForWahlbezirksArtBwb,
  },
  {
    path: "/ereignisse",
    name: ROUTE_EREIGNISSE,
    component: EreignisseView,
    meta: {},
  },
  {
    path: "/stimmabgabevermerke",
    name: ROUTE_STIMMABGABEVERMERKE,
    component: UWBStimmabgabevermerkeView,
    beforeEnter: permitNavigationOnlyForWahlbezirksArtUwb,
  },
  {
    path: "/wahlscheine",
    name: ROUTE_WAHLSCHEINE,
    component: BWBWahlscheineView,
    beforeEnter: permitNavigationOnlyForWahlbezirksArtBwb,
  },
  {
    path: "/:wahlart/wahl/:wahlId/wahlbezirk/:wahlbezirkId/auszaehlungStimmzettel",
    name: ROUTE_AUSZAEHLUNG_STIMMZETTEL,
    component: ErfassungStimmzettelView,
  },
  {
    path: "/OBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/stapelA",
    name: ROUTE_STAPEL_A,
    component: OWBStapelAView,
  },
  {
    path: "/OBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/stapelB",
    name: ROUTE_STAPEL_B,
    component: ObwStapelBView,
  },
  {
    path: "/OBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/stapelC",
    name: ROUTE_STAPEL_C,
    component: StapelCView,
  },
  {
    path: "/MBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/stapelAandB",
    name: ROUTE_STAPEL_A_AND_B,
    component: MBWStapelAandBView,
  },
  {
    path: "/MBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/stapelD",
    name: ROUTE_STAPEL_D,
    component: MBWStapelDView,
  },
  {
    path: "/MBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/schnellmeldung",
    name: ROUTE_SCHNELLMELDUNG,
    component: MBWSchnellmeldungView,
  },
  {
    path: "/MBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/stapelBC",
    name: ROUTE_STAPEL_BC,
    component: MBWStapelBCView,
  },
  {
    path: "/MBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/niederschrift",
    name: ROUTE_NIEDERSCHRIFT,
    component: MBWNiederschriftView,
  },
  {
    path: "/notFound",
    name: EXAMPLE_ROUTES_NOTFOUND,
    component: ExampleError404View,
  },
  {
    path: "/:catchAll(.*)*", //don't call that inside a cached component, it will cause trouble while unmounting
    component: ExampleError404View,
  }, // CatchAll route
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
  scrollBehavior() {
    return {
      top: 0,
      left: 0,
    };
  },
});

router.beforeEach((to) => {
  const { hasTasksToRun, hasAllTasksRun } = storeToRefs(useTaskManagerStore());
  if (
    to.name !== ROUTES_HOME &&
    (!hasTasksToRun.value || !hasAllTasksRun.value)
  ) {
    return { name: ROUTES_HOME };
  }
});

export default router;
