import { storeToRefs } from "pinia";
import { createRouter, createWebHashHistory } from "vue-router";

import {
  EXAMPLE_ROUTES_NOTFOUND,
  ROUTE_AUSZAEHLUNG_STIMMZETTEL,
  ROUTE_BEGINN_STIMMABGABE,
  ROUTE_EREIGNISSE,
  ROUTE_ERFASSUNG_WAHLBRIEFE,
  ROUTE_STAPEL_A,
  ROUTE_STAPEL_B,
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
    path: "/auszaehlungStimmzettel/:wahlId",
    name: ROUTE_AUSZAEHLUNG_STIMMZETTEL,
    component: ErfassungStimmzettelView,
  },
  {
    path: "/stapelA/:wahlId",
    name: ROUTE_STAPEL_A,
    component: OWBStapelAView,
  },
  {
    path: "/auszaehlungStapelB/:wahlId",
    name: ROUTE_STAPEL_B,
    component: ObwStapelBView,
  },
  {
    path: "/:catchAll(.*)*",
    name: EXAMPLE_ROUTES_NOTFOUND,
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
  const { hasAllTasksRunSuccessfully, hasTasksToRun } = storeToRefs(
    useTaskManagerStore()
  );

  if (
    to.name != ROUTES_HOME &&
    (!hasAllTasksRunSuccessfully.value || !hasTasksToRun.value)
  ) {
    return { name: ROUTES_HOME };
  }
});

export default router;
