import type { RouteRecordRaw } from "vue-router";

export type RouteRecordRawWithoutName = Omit<RouteRecordRaw, "name">;
