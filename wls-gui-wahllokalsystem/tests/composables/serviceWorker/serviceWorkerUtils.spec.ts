import { useServiceWorkerTestDataFactory } from "@tests/utils/serviceWorker/serviceWorkerTestDataFactory.ts";
import {
  afterAll,
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";

import { useServiceWorkerUtils } from "@/composables/serviceWorker/serviceWorkerUtils.ts";

const mockDefinitions = vi.hoisted(() => ({
  postMessage: vi.fn(),
}));

Object.defineProperty(global.navigator, "serviceWorker", {
  value: {
    controller: {
      postMessage: mockDefinitions.postMessage,
    },
  },
  configurable: true,
});

const { createServiceWorkerMessage } = useServiceWorkerTestDataFactory();

describe("serviceWorkerUtils.ts", () => {
  let unitUnderTest: ReturnType<typeof useServiceWorkerUtils>;

  beforeEach(() => {
    unitUnderTest = useServiceWorkerUtils();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe("awaitServiceWorkerActive", () => {
    it("should_returnFalse_when_serviceWorkerControllerDoesNotExist", async () => {
      Object.defineProperty(global.navigator, "serviceWorker", {
        value: {
          controller: null,
        },
        configurable: true,
      });

      const result = await unitUnderTest.awaitServiceWorkerActive();
      expect(result).toStrictEqual(false);
    });

    it("should_returnTrue_when_serviceWorkerControllerAlreadyExists", async () => {
      Object.defineProperty(global.navigator, "serviceWorker", {
        value: {
          controller: {
            postMessage: mockDefinitions.postMessage,
          },
        },
        configurable: true,
      });

      const result = await unitUnderTest.awaitServiceWorkerActive();
      expect(result).toStrictEqual(true);
    });

    it("should_returnTrue_when_serviceWorkerWentActiveDuringWaits", async () => {
      vi.useFakeTimers();

      //start with missing service worker controller
      Object.defineProperty(global.navigator, "serviceWorker", {
        value: {
          controller: null,
        },
        configurable: true,
      });

      const numberOfTries = 4;
      const delayForEachTryInMilliseconds = 200;
      const waitForServiceWorkerPromise =
        unitUnderTest.awaitServiceWorkerActive(
          numberOfTries,
          delayForEachTryInMilliseconds
        );

      vi.advanceTimersByTime(delayForEachTryInMilliseconds);
      //service worker got online
      Object.defineProperty(global.navigator, "serviceWorker", {
        value: {
          controller: {
            postMessage: mockDefinitions.postMessage,
          },
        },
        configurable: true,
      });

      //advance to end of all tries
      vi.advanceTimersByTime(
        (numberOfTries - 1) * delayForEachTryInMilliseconds
      );
      const result = await waitForServiceWorkerPromise;
      expect(result).toStrictEqual(true);
      vi.useRealTimers();
    });
  });

  describe("isServiceWorkerActive", () => {
    it("should_returnTrue_when_serviceWorkerControllerExists", () => {
      Object.defineProperty(global.navigator, "serviceWorker", {
        value: {
          controller: {
            postMessage: mockDefinitions.postMessage,
          },
        },
        configurable: true,
      });

      expect(unitUnderTest.isServiceWorkerActive()).toStrictEqual(true);
    });

    it("should_returnFalse_when_serviceWorkerControllerDoesNotExists", () => {
      Object.defineProperty(global.navigator, "serviceWorker", {
        value: {
          controller: null,
        },
        configurable: true,
      });

      expect(unitUnderTest.isServiceWorkerActive()).toStrictEqual(false);
    });
  });

  describe("isServiceWorkerEnabled", () => {
    it("should_returnTrue_when_serviceWorkerExists", () => {
      Object.defineProperty(global.navigator, "serviceWorker", {
        value: true,
      });

      expect(unitUnderTest.isServiceWorkerEnabled()).toStrictEqual(true);
    });

    it("should_returnFalse_when_serviceWorkerDoesNotExists", () => {
      Object.defineProperty(global.navigator, "serviceWorker", {
        value: false,
      });

      expect(unitUnderTest.isServiceWorkerEnabled()).toStrictEqual(false);
    });
  });

  describe("sendMessage", () => {
    it("should_sendMessage_when_serviceWorkerControllerExists", () => {
      Object.defineProperty(global.navigator, "serviceWorker", {
        value: {
          controller: {
            postMessage: mockDefinitions.postMessage,
          },
        },
        configurable: true,
      });

      const messageToSend = createServiceWorkerMessage();
      unitUnderTest.sendMessage(messageToSend);

      expect(mockDefinitions.postMessage.mock.calls).toStrictEqual([
        [messageToSend],
      ]);
    });

    it("should_notSendMessage_when_serviceWorkerControllerDoesNotExists", () => {
      Object.defineProperty(global.navigator, "serviceWorker", {
        value: {
          controller: null,
        },
        configurable: true,
      });

      const messageToSend = createServiceWorkerMessage();
      unitUnderTest.sendMessage(messageToSend);

      expect(mockDefinitions.postMessage.mock.calls.length).toStrictEqual(0);
    });
  });
});
