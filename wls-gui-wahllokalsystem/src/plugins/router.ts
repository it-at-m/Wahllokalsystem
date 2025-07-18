import { storeToRefs } from "pinia";
import { createRouter, createWebHashHistory } from "vue-router";

import {
  EXAMPLE_ROUTES_NOTFOUND,
  ROUTE_BEGINN_STIMMABGABE,
  ROUTE_EREIGNISSE,
  ROUTE_ERFASSUNG_WAHLBRIEFE,
  ROUTE_WAHLSCHLIESSUNG,
  ROUTE_WAHLUMGEBUNG,
  ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS,
  ROUTE_WAHLVORSTAND,
  ROUTES_HOME,
} from "@/constants";
import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";
import EreignisseView from "@/views/EreignisseView.vue";
import ExampleError404View from "@/views/ExampleError404View.vue";
import HomeView from "@/views/HomeView.vue";
import WaehlerverzeichnisView from "@/views/wahlvorbereitung/WaehlerverzeichnisView.vue";
import WahlbriefErfassungView from "@/views/wahlvorbereitung/WahlbriefErfassungView.vue";
import WahleroeffnungView from "@/views/wahlvorbereitung/WahleroeffnungView.vue";
import WahlschliessungView from "@/views/wahlvorbereitung/WahlschliessungView.vue";
import WahlumgebungView from "@/views/wahlvorbereitung/WahlumgebungView.vue";
import WahlvorstandAnwesenheitView from "@/views/WahlvorstandAnwesenheitView.vue";

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
    component: WahlschliessungView,
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
    component: WahlbriefErfassungView,
  },
  {
    path: "/waehlerverzeichnis",
    name: ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS,
    component: WaehlerverzeichnisView,
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
