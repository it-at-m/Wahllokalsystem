// Composables
import {createRouter, createWebHashHistory} from "vue-router";

import { ROUTES_HOME, ROUTES_NEWROUTE, ROUTES_NOTFOUND} from "@/constants";
import HomeView from "@/views/HomeView.vue";
import NewRouteView from "@/views/NewRouteView.vue";
import Error404View from "@/views/Error404View.vue";

const routes = [
  {
    path: "/",
    name: ROUTES_HOME,
    component: HomeView,
    meta: {},
  },
  {
    path: "/newroute",
    name: ROUTES_NEWROUTE,
    component: NewRouteView,
  },
  {
    path: "/:catchAll(.*)*",
    name: ROUTES_NOTFOUND,
    component: Error404View,
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
