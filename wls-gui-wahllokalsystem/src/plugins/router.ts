import { storeToRefs } from "pinia";
import { createRouter, createWebHashHistory } from "vue-router";

import { useNavigationGuards } from "@/composables/navigation/navigationGuards.ts";
import {
  ROUTE_BEGINN_STIMMABGABE,
  ROUTE_EREIGNISSE,
  ROUTE_ERFASSUNG_WAHLBRIEFE,
  ROUTE_LOGOUT,
  ROUTE_NOTFOUND,
  ROUTE_STAPEL_A,
  ROUTE_STAPEL_B,
  ROUTE_STAPEL_C,
  ROUTE_STIMMABGABE,
  ROUTE_STIMMABGABEVERMERKE,
  ROUTE_WAHLBRIEFE_ZULASSEN,
  ROUTE_WAHLSCHEINE,
  ROUTE_WAHLUMGEBUNG,
  ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS,
  ROUTE_WAHLVORSTAND,
  ROUTES_HOME,
} from "@/constants";
import { mbwRouteDefinitions } from "@/plugins/router/mbwRoutes.ts";
import { useInitTaskManagerStore } from "@/stores/initTaskManagerStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import BWBWahlscheineView from "@/views/BWBWahlscheineView.vue";
import EreignisseView from "@/views/EreignisseView.vue";
import OBWStapelBView from "@/views/ergebnismeldung/OBW/OBWStapelBView.vue";
import OWBStapelAView from "@/views/ergebnismeldung/OBW/OWBStapelAView.vue";
import StapelCView from "@/views/ergebnismeldung/OBW/StapelCView.vue";
import ExampleError404View from "@/views/ExampleError404View.vue";
import HomeView from "@/views/HomeView.vue";
import LogoutSuccessView from "@/views/LogoutSuccessView.vue";
import UWBStimmabgabevermerkeView from "@/views/UWBStimmabgabevermerkeView.vue";
import BWBWahlbriefErfassungView from "@/views/wahlhandlung/BWBWahlbriefErfassungView.vue";
import BwbWahlbriefZulassungView from "@/views/wahlhandlung/BWBWahlbriefZulassungView.vue";
import UWBStimmabgabeView from "@/views/wahlhandlung/UWBStimmabgabeView.vue";
import UWBWaehlerverzeichnisView from "@/views/wahlhandlung/UWBWaehlerverzeichnisView.vue";
import WahleroeffnungView from "@/views/wahlhandlung/WahleroeffnungView.vue";
import WahlumgebungView from "@/views/wahlhandlung/WahlumgebungView.vue";
import WahlvorstandAnwesenheitView from "@/views/WahlvorstandAnwesenheitView.vue";

