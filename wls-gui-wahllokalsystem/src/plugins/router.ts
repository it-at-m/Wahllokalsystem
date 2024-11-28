// Composables
import { createRouter, createWebHashHistory } from "vue-router";

import { EXAMPLE_ROUTES_BACKEND, ROUTES_HOME } from "@/constants";
import ExampleBackendCommunicationView from "@/views/ExampleBackendCommunicationView.vue";
import HomeView from "@/views/HomeView.vue";

const routes = [
  {
    path: "/",
    name: ROUTES_HOME,
    component: HomeView,
    meta: {},
  },
  {
    path: "/talk-to-backend",
    name: EXAMPLE_ROUTES_BACKEND,
    component: ExampleBackendCommunicationView,
    meta: {},
  },
  { path: "/:catchAll(.*)*", redirect: "/" }, // CatchAll route
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
