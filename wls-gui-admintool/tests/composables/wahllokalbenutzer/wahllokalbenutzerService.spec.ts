import { spyOn } from "storybook/test";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useWahllokalBenutzerService } from "@/composables/wahllokalbenutzer/wahllokalbenutzerService.ts";

const mockDefinitions = vi.hoisted(() => ({
  apiGenerateWahllokalbenutzer: vi.fn(),
  apiExportWahllokalBenutzer: vi.fn(),
  apiDeleteWahllokalBenutzer: vi.fn(),
  addNotification: vi.fn(),
  adminApiConfigurationConstructor: vi.fn(),
  wahllokalBenutzerControllerApiConstructor: class {
    generateWahllokalbenutzer = mockDefinitions.apiGenerateWahllokalbenutzer;
    exportWahllokalBenutzer = mockDefinitions.apiExportWahllokalBenutzer;
    deleteWahllokalBenutzer = mockDefinitions.apiDeleteWahllokalBenutzer;
  },
  vueRefBuilder: vi.fn().mockImplementation(() => ({
    value: undefined,
  })),
}));

vi.mock("@/api/wls-clients/generated-admin-api", () => ({
  Configuration: mockDefinitions.adminApiConfigurationConstructor,
  WahllokalBenutzerControllerApi:
    mockDefinitions.wahllokalBenutzerControllerApiConstructor,
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

const unitUnderTest = useWahllokalBenutzerService();

describe("wahllokalbenutzerService.ts", () => {
  const createObjectURLMock = vi.fn();
  const revokeObjectURLMock = vi.fn();
  const anchorClickMock = vi.fn();

  beforeEach(() => {
    createObjectURLMock.mockReturnValue("blob:url");
    window.URL.createObjectURL = createObjectURLMock;
    window.URL.revokeObjectURL = revokeObjectURLMock;
    HTMLAnchorElement.prototype.click = anchorClickMock;
    mockDefinitions.apiGenerateWahllokalbenutzer.mockResolvedValue({
      data: { csv: "username1\nusername2" },
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
  });

  describe("useWahllokalBenutzerService", () => {
    describe("generateBenutzer", () => {
      it("should_triggerApiCallWithWahltagID_when_called", async () => {
        const wahltagID = "wahltagID";

        await unitUnderTest.generateBenutzer(wahltagID);

        expect(
          mockDefinitions.apiGenerateWahllokalbenutzer
        ).toHaveBeenCalledWith(wahltagID);
      });

      it("should_addSuccessNotification_when_succeeded", async () => {
        await unitUnderTest.generateBenutzer("wahltagID");

        expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
          expect.any(String),
          "Success",
        ]);
      });

      it("should_returnCsvFromResponse_when_succeeded", async () => {
        mockDefinitions.apiGenerateWahllokalbenutzer.mockResolvedValue({
          data: { csv: "kueh-0001\nmfpz-0002" },
        });

        const result = await unitUnderTest.generateBenutzer("wahltagID");

        expect(result).toBe("kueh-0001\nmfpz-0002");
      });

      it("should_addErrorNotification_when_exceptionOccurred", async () => {
        mockDefinitions.apiGenerateWahllokalbenutzer.mockRejectedValue(
          new Error("api call failed")
        );

        await expect(
          unitUnderTest.generateBenutzer("wahltagID")
        ).rejects.toThrow();

        expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
          expect.any(String),
          "Error",
        ]);
      });

      it("should_updateIsGeneratingRef_when_succeeded", async () => {
        const spyOnValueSetterOfRef = spyOn(
          unitUnderTest.isGenerating,
          "value",
          "set"
        );

        await unitUnderTest.generateBenutzer("wahltagID");

        expect(spyOnValueSetterOfRef.mock.calls).toStrictEqual([
          [true],
          [false],
        ]);

        spyOnValueSetterOfRef.mockRestore();
      });
    });

    describe("exportBenutzer", () => {
      it("should_triggerApiCallWithWahltagID_when_called", async () => {
        mockDefinitions.apiExportWahllokalBenutzer.mockResolvedValue({
          data: [{ csv: "username1\nusername2" }],
        });

        await unitUnderTest.exportBenutzer("wahltagID");

        expect(mockDefinitions.apiExportWahllokalBenutzer).toHaveBeenCalledWith(
          "wahltagID"
        );
      });

      it("should_triggerCsvDownload_when_succeeded", async () => {
        mockDefinitions.apiExportWahllokalBenutzer.mockResolvedValue({
          data: [{ csv: "username1\nusername2" }],
        });

        await unitUnderTest.exportBenutzer("wahltagID");

        expect(createObjectURLMock).toHaveBeenCalledTimes(1);
        expect(anchorClickMock).toHaveBeenCalledTimes(1);
        expect(revokeObjectURLMock).toHaveBeenCalledTimes(1);
      });

      it("should_triggerCsvDownload_when_responseIsSingleDto", async () => {
        mockDefinitions.apiExportWahllokalBenutzer.mockResolvedValue({
          data: { csv: "username1\nusername2" },
        });

        await unitUnderTest.exportBenutzer("wahltagID");

        expect(anchorClickMock).toHaveBeenCalledTimes(1);
      });

      it("should_returnJoinedCsv_when_succeeded", async () => {
        mockDefinitions.apiExportWahllokalBenutzer.mockResolvedValue({
          data: [{ csv: "kueh-0001" }, { csv: "mfpz-0002" }],
        });

        const result = await unitUnderTest.exportBenutzer("wahltagID");

        expect(result).toBe("kueh-0001\nmfpz-0002");
      });

      it("should_addErrorNotification_when_exceptionOccurred", async () => {
        mockDefinitions.apiExportWahllokalBenutzer.mockRejectedValue(
          new Error("api call failed")
        );

        await expect(
          unitUnderTest.exportBenutzer("wahltagID")
        ).rejects.toThrow();

        expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
          expect.any(String),
          "Error",
        ]);
        expect(anchorClickMock).toHaveBeenCalledTimes(0);
      });

      it("should_updateIsExportingRef_when_succeeded", async () => {
        mockDefinitions.apiExportWahllokalBenutzer.mockResolvedValue({
          data: [{ csv: "username1" }],
        });

        const spyOnValueSetterOfRef = spyOn(
          unitUnderTest.isExporting,
          "value",
          "set"
        );

        await unitUnderTest.exportBenutzer("wahltagID");

        expect(spyOnValueSetterOfRef.mock.calls).toStrictEqual([
          [true],
          [false],
        ]);

        spyOnValueSetterOfRef.mockRestore();
      });
    });

    describe("loadBenutzer", () => {
      it("should_returnJoinedCsvWithoutDownload_when_called", async () => {
        mockDefinitions.apiExportWahllokalBenutzer.mockResolvedValue({
          data: [{ csv: "kueh-0001" }, { csv: "mfpz-0002" }],
        });

        const result = await unitUnderTest.loadBenutzer("wahltagID");

        expect(mockDefinitions.apiExportWahllokalBenutzer).toHaveBeenCalledWith(
          "wahltagID"
        );
        expect(result).toBe("kueh-0001\nmfpz-0002");
        expect(anchorClickMock).toHaveBeenCalledTimes(0);
      });

      it("should_addErrorNotification_when_exceptionOccurred", async () => {
        mockDefinitions.apiExportWahllokalBenutzer.mockRejectedValue(
          new Error("api call failed")
        );

        await expect(unitUnderTest.loadBenutzer("wahltagID")).rejects.toThrow();

        expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
          expect.any(String),
          "Error",
        ]);
      });
    });

    describe("deleteBenutzer", () => {
      it("should_triggerApiCallWithWahltagID_when_called", async () => {
        const wahltagID = "wahltagID";

        await unitUnderTest.deleteBenutzer(wahltagID);

        expect(mockDefinitions.apiDeleteWahllokalBenutzer).toHaveBeenCalledWith(
          wahltagID
        );
      });

      it("should_addSuccessNotification_when_succeeded", async () => {
        await unitUnderTest.deleteBenutzer("wahltagID");

        expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
          expect.any(String),
          "Success",
        ]);
      });

      it("should_addErrorNotification_when_exceptionOccurred", async () => {
        mockDefinitions.apiDeleteWahllokalBenutzer.mockRejectedValue(
          new Error("api call failed")
        );

        await expect(
          unitUnderTest.deleteBenutzer("wahltagID")
        ).rejects.toThrow();

        expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
          expect.any(String),
          "Error",
        ]);
      });

      it("should_updateIsDeletingRef_when_succeeded", async () => {
        const spyOnValueSetterOfRef = spyOn(
          unitUnderTest.isDeleting,
          "value",
          "set"
        );

        await unitUnderTest.deleteBenutzer("wahltagID");

        expect(spyOnValueSetterOfRef.mock.calls).toStrictEqual([
          [true],
          [false],
        ]);

        spyOnValueSetterOfRef.mockRestore();
      });
    });
  });
});
