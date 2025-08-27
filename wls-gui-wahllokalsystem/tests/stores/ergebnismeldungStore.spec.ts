import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getErgebnisse: vi.fn(),
}));

vi.mock("@/composables/ergebnismeldung/ergebnisService.ts", () => ({
  useErgebnisService: () => ({
    getErgebnisse: mockDefinitions.getErgebnisse,
  }),
}));

const { generateRandomString } = useCommonTestDataFactory();
const { createErgebnisse } = useErgebnisseTestDataFactory();
const { prepareUser } = useUserTestDataFactory();

describe("ergebnismeldungStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useErgebnismeldungStore>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useErgebnismeldungStore();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("loadErgebnisseByStapelArt", () => {
    it("should_loadErgebnisseByStapelArtAndReturnNull_when_calledAndNoErgebnisseFound", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArt = StapelArtEnum.ObwA;

      const userStore = useUserStore();
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      mockDefinitions.getErgebnisse.mockResolvedValue(null);

      await unitUnderTest.loadErgebnisseByStapelArt(wahlID, stapelArt);

      expect(mockDefinitions.getErgebnisse.mock.calls).toStrictEqual([
        [wahlID, wahlbezirkID, stapelArt],
      ]);
      expect(unitUnderTest.ergebnisse).toBeNull();
    });

    it("should_loadErgebnisseByStapelArt_when_calledAndErgebnisseFound", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArt = StapelArtEnum.ObwA;

      const userStore = useUserStore();
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const mockedErgebnisseModel = createErgebnisse();

      mockDefinitions.getErgebnisse.mockResolvedValue(mockedErgebnisseModel);

      await unitUnderTest.loadErgebnisseByStapelArt(wahlID, stapelArt);

      expect(mockDefinitions.getErgebnisse.mock.calls).toStrictEqual([
        [wahlID, wahlbezirkID, stapelArt],
      ]);
      expect(unitUnderTest.ergebnisse).toStrictEqual(mockedErgebnisseModel);
    });

    it("should_throwError_when_calledServiceThrowsError", async () => {
      const wahlID = generateRandomString(10);
      const stapelArt = StapelArtEnum.ObwA;

      mockDefinitions.getErgebnisse.mockRejectedValue(
        new Error("service call failed")
      );

      await expect(
        async () =>
          await unitUnderTest.loadErgebnisseByStapelArt(wahlID, stapelArt)
      ).rejects.toThrow();
    });
  });
});
