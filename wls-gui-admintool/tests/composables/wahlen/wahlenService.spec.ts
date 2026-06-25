import type { WahlDTO } from "@/api/wls-clients/generated-admin-api";

import { spyOn } from "storybook/test";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useWahlenService } from "@/composables/wahlen/wahlenService.ts";

const mockDefinitions = vi.hoisted(() => ({
  apiGetWahlen: vi.fn(),
  apiUpdateWahlen: vi.fn(),
  addNotification: vi.fn(),
  adminApiConfigurationConstructor: vi.fn(),
  wahlenControllerApiConstructor: class {
    getWahlen = mockDefinitions.apiGetWahlen;
    updateWahlen = mockDefinitions.apiUpdateWahlen;
  },
  vueRefBuilder: vi.fn().mockImplementation(() => ({
    value: undefined,
  })),
}));

vi.mock("@/api/wls-clients/generated-admin-api", () => ({
  Configuration: mockDefinitions.adminApiConfigurationConstructor,
  WahlenControllerApi: mockDefinitions.wahlenControllerApiConstructor,
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

const unitUnderTest = useWahlenService();

const wahlDto: WahlDTO = {
  wahlID: "wahl-1",
  name: "Bundestagswahl",
  reihenfolge: 1,
  waehlerverzeichnisNummer: 10,
  wahltag: "2026-09-27",
  wahlart: "BTW",
  farbe: { r: 255, g: 0, b: 0 },
};

describe("wahlenService.ts", () => {
  beforeEach(() => {
    mockDefinitions.apiGetWahlen.mockResolvedValue({
      status: 200,
      data: [wahlDto],
    });
    mockDefinitions.apiUpdateWahlen.mockResolvedValue({
      status: 200,
      data: {},
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
  });

  describe("useWahlenService", () => {
    describe("getWahlen", () => {
      it("should_triggerApiCallWithWahltagID_when_called", async () => {
        const wahltagID = "wahltagID";

        await unitUnderTest.getWahlen(wahltagID);

        expect(mockDefinitions.apiGetWahlen).toHaveBeenCalledWith(wahltagID);
      });

      it("should_returnWahlen_when_succeeded", async () => {
        const result = await unitUnderTest.getWahlen("wahltagID");

        expect(result).toEqual([wahlDto]);
      });

      it("should_returnEmptyArray_when_noContent", async () => {
        mockDefinitions.apiGetWahlen.mockResolvedValue({
          status: 204,
          data: undefined,
        });

        const result = await unitUnderTest.getWahlen("wahltagID");

        expect(result).toEqual([]);
      });

      it("should_addErrorNotification_when_exceptionOccurred", async () => {
        mockDefinitions.apiGetWahlen.mockRejectedValue(
          new Error("api call failed")
        );

        await expect(unitUnderTest.getWahlen("wahltagID")).rejects.toThrow();

        expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
          expect.any(String),
          "Error",
        ]);
      });

      it("should_updateIsLoadingRef_when_succeeded", async () => {
        const spyOnValueSetterOfRef = spyOn(
          unitUnderTest.isLoading,
          "value",
          "set"
        );

        await unitUnderTest.getWahlen("wahltagID");

        expect(spyOnValueSetterOfRef.mock.calls).toStrictEqual([
          [true],
          [false],
        ]);

        spyOnValueSetterOfRef.mockRestore();
      });
    });

    describe("updateWahlen", () => {
      it("should_triggerApiCallWithWahltagIDAndFullWahlArray_when_called", async () => {
        const wahltagID = "wahltagID";

        await unitUnderTest.updateWahlen(wahltagID, [wahlDto]);

        expect(mockDefinitions.apiUpdateWahlen).toHaveBeenCalledWith(
          wahltagID,
          [wahlDto]
        );
      });

      it("should_addSuccessNotification_when_succeeded", async () => {
        await unitUnderTest.updateWahlen("wahltagID", [wahlDto]);

        expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
          expect.any(String),
          "Success",
        ]);
      });

      it("should_addErrorNotification_when_exceptionOccurred", async () => {
        mockDefinitions.apiUpdateWahlen.mockRejectedValue(
          new Error("api call failed")
        );

        await expect(
          unitUnderTest.updateWahlen("wahltagID", [wahlDto])
        ).rejects.toThrow();

        expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
          expect.any(String),
          "Error",
        ]);
      });

      it("should_updateIsSavingRef_when_succeeded", async () => {
        const spyOnValueSetterOfRef = spyOn(
          unitUnderTest.isSaving,
          "value",
          "set"
        );

        await unitUnderTest.updateWahlen("wahltagID", [wahlDto]);

        expect(spyOnValueSetterOfRef.mock.calls).toStrictEqual([
          [true],
          [false],
        ]);

        spyOnValueSetterOfRef.mockRestore();
      });
    });
  });
});
