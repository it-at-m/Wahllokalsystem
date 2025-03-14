// Composables
import { createRouter, createWebHashHistory } from "vue-router";

import ExampleDynamicComponent from "@/components/ExampleDynamicComponent.vue";
import {
  EXAMPLE_ROUTES_BACKEND,
  EXAMPLE_ROUTES_DYNAMIC,
  EXAMPLE_ROUTES_NEWROUTE,
  EXAMPLE_ROUTES_NOTFOUND,
  EXAMPLE_VALIDATION,
  ROUTE_WAHLVORSTAND,
  ROUTES_HOME,
} from "@/constants";
import ExampleBackendCommunicationView from "@/views/ExampleBackendCommunicationView.vue";
import ExampleError404View from "@/views/ExampleError404View.vue";
import ExampleNewRouteView from "@/views/ExampleNewRouteView.vue";
import ExampleValidation from "@/views/ExampleValidation.vue";
import HomeView from "@/views/HomeView.vue";
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
    path: "/talk-to-backend",
    name: EXAMPLE_ROUTES_BACKEND,
    component: ExampleBackendCommunicationView,
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

export default router;
