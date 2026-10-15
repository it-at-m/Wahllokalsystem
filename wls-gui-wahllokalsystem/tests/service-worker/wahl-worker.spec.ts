import { afterAll, afterEach, describe, expect, it, vi } from "vitest";

import "@/service-worker/wahl-worker.ts";

import { flushPromises } from "@vue/test-utils";

import { REGEX_MATCH_BACKEND_API_CALLS } from "@/service-worker/wahlWorkerConstants.ts";
import { ServiceWorkerMessageTypeEnum } from "@/types/serviceWorker/ServiceWorkerMessageTypeEnum.ts";

const mockDefinitions = vi.hoisted(() => {
  const matchAll = vi.fn().mockImplementation(() => {
    return Promise.resolve([]);
  });
  vi.stubGlobal("clients", {
    matchAll,
  });

  return {
    registerRoute: vi.fn(),
    matchAll,
  };
});

vi.mock(import("workbox-routing"), () => ({
  registerRoute: mockDefinitions.registerRoute,
}));

vi.mock(import("localforage"));

describe("wahl-worker.ts", () => {
  const API_BASE_PATH_REGEX = REGEX_MATCH_BACKEND_API_CALLS;

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.unstubAllGlobals();
  });

  it("should_registerRequestHandler_when_serviceWorkerIsInitialised", () => {
    expect(mockDefinitions.registerRoute).toHaveBeenCalledTimes(2);

    const getCall = mockDefinitions.registerRoute.mock.calls.find(
      (call) => call[2] === "GET"
    );
    expect(getCall).toBeDefined();
    if (getCall != undefined) {
      expect(getCall[0]).toEqual(API_BASE_PATH_REGEX);
      expect(getCall[1].name).equals("getRequestHandler");
    }

    const postCall = mockDefinitions.registerRoute.mock.calls.find(
      (call) => call[2] === "POST"
    );
    expect(postCall).toBeDefined();
    if (postCall != undefined) {
      expect(postCall[0]).toEqual(API_BASE_PATH_REGEX);
      expect(postCall[1].name).equals("postRequestHandler");
    }
  });

  it("should_sendMessageToAllClients_when_startedAndClientsAreGiven", async () => {
    const clients = [
      createMockedClient(),
      createMockedClient(),
      createMockedClient(),
    ];
    mockDefinitions.matchAll.mockReturnValue(Promise.resolve(clients));
    vi.resetModules();

    vi.stubGlobal("clients", {
      matchAll: mockDefinitions.matchAll,
    });
    await import("@/service-worker/wahl-worker.ts");
    await flushPromises();

    const expectedMessage = {
      type: ServiceWorkerMessageTypeEnum.SERVICE_WORKER_INSTALLED,
      payload: undefined,
    };
    clients.forEach((client) => {
      expect(client.postMessage).toHaveBeenCalledWith(expectedMessage);
    });
  });

  it("should_sendNoMessageToClients_when_startedAndNoClientsAreGiven", async () => {
    const clients: unknown[] = [];
    mockDefinitions.matchAll.mockReturnValue(Promise.resolve(clients));
    vi.resetModules();

    vi.stubGlobal("clients", {
      matchAll: mockDefinitions.matchAll,
    });
    await import("@/service-worker/wahl-worker.ts");
    await flushPromises();
  });

  function createMockedClient() {
    return {
      postMessage: vi.fn(),
    };
  }
});
