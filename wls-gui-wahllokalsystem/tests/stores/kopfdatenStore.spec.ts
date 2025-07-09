import type { WahlMetaData } from "@/types/wlsTypes/WahlMetaData.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useKopfdatenTestDataFacotry } from "@tests/utils/kopfdaten/KopfdatenTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useKopfdatenStore } from "@/stores/kopfdatenStore.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { createKopfdaten } = useKopfdatenTestDataFacotry();
const { prepareUser } = useUserTestDataFactory();
const { generateRandomString } = useCommonTestDataFactory();

const mockDefinitions = vi.hoisted(() => ({
  getKopfdaten: vi.fn(),
}));

vi.mock("@/composables/kopfdaten/kopfdatenService.ts", () => ({
  useKopfdatenService: () => ({
    getKopfdaten: mockDefinitions.getKopfdaten,
  }),
}));

describe("kopfdatenService.ts", () => {
  let unitUnderTest: ReturnType<typeof useKopfdatenStore>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useKopfdatenStore();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("initKopfdaten", () => {
    it("should_loadWahlen_when_calledWithValidWahlIdAndWahlbezirkId", async () => {
      const wahlMetaData: WahlMetaData = {
        wahlbezirkID: generateRandomString(10),
        wahlnummer: generateRandomString(10),
        wahlID: generateRandomString(10),
      };
      useUserStore().setUser(
        prepareUser().wahlMetaData([wahlMetaData]).build()
      );
      const mockedKopfdaten = createKopfdaten();

      mockDefinitions.getKopfdaten.mockReturnValue(mockedKopfdaten);

      await unitUnderTest.initKopfdaten();

      expect(mockDefinitions.getKopfdaten.mock.calls).toStrictEqual([
        [wahlMetaData.wahlID, wahlMetaData.wahlbezirkID],
      ]);

      expect(unitUnderTest);
    });
  });
});
