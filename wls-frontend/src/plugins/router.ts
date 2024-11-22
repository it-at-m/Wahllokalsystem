// Composables
import { createRouter, createWebHashHistory } from "vue-router";

import { ROUTES_HOME } from "@/constants";
import HomeView from "@/views/HomeView.vue";

const routes = [
  {
    path: "/",
    name: ROUTES_HOME,
    component: HomeView,
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
