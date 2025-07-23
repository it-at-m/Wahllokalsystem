import { storeToRefs } from "pinia";
import { createRouter, createWebHashHistory } from "vue-router";

import {
  EXAMPLE_ROUTES_NOTFOUND,
  ROUTE_BEGINN_STIMMABGABE,
  ROUTE_EREIGNISSE,
  ROUTE_WAHLBRIEFE_ZULASSEN,
  ROUTE_WAHLSCHLIESSUNG,
  ROUTE_WAHLUMGEBUNG,
  ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS,
  ROUTE_WAHLVORSTAND,
  ROUTES_HOME,
} from "@/constants";
import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";
import EreignisseView from "@/views/EreignisseView.vue";
import ExampleError404View from "@/views/ExampleError404View.vue";
import HomeView from "@/views/HomeView.vue";
import UWBWaehlerverzeichnisView from "@/views/wahlvorbereitung/UWBWaehlerverzeichnisView.vue";
import UWBWahlhandlungView from "@/views/wahlvorbereitung/UWBWahlhandlungView.vue";
import WahlbriefZulassungView from "@/views/wahlvorbereitung/WahlbriefZulassungView.vue";
import WahleroeffnungView from "@/views/wahlvorbereitung/WahleroeffnungView.vue";
import WahlumgebungView from "@/views/wahlvorbereitung/WahlumgebungView.vue";
import WahlvorstandAnwesenheitView from "@/views/WahlvorstandAnwesenheitView.vue";

const permitNavigationOnlyForWahlbezirksArtUwb = () => {
  const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());
  return currentUserWahlbezirksArt.value === WahlbezirksArtEnum.UWB;
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
    path: "/wahlschliessung",
    name: ROUTE_WAHLSCHLIESSUNG,
    component: UWBWahlhandlungView,
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
    path: "/waehlerverzeichnis",
    name: ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS,
    component: UWBWaehlerverzeichnisView,
    beforeEnter: permitNavigationOnlyForWahlbezirksArtUwb,
  },
  {
    path: "/wahlbriefzulassung",
    name: ROUTE_WAHLBRIEFE_ZULASSEN,
    component: WahlbriefZulassungView,
  },
  {
    path: "/ereignisse",
    name: ROUTE_EREIGNISSE,
    component: EreignisseView,
    meta: {},
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
  const { hasInitializationOfTasksCompletelyRun } = storeToRefs(
    useTaskManagerStore()
  );

  if (to.name != ROUTES_HOME && !hasInitializationOfTasksCompletelyRun.value) {
    return { name: ROUTES_HOME };
  }
});

export default router;