const {
  permitNavigationWhenWahlumgebungIsErfasst,
  permitNavigationOnlyForWahlbezirksArtUwb,
  permitNavigationOnlyForWahlbezirksArtBwb,
  permitNavigationOnlyIfUserIsLoggedOut,
  permitNavigationWhenWahleroeffnungIsErfasst,
  permitNavigationWhenWahlbriefeErfassenIsErfasst,
  permitNavigationWhenWahlbriefeZulassenIsErfasst,
  permitNavigationWhenWahlvorstandIsErfasst,
  permitNavigationWhenWaehlerverzeichnisIsErfasst,
  permitNavigationWhenStimmabgabeIsErfasst,
  beforeEnterBeginnStimmabgabe,
  beforeEnterWahlumgebung,
} = useNavigationGuards();

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
    beforeEnter: [
      permitNavigationOnlyForWahlbezirksArtUwb,
      permitNavigationWhenWahlvorstandIsErfasst,
      permitNavigationWhenWahlumgebungIsErfasst,
      permitNavigationWhenWahleroeffnungIsErfasst,
    ],
  },
  {
    path: "/wahlumgebung",
    name: ROUTE_WAHLUMGEBUNG,
    component: WahlumgebungView,
    beforeEnter: beforeEnterWahlumgebung,
  },
  {
    path: "/beginnStimmabgabe",
    name: ROUTE_BEGINN_STIMMABGABE,
    component: WahleroeffnungView,
    beforeEnter: beforeEnterBeginnStimmabgabe,
  },
  {
    path: "/erfassungWahlbriefe",
    name: ROUTE_ERFASSUNG_WAHLBRIEFE,
    component: BWBWahlbriefErfassungView,
    beforeEnter: [
      permitNavigationOnlyForWahlbezirksArtBwb,
      permitNavigationWhenWahlvorstandIsErfasst,
      permitNavigationWhenWahleroeffnungIsErfasst,
      permitNavigationWhenWahlumgebungIsErfasst,
    ],
  },
  {
    path: "/waehlerverzeichnis",
    name: ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS,
    component: UWBWaehlerverzeichnisView,
    beforeEnter: [
      permitNavigationOnlyForWahlbezirksArtUwb,
      permitNavigationWhenWahlvorstandIsErfasst,
      permitNavigationWhenWahlumgebungIsErfasst,
    ],
  },
  {
    path: "/wahlbriefzulassung",
    name: ROUTE_WAHLBRIEFE_ZULASSEN,
    component: BwbWahlbriefZulassungView,
    beforeEnter: [
      permitNavigationOnlyForWahlbezirksArtBwb,
      permitNavigationWhenWahlvorstandIsErfasst,
      permitNavigationWhenWahleroeffnungIsErfasst,
      permitNavigationWhenWahlumgebungIsErfasst,
      permitNavigationWhenWahlbriefeErfassenIsErfasst,
    ],
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
    beforeEnter: [
      permitNavigationOnlyForWahlbezirksArtUwb,
      permitNavigationWhenWahlvorstandIsErfasst,
      permitNavigationWhenWahlumgebungIsErfasst,
      permitNavigationWhenWaehlerverzeichnisIsErfasst,
      permitNavigationWhenWahleroeffnungIsErfasst,
      permitNavigationWhenStimmabgabeIsErfasst,
    ],
  },
  {
    path: "/wahlscheine",
    name: ROUTE_WAHLSCHEINE,
    component: BWBWahlscheineView,
    beforeEnter: [
      permitNavigationOnlyForWahlbezirksArtBwb,
      permitNavigationWhenWahlvorstandIsErfasst,
      permitNavigationWhenWahleroeffnungIsErfasst,
      permitNavigationWhenWahlumgebungIsErfasst,
      permitNavigationWhenWahlbriefeErfassenIsErfasst,
      permitNavigationWhenWahlbriefeZulassenIsErfasst,
    ],
  },
  {
    path: "/OBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/stapelA",
    name: ROUTE_STAPEL_A,
    component: OWBStapelAView,
  },
  {
    path: "/OBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/stapelB",
    name: ROUTE_STAPEL_B,
    component: OBWStapelBView,
  },
  {
    path: "/OBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/stapelC",
    name: ROUTE_STAPEL_C,
    component: StapelCView,
  },
  ...mbwRouteDefinitions,
  {
    path: "/logout",
    name: ROUTE_LOGOUT,
    component: LogoutSuccessView,
    beforeEnter: permitNavigationOnlyIfUserIsLoggedOut,
  },
  {
    path: "/notFound",
    name: ROUTE_NOTFOUND,
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
  const { hasTasksToRun, hasAllTasksRun } = storeToRefs(
    useInitTaskManagerStore()
  );
  const { isUserLoggedIn } = storeToRefs(useUserStore());
  if (
    to.name !== ROUTES_HOME &&
    (!hasTasksToRun.value || !hasAllTasksRun.value)
  ) {
    return { name: ROUTES_HOME };
  }

  if (to.name !== ROUTE_LOGOUT && !isUserLoggedIn.value) {
    return { name: ROUTE_LOGOUT };
  }
});

export default router;
