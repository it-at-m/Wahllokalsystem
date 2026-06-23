import { spyOn } from "storybook/test";
import { afterEach, describe, expect, it, vi } from "vitest";

import { useWahltermindatenService } from "@/composables/wahltermindaten/wahltermindatenService.ts";

const mockDefinitions = vi.hoisted(() => ({
  apiLoadWahlterminDaten: vi.fn(),
  apiDeleteWahlterminDaten: vi.fn(),
  addNotification: vi.fn(),
  adminApiConfigurationConstructor: vi.fn(),
  wahltermindatenControllerApiConstructor: class {
    loadWahltermindaten = mockDefinitions.apiLoadWahlterminDaten;
    deleteWahltermindaten = mockDefinitions.apiDeleteWahlterminDaten;
  },
  vueRefBuilder: vi.fn().mockImplementation(() => ({
    value: undefined,
  })),
}));

vi.mock("@/api/wls-clients/generated-admin-api", () => ({
  Configuration: mockDefinitions.adminApiConfigurationConstructor,
  WahltermindatenControllerApi:
    mockDefinitions.wahltermindatenControllerApiConstructor,
}));
vi.mock(
  import("@/composables/userNotification/userNotificationService.ts"),
  () => ({
    useUserNotificationService: () => ({
      addNotification: mockDefinitions.addNotification,
    }),
  })
);
vi.mock(import("vue"), () => ({
  ref: mockDefinitions.vueRefBuilder,
}));

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

        await expect(
          unitUnderTest.importWahlterminDaten(wahltagID)
        ).rejects.toThrow();

        expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
          expect.any(String),
          "Error",
        ]);
      });

      it("should_updateIsLoadingRef_when_succeeded", async () => {
        const wahltagID = "wahltagID";

        const spyOnValueSetterOfRef = spyOn(
          unitUnderTest.isLoading,
          "value",
          "set"
        );

        await unitUnderTest.importWahlterminDaten(wahltagID);

        expect(spyOnValueSetterOfRef.mock.calls).toStrictEqual([
          [true],
          [false],
        ]);

        spyOnValueSetterOfRef.mockRestore();
      });

      it("should_updateIsLoadingRef_when_exceptionOccurred", async () => {
        const wahltagID = "wahltagID";

        const spyOnValueSetterOfRef = spyOn(
          unitUnderTest.isLoading,
          "value",
          "set"
        );

        mockDefinitions.apiLoadWahlterminDaten.mockRejectedValue(
          new Error("api call failed")
        );

        await expect(
          unitUnderTest.importWahlterminDaten(wahltagID)
        ).rejects.toThrow();

        expect(spyOnValueSetterOfRef.mock.calls).toStrictEqual([
          [true],
          [false],
        ]);

        spyOnValueSetterOfRef.mockRestore();
      });
    });

    describe("deleteAndImportWahlterminDaten", () => {
      it("should_returnResolvedPromise_when_calledWithWahltag", async () => {
        const wahltagID = "wahltagID";

        await unitUnderTest.deleteAndImportWahlterminDaten(wahltagID);

        expect(mockDefinitions.apiDeleteWahlterminDaten).toHaveBeenCalledWith(
          wahltagID
        );
        expect(mockDefinitions.apiLoadWahlterminDaten).toHaveBeenCalledWith(
          wahltagID
        );
        expect(unitUnderTest.isDeleting.value).toStrictEqual(false);
        expect(unitUnderTest.isLoading.value).toStrictEqual(false);
      });

      it("should_addNotification_when_ deleteApiCallFailed", async () => {
        const wahltagID = "wahltagID";

        mockDefinitions.apiDeleteWahlterminDaten.mockRejectedValue(
          new Error("api call failed")
        );

        await expect(
          unitUnderTest.deleteAndImportWahlterminDaten(wahltagID)
        ).rejects.toThrow();

        expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
          expect.any(String),
          "Error",
        ]);
        expect(unitUnderTest.isDeleting.value).toStrictEqual(false);
        expect(unitUnderTest.isLoading.value).toStrictEqual(false);
      });

      it("should_addNotification_when_ loadApiCallFailed", async () => {
        const wahltagID = "wahltagID";

        mockDefinitions.apiLoadWahlterminDaten.mockRejectedValue(
          new Error("api call failed")
        );

        await expect(
          unitUnderTest.deleteAndImportWahlterminDaten(wahltagID)
        ).rejects.toThrow();

        expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
          expect.any(String),
          "Error",
        ]);
        expect(unitUnderTest.isDeleting.value).toStrictEqual(false);
        expect(unitUnderTest.isLoading.value).toStrictEqual(false);
      });

      it("should_updateIsRefs_when_succeeded", async () => {
        const wahltagID = "wahltagID";

        const spyOnValueSetterOfIsLoadingRef = spyOn(
          unitUnderTest.isLoading,
          "value",
          "set"
        );
        const spyOnValueSetterOfIsDeletingRef = spyOn(
          unitUnderTest.isDeleting,
          "value",
          "set"
        );

        await unitUnderTest.deleteAndImportWahlterminDaten(wahltagID);

        expect(spyOnValueSetterOfIsLoadingRef.mock.calls).toStrictEqual([
          [true],
          [false],
        ]);
        expect(spyOnValueSetterOfIsDeletingRef.mock.calls).toStrictEqual([
          [true],
          [false],
          [false],
        ]);

        spyOnValueSetterOfIsLoadingRef.mockRestore();
        spyOnValueSetterOfIsDeletingRef.mockRestore();
      });
    });
  });
});
