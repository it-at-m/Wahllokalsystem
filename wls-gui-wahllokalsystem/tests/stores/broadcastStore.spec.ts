import { useBroadcastTestDataFactory } from "@tests/utils/broadcast/BroadcastTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useBroadcastStore } from "@/stores/broadcastStore.ts";
import { useUserStore } from "@/stores/userStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  deleteMessage: vi.fn(),
  getMessage: vi.fn(),
}));

vi.mock("@/composables/broadcast/broadcastService.ts", () => ({
  useBroadcastService: () => ({
    getMessage: mockDefinitions.getMessage,
    deleteMessage: mockDefinitions.deleteMessage,
  }),
}));

const { createBroadcastMessage } = useBroadcastTestDataFactory();
const { prepareUser } = useUserTestDataFactory();

describe("broadcastStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useBroadcastStore>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useBroadcastStore();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("loadLatestMessage", () => {
    it("should_setState_when_messageWasReceived", async () => {
      const userStore = useUserStore();
      userStore.setUser(prepareUser().wahlbezirkID("wahlbezirkID").build());

      const mockedBroadcastMessage = createBroadcastMessage();

      mockDefinitions.getMessage.mockResolvedValue(
        Promise.resolve(mockedBroadcastMessage)
      );

      await unitUnderTest.loadLatestMessage();

      expect(unitUnderTest.currentBroadcastNachricht).toStrictEqual(
        mockedBroadcastMessage
      );
    });
  });

  describe("markMessageAsReadAndLoadNextMessage", () => {
    it("should_deleteAndLoadNextMessage_when_broadcastMessageIdAndWahlbezirkIdIsGiven", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const broadcastMessageToDelete = createBroadcastMessage();
      unitUnderTest.currentBroadcastNachricht = broadcastMessageToDelete;

      const mockedBroadcastMessage = createBroadcastMessage();

      mockDefinitions.getMessage.mockResolvedValue(
        Promise.resolve(mockedBroadcastMessage)
      );

      await unitUnderTest.markMessageAsReadAndLoadNextMessage();

      expect(mockDefinitions.deleteMessage.mock.calls).toStrictEqual([
        [broadcastMessageToDelete.id],
      ]);
      expect(mockDefinitions.getMessage.mock.calls).toStrictEqual([
        [wahlbezirkID],
      ]);
    });

    it("should_notTriggerDeleteOperation_when_noBroadcastMessageIsInState", async () => {
      unitUnderTest.currentBroadcastNachricht = null;

      await unitUnderTest.markMessageAsReadAndLoadNextMessage();

      expect(mockDefinitions.deleteMessage.mock.calls.length).toStrictEqual(0);
    });

    it("should_loadNextMessage_when_noBroadcastMessageIsInState", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      unitUnderTest.currentBroadcastNachricht = null;

      const mockedBroadcastMessage = createBroadcastMessage();

      mockDefinitions.getMessage.mockResolvedValue(
        Promise.resolve(mockedBroadcastMessage)
      );

      await unitUnderTest.markMessageAsReadAndLoadNextMessage();

      expect(mockDefinitions.getMessage.mock.calls).toStrictEqual([
        [wahlbezirkID],
      ]);
    });
  });
});
