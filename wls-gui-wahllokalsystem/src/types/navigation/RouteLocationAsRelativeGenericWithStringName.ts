import type { RouteLocationAsRelativeGeneric } from "vue-router";

export type RouteLocationAsRelativeGenericWithStringName =
  RouteLocationAsRelativeGeneric & { name: string };
