import { spyOn } from "storybook/test";
import { afterEach, describe, expect, it, vi } from "vitest";

import { useWahllokalBenutzerService } from "@/composables/wahllokalbenutzer/wahllokalbenutzerService.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  adminApiConfigurationConstructor: vi.fn(),
  apiDeleteWahllokalBenutzer: vi.fn(),
  apiExportWahllokalBenutzer: vi.fn(),
  apiGenerateWahllokalbenutzer: vi.fn(),
  createElement: vi.fn(),
  createObjectURL: vi.fn(),
  revokeObjectURL: vi.fn(),
  wahllokalBenutzerControllerApiConstructor: class {
    deleteWahllokalBenutzer = mockDefinitions.apiDeleteWahllokalBenutzer;
    exportWahllokalBenutzer = mockDefinitions.apiExportWahllokalBenutzer;
    generateWahllokalbenutzer = mockDefinitions.apiGenerateWahllokalbenutzer;
  },
  vueRefBuilder: vi.fn().mockImplementation((value?: boolean) => ({
    value,
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
  const realCreateElement = document.createElement.bind(document);
  let linkMock: HTMLAnchorElement;
  let linkClickSpy: ReturnType<typeof vi.spyOn>;
  let linkRemoveSpy: ReturnType<typeof vi.spyOn>;

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
    linkClickSpy?.mockRestore();
    linkRemoveSpy?.mockRestore();
    document.createElement = realCreateElement;
  });

  describe("generateBenutzer", () => {
    it("should_triggerApiCallAndDownloadCsv_when_called", async () => {
      const wahltagID = "wahltagID";
      prepareDownloadMocks();
      mockDefinitions.apiGenerateWahllokalbenutzer.mockResolvedValue({
        data: { csv: "username;pin" },
      });

      await unitUnderTest.generateBenutzer(wahltagID);

      expect(mockDefinitions.apiGenerateWahllokalbenutzer).toHaveBeenCalledWith(
        wahltagID
      );
      expect(linkMock.download).toStrictEqual(
        `wahllokalbenutzer-${wahltagID}.csv`
      );
      expect(linkMock.click).toHaveBeenCalledTimes(1);
      expect(unitUnderTest.benutzer.value).toStrictEqual([
        { benutzername: "username;pin" },
      ]);
      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        "Success",
      ]);
    });

    it("should_addNotificationAndResetLoading_when_exceptionOccurred", async () => {
      const spyOnValueSetterOfRef = spyOn(
        unitUnderTest.isGenerating,
        "value",
        "set"
      );
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
      expect(spyOnValueSetterOfRef.mock.calls).toStrictEqual([[true], [false]]);

      spyOnValueSetterOfRef.mockRestore();
    });
  });

  describe("exportBenutzer", () => {
    it("should_triggerApiCallAndDownloadCsv_when_apiReturnsSingleCsvFile", async () => {
      const wahltagID = "wahltagID";
      prepareDownloadMocks();
      mockDefinitions.apiExportWahllokalBenutzer.mockResolvedValue({
        data: { csv: "username;pin" },
      });

      await unitUnderTest.exportBenutzer(wahltagID);

      expect(mockDefinitions.apiExportWahllokalBenutzer).toHaveBeenCalledWith(
        wahltagID
      );
      expect(linkMock.download).toStrictEqual(
        `wahllokalbenutzer-${wahltagID}.csv`
      );
      expect(linkMock.click).toHaveBeenCalledTimes(1);
    });

    it("should_triggerApiCallAndDownloadCsvs_when_apiReturnsCsvFileArray", async () => {
      const wahltagID = "wahltagID";
      prepareDownloadMocks();
      mockDefinitions.apiExportWahllokalBenutzer.mockResolvedValue({
        data: [{ csv: "username;pin" }, { csv: "username2;pin2" }],
      });

      await unitUnderTest.exportBenutzer(wahltagID);

      expect(linkMock.click).toHaveBeenCalledTimes(2);
      expect(linkMock.remove).toHaveBeenCalledTimes(2);
    });

    it("should_addNotificationAndResetLoading_when_exceptionOccurred", async () => {
      const spyOnValueSetterOfRef = spyOn(
        unitUnderTest.isExporting,
        "value",
        "set"
      );
      mockDefinitions.apiExportWahllokalBenutzer.mockRejectedValue(
        new Error("api call failed")
      );

      await expect(unitUnderTest.exportBenutzer("wahltagID")).rejects.toThrow();

      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        "Error",
      ]);
      expect(spyOnValueSetterOfRef.mock.calls).toStrictEqual([[true], [false]]);

      spyOnValueSetterOfRef.mockRestore();
    });
  });

  describe("loadBenutzer", () => {
    it("should_loadSortedBenutzer_when_apiReturnsCsv", async () => {
      const wahltagID = "wahltagID";
      mockDefinitions.apiExportWahllokalBenutzer.mockResolvedValue({
        data: { csv: "user-0003\r\nuser-0001\r\nuser-0002\r\n" },
      });

      await unitUnderTest.loadBenutzer(wahltagID);

      expect(mockDefinitions.apiExportWahllokalBenutzer).toHaveBeenCalledWith(
        wahltagID
      );
      expect(unitUnderTest.benutzer.value).toStrictEqual([
        { benutzername: "user-0001" },
        { benutzername: "user-0002" },
        { benutzername: "user-0003" },
      ]);
    });

    it("should_loadBenutzer_when_apiReturnsCsvFileArray", async () => {
      mockDefinitions.apiExportWahllokalBenutzer.mockResolvedValue({
        data: [{ csv: "user-0002" }, { csv: "user-0001" }],
      });

      await unitUnderTest.loadBenutzer("wahltagID");

      expect(unitUnderTest.benutzer.value).toStrictEqual([
        { benutzername: "user-0001" },
        { benutzername: "user-0002" },
      ]);
    });

    it("should_addNotificationAndResetLoading_when_exceptionOccurred", async () => {
      const spyOnValueSetterOfRef = spyOn(
        unitUnderTest.isLoading,
        "value",
        "set"
      );
      mockDefinitions.apiExportWahllokalBenutzer.mockRejectedValue(
        new Error("api call failed")
      );

      await expect(unitUnderTest.loadBenutzer("wahltagID")).rejects.toThrow();

      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        "Error",
      ]);
      expect(spyOnValueSetterOfRef.mock.calls).toStrictEqual([[true], [false]]);

      spyOnValueSetterOfRef.mockRestore();
    });
  });

  describe("deleteBenutzer", () => {
    it("should_triggerApiCallWithWahltagID_when_called", async () => {
      const wahltagID = "wahltagID";

      await unitUnderTest.deleteBenutzer(wahltagID);

      expect(mockDefinitions.apiDeleteWahllokalBenutzer).toHaveBeenCalledWith(
        wahltagID
      );
      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        "Success",
      ]);
      expect(unitUnderTest.benutzer.value).toStrictEqual([]);
    });

    it("should_addNotificationAndResetLoading_when_exceptionOccurred", async () => {
      const spyOnValueSetterOfRef = spyOn(
        unitUnderTest.isDeleting,
        "value",
        "set"
      );
      mockDefinitions.apiDeleteWahllokalBenutzer.mockRejectedValue(
        new Error("api call failed")
      );

      await expect(unitUnderTest.deleteBenutzer("wahltagID")).rejects.toThrow();

      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        "Error",
      ]);
      expect(spyOnValueSetterOfRef.mock.calls).toStrictEqual([[true], [false]]);

      spyOnValueSetterOfRef.mockRestore();
    });
  });

  describe("clearBenutzer", () => {
    it("should_clearBenutzer_when_called", async () => {
      mockDefinitions.apiExportWahllokalBenutzer.mockResolvedValue({
        data: { csv: "user-0001" },
      });
      await unitUnderTest.loadBenutzer("wahltagID");

      unitUnderTest.clearBenutzer();

      expect(unitUnderTest.benutzer.value).toStrictEqual([]);
    });
  });

  function prepareDownloadMocks() {
    linkMock = realCreateElement("a");
    linkClickSpy = vi
      .spyOn(linkMock, "click")
      .mockImplementation(() => undefined);
    linkRemoveSpy = vi
      .spyOn(linkMock, "remove")
      .mockImplementation(() => undefined);
    mockDefinitions.createObjectURL.mockReturnValue("blob:csv");
    Object.defineProperty(URL, "createObjectURL", {
      configurable: true,
      value: mockDefinitions.createObjectURL,
    });
    Object.defineProperty(URL, "revokeObjectURL", {
      configurable: true,
      value: mockDefinitions.revokeObjectURL,
    });
    mockDefinitions.createElement.mockReturnValue(linkMock);
    document.createElement = mockDefinitions.createElement;
  }
});
