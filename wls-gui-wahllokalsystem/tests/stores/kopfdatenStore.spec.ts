import type { WahlMetaData } from "@/types/wlsTypes/WahlMetaData.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useKopfdatenTestDataFactory } from "@tests/utils/kopfdaten/KopfdatenTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useKopfdatenStore } from "@/stores/kopfdatenStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const { createKopfdaten } = useKopfdatenTestDataFactory();
const { prepareUser } = useUserTestDataFactory();
const { generateRandomString } = useCommonTestDataFactory();

const mockDefinitions = vi.hoisted(() => ({
  getKopfdaten: vi.fn(),
}));

const mockWahlenDefinitions = vi.hoisted(() => ({
  getWahlOrUndefinedById: vi.fn(),
}));

vi.mock(import("@/composables/kopfdaten/kopfdatenService.ts"), () => ({
  useKopfdatenService: () => ({
    getKopfdaten: mockDefinitions.getKopfdaten,
  }),
}));

vi.mock(import("@/stores/wahlenStore.ts"), () => ({
  useWahlenStore: () => ({
    wahlenActions: {
      getWahlOrUndefinedById: mockWahlenDefinitions.getWahlOrUndefinedById,
    },
  }),
}));

describe("kopfdatenStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useKopfdatenStore>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useKopfdatenStore();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("initKopfdaten", () => {
    it("should_loadKopfdaten_when_calledWithValidWahlIdAndWahlbezirkId", async () => {
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
    });

    it("should_throwError_when_calledServiceThrowsError", async () => {
      const wahlMetaData: WahlMetaData = {
        wahlbezirkID: generateRandomString(10),
        wahlnummer: generateRandomString(10),
        wahlID: generateRandomString(10),
      };
      useUserStore().setUser(
        prepareUser().wahlMetaData([wahlMetaData]).build()
      );

      mockDefinitions.getKopfdaten.mockRejectedValue(
        new Error("service call failed")
      );

      await expect(unitUnderTest.initKopfdaten()).rejects.toThrow();
    });

    it("should_throwError_when_mbW_missing_maximalErlaubteStimmenProWaehler", async () => {
      const wahlMetaData: WahlMetaData = {
        wahlbezirkID: generateRandomString(10),
        wahlnummer: generateRandomString(10),
        wahlID: generateRandomString(10),
      };
      useUserStore().setUser(
        prepareUser().wahlMetaData([wahlMetaData]).build()
      );

      const mockedKopfdaten = { wahlID: wahlMetaData.wahlID };
      mockDefinitions.getKopfdaten.mockReturnValue(mockedKopfdaten);

      mockWahlenDefinitions.getWahlOrUndefinedById.mockReturnValue({
        wahlart: WahlWahlartEnum.Mbw,
      });

      await expect(unitUnderTest.initKopfdaten()).rejects.toThrow(
        "maximalErlaubteStimmenProWaehler"
      );
    });
  });
});
