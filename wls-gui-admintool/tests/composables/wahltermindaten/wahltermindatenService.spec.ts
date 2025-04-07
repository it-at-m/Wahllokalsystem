import { spyOn } from "@storybook/test";
import { afterEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import { useWahltermindatenService } from "@/composables/wahltermindaten/wahltermindatenService.ts";

const mockDefinitions = vi.hoisted(() => ({
  apiLoadWahlterminDaten: vi.fn(),
  apiDeleteWahlterminDaten: vi.fn(),
  addNotification: vi.fn(),
  adminApiConfigurationConstructor: vi.fn().mockImplementation(() => {
    return {};
  }),
  wahltermindatenControllerApiConstructor: vi.fn().mockImplementation(() => {
    return {
      loadWahltermindaten: mockDefinitions.apiLoadWahlterminDaten,
      deleteWahltermindaten: mockDefinitions.apiDeleteWahlterminDaten,
    };
  }),
  vueRef: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-admin-api", () => ({
  Configuration: mockDefinitions.adminApiConfigurationConstructor,
  WahltermindatenControllerApi:
    mockDefinitions.wahltermindatenControllerApiConstructor,
}));
vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));
vi.mock("vue", () => ({
  ref: mockDefinitions.vueRef,
}));

const mockedRef = {
  value: false,
};
mockDefinitions.vueRef.mockImplementation(() => {
  return mockedRef;
});

const unitUnderTest = useWahltermindatenService();

describe("wahltermindatenService.ts", () => {
  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
  });

  describe("useWahltermindatenService", () => {
    describe("importWahlterminDaten", () => {
      it("should_triggerApiCallWithWahltagID_when_called", async () => {
        const wahltagID = "wahltagID";

        await unitUnderTest.importWahlterminDaten(wahltagID);

        expect(mockDefinitions.apiLoadWahlterminDaten).toHaveBeenCalledWith(
          wahltagID
        );
      });

      it("should_addNotification_when_exceptionOccurred", async () => {
        const wahltagID = "wahltagID";

        mockDefinitions.apiLoadWahlterminDaten.mockRejectedValue(
          new Error("api call failed")
        );

        await unitUnderTest.importWahlterminDaten(wahltagID);

        expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
          expect.any(String),
          "Error",
        ]);
      });

      it("should_updateIsLoadingRef_when_succeeded", async () => {
        const wahltagID = "wahltagID";

        const loadingRef = ref(false);
        mockDefinitions.vueRef.mockReturnValue(loadingRef);
        const spyOnValueSetterOfRef = spyOn(mockedRef, "value", "set");

        await unitUnderTest.importWahlterminDaten(wahltagID);

        expect(spyOnValueSetterOfRef.mock.calls).toStrictEqual([
          [true],
          [false],
        ]);

        spyOnValueSetterOfRef.mockRestore();
      });

      it("should_updateIsLoadingRef_when_exceptionOccurred", async () => {
        const wahltagID = "wahltagID";

        const loadingRef = ref(false);
        mockDefinitions.vueRef.mockReturnValue(loadingRef);
        const spyOnValueSetterOfRef = spyOn(mockedRef, "value", "set");

        mockDefinitions.apiLoadWahlterminDaten.mockRejectedValue(
          new Error("api call failed")
        );

        await unitUnderTest.importWahlterminDaten(wahltagID);

        expect(spyOnValueSetterOfRef.mock.calls).toStrictEqual([
          [true],
          [false],
        ]);

        spyOnValueSetterOfRef.mockRestore();
      });
    });

    describe("deleteWahltermindaten", () => {
      it("should_returnResolvedPromise_when_calledWithWahltag", async () => {
        const wahltagID = "wahltagID";

        await unitUnderTest.deleteWahltermindaten(wahltagID);

        expect(mockDefinitions.apiDeleteWahlterminDaten).toHaveBeenCalledWith(
          wahltagID
        );
      });

      it("should_returnRejectedPromise_when_calledExceptionOnApiCallOccurred", async () => {
        const wahltagID = "wahltagID";

        mockDefinitions.apiDeleteWahlterminDaten.mockRejectedValue(
          new Error("api call failed")
        );

        await expect(
          async () => await unitUnderTest.deleteWahltermindaten(wahltagID)
        ).rejects.toEqual(undefined);

        expect(mockDefinitions.apiDeleteWahlterminDaten).toHaveBeenCalledWith(
          wahltagID
        );
      });

      it("should_addNotification_when_exceptionOccurred", async () => {
        const wahltagID = "wahltagID";

        mockDefinitions.apiDeleteWahlterminDaten.mockRejectedValue(
          new Error("api call failed")
        );

        await expect(
          async () => await unitUnderTest.deleteWahltermindaten(wahltagID)
        ).rejects.toEqual(undefined);

        expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
          expect.any(String),
          "Error",
        ]);
      });

      it("should_updateIsDeletingRef_when_succeeded", async () => {
        const wahltagID = "wahltagID";

        const loadingRef = ref(false);
        mockDefinitions.vueRef.mockReturnValue(loadingRef);
        const spyOnValueSetterOfRef = spyOn(mockedRef, "value", "set");

        await unitUnderTest.deleteWahltermindaten(wahltagID);

        expect(spyOnValueSetterOfRef.mock.calls).toStrictEqual([
          [true],
          [false],
        ]);

        spyOnValueSetterOfRef.mockRestore();
      });

      it("should_updateIsDeletingRef_when_exceptionOccurred", async () => {
        const wahltagID = "wahltagID";

        const loadingRef = ref(false);
        mockDefinitions.vueRef.mockReturnValue(loadingRef);
        const spyOnValueSetterOfRef = spyOn(mockedRef, "value", "set");

        mockDefinitions.apiDeleteWahlterminDaten.mockRejectedValue(
          new Error("api call failed")
        );

        await expect(
          async () => await unitUnderTest.deleteWahltermindaten(wahltagID)
        ).rejects.toEqual(undefined);

        expect(spyOnValueSetterOfRef.mock.calls).toStrictEqual([
          [true],
          [false],
        ]);

        spyOnValueSetterOfRef.mockRestore();
      });
    });
  });
});
