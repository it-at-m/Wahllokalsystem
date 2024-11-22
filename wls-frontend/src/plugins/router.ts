// Composables
import {createRouter, createWebHashHistory} from "vue-router";

import {ROUTES_404, ROUTES_GETSTARTED, ROUTES_HOME, ROUTES_NEWROUTE} from "@/constants";
import GetStartedView from "@/views/GetStartedView.vue";
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
    path: "/getstarted",
    name: ROUTES_GETSTARTED,
    component: GetStartedView,
  },
  {
    path: "/newroute",
    name: ROUTES_NEWROUTE,
    component: NewRouteView,
  },
  {
    path: "/404",
    name: ROUTES_404,
    component: Error404View,
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
