import type { Waehleranzahl } from "@/types/monitoring/Waehleranzahl.ts";

import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";

import { useMonitoringStore } from "@/stores/monitoringStore.ts";
import { useUserStore } from "@/stores/userStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlbeteiligung: vi.fn(),
  postWahlbeteiligung: vi.fn(),
}));

vi.mock(
  import("@/composables/monitoring/monitoringService.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useMonitoringService: () => ({
        ...mod.useMonitoringService(),
        getWahlbeteiligung: mockDefinitions.getWahlbeteiligung,
        postWahlbeteiligung: mockDefinitions.postWahlbeteiligung,
      }),
    };
  }
);

const mockedNow = new Date();
const { prepareUser } = useUserTestDataFactory();
const { generateRandomString } = useCommonTestDataFactory();

describe("monitoringStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useMonitoringStore>;
  let userStore: ReturnType<typeof useUserStore>;

  beforeEach(() => {
    const testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
    unitUnderTest = useMonitoringStore(testPinia);
    userStore = useUserStore(testPinia);

    vi.useFakeTimers({
      now: mockedNow,
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  describe("increaseWaehlerByOne", () => {
    it("should_addOneWaehler_when_triggered", () => {
      unitUnderTest.waehler = 10;
      unitUnderTest.increaseWaehlerByOne();

      expect(unitUnderTest.waehler).toBe(11);
    });

    it("should_notCountAbove9999_when_triggered", () => {
      unitUnderTest.waehler = 9999;
      unitUnderTest.increaseWaehlerByOne();

      expect(unitUnderTest.waehler).toBe(9999);
    });
  });

  describe("loadWaehler", () => {
    it("should_loadWaehleranzahl_when_userHasWahlbezirkIDAndHauptWahlID", async () => {
      userStore.setUser(prepareUser().wahlbezirkID("ich bin eine id").build());
      // @ts-expect-error: cannot set readonly
      userStore.currentUserHauptWahlID = generateRandomString(10);

      const mockedWaehler = 3;
      const mockedWaehleranzahl: Waehleranzahl = {
        anzahlWaehler: mockedWaehler,
        uhrzeit: mockedNow,
      };
      mockDefinitions.getWahlbeteiligung.mockReturnValue(mockedWaehleranzahl);

      await unitUnderTest.loadWaehler();

      await nextTick();

      expect(unitUnderTest.waehler).toStrictEqual(
        mockedWaehleranzahl.anzahlWaehler
      );
    });
  });

  describe("sendWaehler", () => {
    it("should_sendWaehleranzahl_when_userHasWahlbezirkIDAndHauptWahlID", async () => {
      userStore.setUser(prepareUser().wahlbezirkID("ich bin eine id").build());
      // @ts-expect-error: cannot set readonly
      userStore.currentUserHauptWahlID = generateRandomString(10);

      const mockedWahlbeteiligung = 17;

      unitUnderTest.waehler = mockedWahlbeteiligung;
      mockDefinitions.postWahlbeteiligung.mockReturnValue(Promise.resolve());

      await unitUnderTest.sendWaehler();

      await nextTick();

      expect(unitUnderTest.waehler).toStrictEqual(mockedWahlbeteiligung);
      expect(mockDefinitions.postWahlbeteiligung).toHaveBeenCalledWith(
        userStore.currentUserWahlbezirkID,
        userStore.currentUserHauptWahlID,
        mockedWahlbeteiligung
      );
    });
  });
});
