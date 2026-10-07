import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmabgabevermerkeTestDataFactory } from "@tests/utils/stimmabgabevermerke/StimmabgabevermerkeTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useBWerteService } from "@/composables/ergebnismeldung/common/bWerteService.ts";
import pinia from "@/plugins/pinia.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlOrUndefinedById: vi.fn(),
  getWaehlerverzeichnisNummerOrUndefinedById: vi.fn(),
  getStimmabgabevermerke: vi.fn(),
  getStimmzettelumschlaege: vi.fn(),
}));

vi.mock(
  import("@/composables/ergebnismeldung/common/ergebnisService.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useErgebnisService: () => ({
        ...mod.useErgebnisService(),
        getStimmzettelumschlaege: mockDefinitions.getStimmzettelumschlaege,
      }),
    };
  }
);

vi.mock(
  "@/composables/stimmabgabevermerke/stimmabgabevermerkeService.ts",
  () => ({
    useStimmabgabevermerkeService: () => ({
      getStimmabgabevermerke: mockDefinitions.getStimmabgabevermerke,
    }),
  })
);

vi.mock("@/stores/wahlenStore.ts", () => ({
  useWahlenStore: () => ({
    wahlenActions: {
      getWahlOrUndefinedById: mockDefinitions.getWahlOrUndefinedById,
    },
    waehlerverzeichnisActions: {
      getWaehlerverzeichnisNummerOrUndefinedById:
        mockDefinitions.getWaehlerverzeichnisNummerOrUndefinedById,
    },
  }),
}));

const { generateRandomString } = useCommonTestDataFactory();
const { prepareUser } = useUserTestDataFactory();
const { prepareStimmabgabevermerke, prepareVermerk, prepareStimmzettel } =
  useStimmabgabevermerkeTestDataFactory();
const { prepareWahl } = useWahlTestDataFactory();

describe("bWerteService.ts", () => {
  let unitUnderTest: ReturnType<typeof useBWerteService>;

  const wahlID = generateRandomString(10);
  const wahlbezirkID = generateRandomString(10);

  beforeEach(() => {
    createTestingPinia({ createSpy: vi.fn, stubActions: false });
    unitUnderTest = useBWerteService(wahlID, wahlbezirkID);
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  describe("getBWerteForWahlbezirkAndWahl", () => {
    it("should_calculateBWerte_when_wahlbezirksartIsUWB", async () => {
      const userStore = useUserStore(pinia);
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );

      mockDefinitions.getWaehlerverzeichnisNummerOrUndefinedById.mockReturnValue(
        1
      );

      mockDefinitions.getStimmabgabevermerke.mockReturnValue(
        prepareStimmabgabevermerke()
          .eingenommeneWahlscheine(
            new Map([
              [StimmzettelStimmzettelartEnum.Klein, 1],
              [StimmzettelStimmzettelartEnum.Beide, 1],
            ])
          )
          .vermerke([
            prepareVermerk()
              .blattnummer(1)
              .stimmzettel([
                prepareStimmzettel()
                  .anzahl(1)
                  .stimmzettelart(StimmzettelStimmzettelartEnum.Klein)
                  .build(),
                prepareStimmzettel()
                  .anzahl(1)
                  .stimmzettelart(StimmzettelStimmzettelartEnum.Beide)
                  .build(),
              ])
              .build(),
            prepareVermerk()
              .blattnummer(2)
              .stimmzettel([
                prepareStimmzettel()
                  .anzahl(1)
                  .stimmzettelart(StimmzettelStimmzettelartEnum.Klein)
                  .build(),
              ])
              .build(),
          ])
          .build()
      );

      const result = await unitUnderTest.getBWerteForWahlbezirkAndWahl();

      expect(mockDefinitions.getStimmabgabevermerke).toHaveBeenCalledWith(
        wahlbezirkID,
        wahlID,
        1
      );
      expect(result.b1).toBe(3);
      expect(result.b2).toBe(2);
      expect(result.b).toBe(5);
    });

    it("should_calculateBWerteAs0_when_wahlbezirksartIsUWBAndStimmabgabevermerkeIsNull", async () => {
      const userStore = useUserStore(pinia);
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );

      mockDefinitions.getWaehlerverzeichnisNummerOrUndefinedById.mockReturnValue(
        1
      );

      mockDefinitions.getStimmabgabevermerke.mockReturnValue(null);

      const result = await unitUnderTest.getBWerteForWahlbezirkAndWahl();

      expect(mockDefinitions.getStimmabgabevermerke).toHaveBeenCalledWith(
        wahlbezirkID,
        wahlID,
        1
      );
      expect(result.b1).toBe(0);
      expect(result.b2).toBe(0);
      expect(result.b).toBe(0);
    });

    it("should_calculateOnlyValueB_when_wahlbezirksartIsBWB", async () => {
      const userStore = useUserStore(pinia);
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );

      const wahl = prepareWahl().wahlID(wahlID).build();

      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(wahl);

      mockDefinitions.getStimmzettelumschlaege.mockReturnValue({
        anzahlWaehler: 4,
      });

      const result = await unitUnderTest.getBWerteForWahlbezirkAndWahl();

      expect(mockDefinitions.getStimmzettelumschlaege).toHaveBeenCalledWith(
        wahl,
        wahlbezirkID,
        "",
        false
      );
      expect(result.b1).toBe(0);
      expect(result.b2).toBe(0);
      expect(result.b).toBe(4);
    });
  });
});
