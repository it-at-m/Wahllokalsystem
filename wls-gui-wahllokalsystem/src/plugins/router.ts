// Composables
import { createRouter, createWebHashHistory } from "vue-router";

import ExampleDynamicComponent from "@/components/ExampleDynamicComponent.vue";
import {
  EXAMPLE_ROUTES_DYNAMIC,
  EXAMPLE_ROUTES_NEWROUTE,
  EXAMPLE_ROUTES_NOTFOUND,
  ROUTES_HOME,
} from "@/constants";
import ExampleError404View from "@/views/ExampleError404View.vue";
import ExampleNewRouteView from "@/views/ExampleNewRouteView.vue";
import HomeView from "@/views/HomeView.vue";

const routes = [
  {
    path: "/",
    name: ROUTES_HOME,
    component: HomeView,
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
