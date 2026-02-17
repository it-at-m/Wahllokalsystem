import type { ServiceWorkerMessage } from "@/types/serviceWorker/ServiceWorkerMessage.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { flushPromises } from "@vue/test-utils";
import { createPinia, setActivePinia, storeToRefs } from "pinia";
import {
  afterAll,
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";

import { useServiceWorkerPinSyncer } from "@/composables/serviceWorker/serviceWorkerPinSyncer.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { ServiceWorkerMessageTypeEnum } from "@/types/serviceWorker/ServiceWorkerMessageTypeEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  addEventListener: vi.fn(),
  importKey: vi.fn(),
  sendMessage: vi.fn(),
}));

vi.mock("@/composables/crypto/cryptoUtils.ts", () => ({
  useCryptoUtils: () => ({
    importKey: mockDefinitions.importKey,
  }),
}));
vi.mock("@/composables/serviceWorker/serviceWorkerUtils.ts", () => ({
  useServiceWorkerUtils: () => ({
    sendMessage: mockDefinitions.sendMessage,
  }),
}));

const { generateRandomString } = useCommonTestDataFactory();
Object.defineProperty(global.navigator, "serviceWorker", {
  value: {
    addEventListener: mockDefinitions.addEventListener,
  },
  configurable: true,
});

describe("serviceWorkerPinSyncer.ts", () => {
  beforeEach(() => {
    setActivePinia(createPinia());
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.restoreAllMocks();
  });

  describe("watch", () => {
    it("should_sendPinMessageWithCryptoKey_when_pinOfUserChanged", async () => {
      const { user } = storeToRefs(useUserStore());

      useServiceWorkerPinSyncer();
      const mockedCryptoKey = generateRandomString(100) as unknown as CryptoKey;

      mockDefinitions.importKey.mockReturnValue(mockedCryptoKey);

      const pin = generateRandomString(10);
      user.value.pin = pin;

      await flushPromises();

      expect(mockDefinitions.importKey).toHaveBeenCalledWith(pin);

      const expectedSentMessage: ServiceWorkerMessage = {
        type: ServiceWorkerMessageTypeEnum.PIN,
        payload: mockedCryptoKey,
      };
      expect(mockDefinitions.sendMessage.mock.calls).toStrictEqual([
        [expectedSentMessage],
      ]);
    });
  });

  describe("syncPin", () => {
    it("should_sendPinMessageWithCryptoKey_when_syncPinIsCalled", async () => {
      const { user } = storeToRefs(useUserStore());

      const { syncPin } = useServiceWorkerPinSyncer();

      const mockedCryptoKey = generateRandomString(100) as unknown as CryptoKey;
      mockDefinitions.importKey.mockReturnValue(mockedCryptoKey);

      await syncPin();

      expect(mockDefinitions.importKey).toHaveBeenCalledWith(user.value.pin);

      const expectedSentMessage: ServiceWorkerMessage = {
        type: ServiceWorkerMessageTypeEnum.PIN,
        payload: mockedCryptoKey,
      };
      expect(mockDefinitions.sendMessage.mock.calls).toStrictEqual([
        [expectedSentMessage],
      ]);
    });
  });

  describe("onMessage", () => {
    it("should_updatePinOfUser_when_serviceWorkerInstalledMessageReceived", async () => {
      const { user } = storeToRefs(useUserStore());
      let eventHandler: (
        event: MessageEvent<ServiceWorkerMessage>
      ) => void = () => {
        return;
      };
      mockDefinitions.addEventListener.mockImplementation(
        (eventName, handler) => {
          if (eventName === "message") {
            eventHandler = handler;
          }
        }
      );

      const mockedCryptoKey = generateRandomString(100) as unknown as CryptoKey;
      mockDefinitions.importKey.mockReturnValue(mockedCryptoKey);
      useServiceWorkerPinSyncer();

      eventHandler({
        data: {
          type: ServiceWorkerMessageTypeEnum.SERVICE_WORKER_INSTALLED,
          payload: generateRandomString(100),
        },
      } as unknown as MessageEvent<ServiceWorkerMessage>);

      await flushPromises();

      expect(mockDefinitions.importKey).toHaveBeenCalledWith(user.value.pin);

      const expectedSentMessage: ServiceWorkerMessage = {
        type: ServiceWorkerMessageTypeEnum.PIN,
        payload: mockedCryptoKey,
      };
      expect(mockDefinitions.sendMessage.mock.calls).toStrictEqual([
        [expectedSentMessage],
      ]);
    });
  });

  describe("onControllerchange", () => {
    it("should_updatePinOfUser_when_serviceWorkerControllerChangedEventOccurred", async () => {
      const { user } = storeToRefs(useUserStore());
      let eventHandler: (
        event: MessageEvent<ServiceWorkerMessage>
      ) => void = () => {
        return;
      };
      mockDefinitions.addEventListener.mockImplementation(
        (eventName, handler) => {
          if (eventName === "controllerchange") {
            eventHandler = handler;
          }
        }
      );

      const mockedCryptoKey = generateRandomString(100) as unknown as CryptoKey;
      mockDefinitions.importKey.mockReturnValue(mockedCryptoKey);
      useServiceWorkerPinSyncer();

      eventHandler({
        data: {
          type: ServiceWorkerMessageTypeEnum.SERVICE_WORKER_INSTALLED,
          payload: generateRandomString(100),
        },
      } as unknown as MessageEvent<ServiceWorkerMessage>);

      await flushPromises();

      expect(mockDefinitions.importKey).toHaveBeenCalledWith(user.value.pin);

      const expectedSentMessage: ServiceWorkerMessage = {
        type: ServiceWorkerMessageTypeEnum.PIN,
        payload: mockedCryptoKey,
      };
      expect(mockDefinitions.sendMessage.mock.calls).toStrictEqual([
        [expectedSentMessage],
      ]);
    });
  });
});
