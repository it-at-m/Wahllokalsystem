import { WLS_SERVICE_API_BASEPATH_NAME } from "@/constants.ts";

export const REGEX_MATCH_BACKEND_API_CALLS = new RegExp(
  `^https?://[^/]+/${WLS_SERVICE_API_BASEPATH_NAME}(/|$)`
);
