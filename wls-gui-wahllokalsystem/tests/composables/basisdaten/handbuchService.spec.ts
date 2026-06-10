import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterAll, beforeEach, describe, expect, it, vi } from "vitest";

import { useHandbuchService } from "@/composables/basisdaten/handbuchService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  configurationConstructor: vi.fn(),
  getHandbuch: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-basisdaten-api", () => ({
  HandbuchControllerApi: class {
    getHandbuch = mockDefinitions.getHandbuch;
  },
  Configuration: mockDefinitions.configurationConstructor,
}));
vi.mock(
  import("@/composables/userNotification/userNotificationService.ts"),
  () => ({
    useUserNotificationService: () => ({
      addNotification: mockDefinitions.addNotification,
    }),
  })
);

describe("handbuchService.ts", () => {
  const { generateRandomString } = useCommonTestDataFactory();
  const { prepareUser } = useUserTestDataFactory();

  let unitUnderTest: ReturnType<typeof useHandbuchService>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useHandbuchService();
    globalThis.URL.createObjectURL = vi.fn();
    globalThis.URL.revokeObjectURL = vi.fn();
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe("downloadHandbuch", () => {
    it("should_triggerDownloadHandbuch_when_calledFromApi", async () => {
      const wahltagID = generateRandomString(10);
      const wahlbezirksArt = WahlbezirksArtEnum.BWB;
      const mockedHandbuchBlob = new Blob(["PDF-Inhalt"]);
      const { setUser } = useUserStore();
      setUser(
        prepareUser()
          .wahltagID(wahltagID)
          .wahlbezirksArt(wahlbezirksArt)
          .build()
      );

      mockDefinitions.getHandbuch.mockReturnValue({
        status: 200,
        data: mockedHandbuchBlob,
      });
      const createObjectURLMock = vi.spyOn(URL, "createObjectURL");
      const createElementMock = vi.spyOn(document, "createElement");

      await unitUnderTest.downloadHandbuch(false);

      expect(mockDefinitions.getHandbuch).toHaveBeenCalledWith(
        wahltagID,
        wahlbezirksArt,
        { responseType: "blob" }
      );
      expect(createObjectURLMock).toHaveBeenCalled();
      expect(createElementMock).toHaveBeenCalledWith("a");
    });
  });

  describe("getHandbuch", () => {
    it("should_returnApiResponse_when_apiCallWasSuccessful", async () => {
      const wahltagID = generateRandomString(10);
      const wahlbezirksArt = WahlbezirksArtEnum.BWB;
      const mockedHandbuchBlob = new Blob(["PDF-Inhalt"]);
      const { setUser } = useUserStore();
      setUser(
        prepareUser()
          .wahltagID(wahltagID)
          .wahlbezirksArt(wahlbezirksArt)
          .build()
      );

      mockDefinitions.getHandbuch.mockReturnValue({
        status: 200,
        data: mockedHandbuchBlob,
      });

      const result = await unitUnderTest.getHandbuch(false);
      expect(result.status).toStrictEqual(200);
      expect(result.data).toStrictEqual(mockedHandbuchBlob);
    });

    it("should_showToasty_when_sendNotificationIsTrueAndApiCallFailed", async () => {
      const { setUser } = useUserStore();
      setUser(
        prepareUser()
          .wahltagID(generateRandomString(10))
          .wahlbezirksArt(WahlbezirksArtEnum.BWB)
          .build()
      );

      mockDefinitions.getHandbuch.mockRejectedValue(
        new Error("api call failed")
      );

      await expect(unitUnderTest.getHandbuch(true)).rejects.toThrow(
        "GetHandbuch Failed"
      );
      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        1
      );
      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        UserNotificationCategoryEnum.ERROR,
      ]);
    });

    it("should_notShowToasty_when_sendNotificationIsFalseAndApiCallFailed", async () => {
      const { setUser } = useUserStore();
      setUser(
        prepareUser()
          .wahltagID(generateRandomString(10))
          .wahlbezirksArt(WahlbezirksArtEnum.BWB)
          .build()
      );

      mockDefinitions.getHandbuch.mockRejectedValue(
        new Error("api call failed")
      );

      await expect(unitUnderTest.getHandbuch(false)).rejects.toThrow(
        "GetHandbuch Failed"
      );
      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
    });
  });
});
