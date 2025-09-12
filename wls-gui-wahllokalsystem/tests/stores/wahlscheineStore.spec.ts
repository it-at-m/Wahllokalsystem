import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useUserStore } from "@/stores/userStore.ts";
import {useWahlscheineStore} from "@/stores/wahlscheineStore.ts";
import {useWahlscheineTestDataFactory} from "@tests/utils/ergebnismeldung/wahlscheineTestDataFactory.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlscheine: vi.fn(),
  postWahlscheine: vi.fn(),
}));

vi.mock(
  "@/composables/ergebnismeldung/wahlscheineService.ts",
  () => ({
    useStimmabgabevermerkeService: () => ({
      getWahlscheine: mockDefinitions.getWahlscheine,
      postWahlscheine: mockDefinitions.postWahlscheine,
    }),
  })
);

describe("wahlscheineStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useWahlscheineStore>;

  const {
    createWahlscheine,
    prepareWahlscheine,
  } = useWahlscheineTestDataFactory();
  const { generateRandomString, generateRandomNumber } =
    useCommonTestDataFactory();
  const { prepareUser } = useUserTestDataFactory();

  beforeEach(() => {
    const testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
    unitUnderTest = useWahlscheineStore(testPinia);
  });

  describe("loadWahlscheine", () => {
    it("should_addWahlscheineeToState_when_serviceReturnsData", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      const existingWahlscheine = createWahlscheine();
      unitUnderTest.wahlscheine = [existingWahlscheine];

      const mockedServiceWahlscheine = createWahlscheine();
      mockDefinitions.getWahlscheine.mockResolvedValue(
        mockedServiceWahlscheine
      );

      await unitUnderTest.loadWahlscheine(
        wahlID,
        wahlbezirkID
      );

      expect(unitUnderTest.wahlscheine).toStrictEqual([
        existingWahlscheine,
        mockedServiceWahlscheine,
      ]);
    });

    /*it("should_addDefaultStimmabgabevermerkeToState_when_serviceReturnsNoDataAndUserHasWahlbezirkID", async () => {
      const wahlbezirkID = generateRandomString(10);
      const waehlerverzeichnisNummer = generateRandomNumber(3);

      const existingStimmabgabevermerke = createStimmabgabevermerke();
      unitUnderTest.stimmabgabevermerke = [existingStimmabgabevermerke];

      mockDefinitions.getStimmabgabevermerke.mockResolvedValue(null);
      const mockedEmptyStimmabgabevermerke = createStimmabgabevermerke();
      mockDefinitions.createEmptyStimmabgabevermerke.mockReturnValue(
        mockedEmptyStimmabgabevermerke
      );

      useUserStore().setUser(
        prepareUser()
          .wahlMetaData([
            {
              wahlbezirkID: wahlbezirkID,
              wahlnummer: generateRandomString(1),
              wahlID: generateRandomString(10),
            },
          ])
          .build()
      );

      await unitUnderTest.loadStimmabgabevermerke(
        wahlbezirkID,
        waehlerverzeichnisNummer
      );

      expect(unitUnderTest.stimmabgabevermerke).toStrictEqual([
        existingStimmabgabevermerke,
        mockedEmptyStimmabgabevermerke,
      ]);
    });

    it("should_notAddDefaultStimmabgabevermerkeToState_when_serviceReturnsNoDataButUserHasNotThatWahlbezirkID", async () => {
      const wahlbezirkID = generateRandomString(10);
      const waehlerverzeichnisNummer = generateRandomNumber(3);

      const existingStimmabgabevermerke = createStimmabgabevermerke();
      unitUnderTest.stimmabgabevermerke = [existingStimmabgabevermerke];

      mockDefinitions.postStimmabgabevermerke.mockResolvedValue(null);
      const mockedEmptyStimmabgabevermerke = createStimmabgabevermerke();
      mockDefinitions.createEmptyStimmabgabevermerke.mockReturnValue(
        mockedEmptyStimmabgabevermerke
      );

      useUserStore().setUser(
        prepareUser()
          .wahlMetaData([
            {
              wahlbezirkID: wahlbezirkID + wahlbezirkID,
              wahlnummer: generateRandomString(1),
              wahlID: generateRandomString(10),
            },
          ])
          .build()
      );

      await unitUnderTest.loadStimmabgabevermerke(
        wahlbezirkID,
        waehlerverzeichnisNummer
      );

      expect(unitUnderTest.stimmabgabevermerke).toStrictEqual([
        existingStimmabgabevermerke,
      ]);
    });*/
  });

  describe("saveWahlscheine", () => {
    it("should_saveWahlscheine_when_called", async () => {
      const wahlscheine = createWahlscheine();

      mockDefinitions.postWahlscheine.mockReturnValue(
        Promise.resolve(null)
      );

      unitUnderTest.wahlscheine = [wahlscheine];

      await unitUnderTest.saveWahlscheine();

      expect(mockDefinitions.postWahlscheine).toHaveBeenCalledWith(
        wahlscheine.bezirkUndWahlID.wahlID,
        wahlscheine.bezirkUndWahlID.wahlbezirkID,
        wahlscheine
      );
    });
  });
});
