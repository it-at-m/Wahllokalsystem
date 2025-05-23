import { storeToRefs } from "pinia";
import { createRouter, createWebHashHistory } from "vue-router";

import ExampleDynamicComponent from "@/components/ExampleDynamicComponent.vue";
import {
  EXAMPLE_ROUTES_DYNAMIC,
  EXAMPLE_ROUTES_NEWROUTE,
  EXAMPLE_ROUTES_NOTFOUND,
  EXAMPLE_VALIDATION,
  PRINT_EXAMPLE,
  ROUTE_BEGINN_STIMMABGABE,
  ROUTE_EREIGNISSE,
  ROUTE_WAHLSCHLIESSUNG,
  ROUTE_WAHLVORSTAND,
  ROUTES_HOME,
  TOAST,
} from "@/constants";
import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";
import EreignisseView from "@/views/EreignisseView.vue";
import ExampleError404View from "@/views/ExampleError404View.vue";
import ExampleNewRouteView from "@/views/ExampleNewRouteView.vue";
import ExamplePrintView from "@/views/ExamplePrintView.vue";
import ExampleToastView from "@/views/ExampleToastView.vue";
import ExampleValidation from "@/views/ExampleValidation.vue";
import HomeView from "@/views/HomeView.vue";
import WahleroeffnungView from "@/views/wahlvorbereitung/WahleroeffnungView.vue";
import WahlschliessungView from "@/views/wahlvorbereitung/WahlschliessungView.vue";
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
    path: "/beginnStimmabgabe",
    name: ROUTE_BEGINN_STIMMABGABE,
    component: WahleroeffnungView,
  },
  {
    path: "/ereignisse",
    name: ROUTE_EREIGNISSE,
    component: EreignisseView,
    meta: {},
  },
  {
    path: "/newroute",
    name: EXAMPLE_ROUTES_NEWROUTE,
    component: ExampleNewRouteView,
  },
  {
    path: "/dynamic/:wahlid",
    name: EXAMPLE_ROUTES_DYNAMIC,
    component: ExampleDynamicComponent,
  },
  {
    path: "/:catchAll(.*)*",
    name: EXAMPLE_ROUTES_NOTFOUND,
    component: ExampleError404View,
  }, // CatchAll route
  {
    path: "/validation-example",
    name: EXAMPLE_VALIDATION,
    component: ExampleValidation,
  },
  {
    path: "/toast-example",
    name: TOAST,
    component: ExampleToastView,
  },
  {
    path: "/print-example",
    name: PRINT_EXAMPLE,
    component: ExamplePrintView,
  },
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
