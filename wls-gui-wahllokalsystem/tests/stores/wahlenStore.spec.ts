import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlen: vi.fn(),
}));

vi.mock("@/composables/wahl/wahlService.ts", () => ({
  useWahlService: () => ({
    getWahlen: mockDefinitions.getWahlen,
  }),
}));

const { createWahl } = useWahlTestDataFactory();
const { generateRandomString } = useCommonTestDataFactory();
const { prepareUser } = useUserTestDataFactory();

describe("wahlenStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useWahlenStore>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useWahlenStore();
  });

  describe("initWahlen", () => {
    it("should_loadWahlen_when_calledWithCorrectWahltagID", async () => {
      const wahltagID = generateRandomString(10);
      const userStore = useUserStore();
      userStore.setUser(prepareUser().wahltagID(wahltagID).build());

      const expectedWahlArray = [createWahl()];
      const wahl = Promise.resolve(expectedWahlArray);
      mockDefinitions.getWahlen.mockReturnValue(wahl);

      await unitUnderTest.initWahlen();

      expect(unitUnderTest.wahlen).toStrictEqual(expectedWahlArray);
    });
  });

  describe("getWahlNameOrBlankStringById", () => {
    it("should_getWahlName_when_calledWithWahlId", async () => {
      const wahlOne = createWahl();
      const wahlTwo = createWahl();
      const wahlThree = createWahl();

      unitUnderTest.wahlen = [wahlOne, wahlTwo, wahlThree];

      const result = unitUnderTest.getWahlNameOrBlankStringById(wahlOne.wahlID);

      expect(result).toStrictEqual(wahlOne.name);
    });
  });

  describe("getWahlTagOrBlankStringById", () => {
    it("should_getWahlTag_when_calledWithWahlId", () => {
      const wahlOne = createWahl();
      const wahlTwo = createWahl();
      const wahlThree = createWahl();

      unitUnderTest.wahlen = [wahlOne, wahlTwo, wahlThree];

      const result = unitUnderTest.getWahlTagOrBlankStringById(wahlOne.wahlID);

      expect(result).toStrictEqual(wahlOne.wahltag);
    });

    it("should_getBlank_when_calledWithWahlIdThatDoesNotExist", () => {
      const wahlOne = createWahl();
      const wahlTwo = createWahl();
      const wahlThree = createWahl();
      const wahlFour = createWahl();

      unitUnderTest.wahlen = [wahlOne, wahlTwo, wahlThree];

      const result = unitUnderTest.getWahlTagOrBlankStringById(wahlFour.wahlID);

      expect(result).toStrictEqual("");
    });
  });
});
