import { describe, expect, it } from "vitest";

import { REGEX_MATCH_BACKEND_API_CALLS } from "@/service-worker/wahlWorkerConstants.ts";

describe("wahlWorkerConstants.ts", () => {
  describe("REGEX_MATCH_BACKEND_API_CALLS", () => {
    it.each([
      "http://localhost/api/backend-service/resouce",
      "http://localhost:123/api/backend-service/resouce",
      "https://localhost/api/backend-service/resouce",
      "https://localhost:123/api/backend-service/resouce",
    ])("should_match_when_urlIsForBackendService", (url) => {
      expect(REGEX_MATCH_BACKEND_API_CALLS.test(url)).toBe(true);
    });

    it.each([
      "http://localhost/backend-service/resouce",
      "http://localhost:123/backend-service/resouce",
      "https://localhost/backend-service/resouce",
      "https://localhost:123/backend-service/resouce",
      "http://localhost/src/wls-clients/api/backend-service/resouce",
      "https://localhost:123/src/wls-clients/api/backend-service/resouce",
    ])("should_match_when_urlIsForBackendService", (url) => {
      expect(REGEX_MATCH_BACKEND_API_CALLS.test(url)).toBe(false);
    });
  });
});
