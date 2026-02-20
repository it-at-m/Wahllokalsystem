// Composables
import { createRouter, createWebHashHistory } from "vue-router";

import {
  ROUTES_GETSTARTED,
  ROUTES_HOME,
  ROUTES_INIT_WAHLTAG,
} from "@/constants";
import GetStartedView from "@/views/GetStartedView.vue";
import HomeView from "@/views/HomeView.vue";
import InitWahltagView from "@/views/InitWahltagView.vue";

const routes = [
  {
    path: "/",
    name: ROUTES_HOME,
    component: HomeView,
    meta: {},
  },
  {
    path: "/getstarted",
    name: ROUTES_GETSTARTED,
    component: GetStartedView,
  },
  {
    path: "/initWahltag",
    name: ROUTES_INIT_WAHLTAG,
    component: InitWahltagView,
  },
  { path: "/:catchAll(.*)*", redirect: "/" }, // CatchAll route
];

const router = createRouter({
  history: createWebHashHistory("/admintool"),
  routes,
  scrollBehavior() {
    return {
      top: 0,
      left: 0,
    };
  },
});

export default router;
