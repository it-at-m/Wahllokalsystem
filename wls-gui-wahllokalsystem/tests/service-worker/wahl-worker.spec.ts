import { afterEach, describe, expect, it, vi } from "vitest";

import "@/service-worker/wahl-worker.ts";

const mockDefinitions = vi.hoisted(() => ({
  registerRoute: vi.fn(),
}));

vi.mock("workbox-routing", () => ({
  registerRoute: mockDefinitions.registerRoute,
}));

vi.mock("localforage");

describe("wahl-worker.ts", () => {
  afterEach(() => {
    vi.clearAllMocks();
  });

  it("should_registerRequestHandler_when_serviceWorkerIsInitialised", () => {
    expect(mockDefinitions.registerRoute).toHaveBeenCalledTimes(2);

    const regex = new RegExp("/api/.+");

    const getCall = mockDefinitions.registerRoute.mock.calls.find(
      (call) => call[2] === "GET"
    );
    expect(getCall).toBeDefined();
    if (getCall != undefined) {
      expect(getCall[0]).toEqual(regex);
      expect(getCall[1].toString()).contains("getRequestHandler");
    }

    const postCall = mockDefinitions.registerRoute.mock.calls.find(
      (call) => call[2] === "POST"
    );
    expect(postCall).toBeDefined();
    if (postCall != undefined) {
      expect(postCall[0]).toEqual(regex);
      expect(postCall[1].toString()).contains("postRequestHandler");
    }
  });

  it("should_coverBasePath_when_regexIsUsed", () => {
    const basePathRegex = new RegExp("/api/.+");

    const testUrls = new Map<string, boolean>();
    testUrls.set("/api/test", true);
    testUrls.set("/api/test/123", true);
    testUrls.set("/api/", false);
    testUrls.set("/other/test", false);

    testUrls.forEach((expected, url) => {
      const matches = basePathRegex.test(url);
      expect(matches).toBe(expected);
    });
  });
});
