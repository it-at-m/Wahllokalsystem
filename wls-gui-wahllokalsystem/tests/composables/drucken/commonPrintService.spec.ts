import type { Status } from "@/types/ergebnismeldung/common/Status.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";

import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useCommonPrintService } from "@/composables/drucken/commonPrintService.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/common/MeldungValidierungsstatusEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  jsBarcode: vi.fn(),
  toGermanDate: vi.fn(),
  toHhMm: vi.fn(),
}));

vi.mock(import("jsbarcode"), () => ({
  default: mockDefinitions.jsBarcode,
}));

vi.mock(
  import("@/composables/common/dateTimeFormatter.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useDateTimeFormatter: () => ({
        ...mod.useDateTimeFormatter(),
        toGermanDate: mockDefinitions.toGermanDate,
        toHhMm: mockDefinitions.toHhMm,
      }),
    };
  }
);

vi.mock(
  import("@/composables/userNotification/userNotificationService.ts"),
  () => ({
    useUserNotificationService: () => ({
      addNotification: mockDefinitions.addNotification,
    }),
  })
);

describe("commonPrintService.ts", () => {
  const mockedNow = new Date("2026-09-25T12:34:00");
  const mockedDataUrl = "data:image/jpeg;base64,barcode";
  const mockedUuid = "f1cc5a27-8b9e-4c19-927c-98b854f3da0f";
  const validWahl = {
    kennzeichen: "BTW26",
    wahltag: "2026-09-25",
  } as Wahl;

  let unitUnderTest: ReturnType<typeof useCommonPrintService>;
  let toDataUrl: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    vi.useFakeTimers({ now: mockedNow });
    toDataUrl = vi.fn().mockReturnValue(mockedDataUrl);
    vi.spyOn(document, "createElement").mockReturnValue({
      toDataURL: toDataUrl,
    } as unknown as HTMLElement);
    vi.spyOn(crypto, "randomUUID").mockReturnValue(mockedUuid);
    mockDefinitions.toGermanDate.mockReturnValue("25.09.2026");
    mockDefinitions.toHhMm.mockReturnValue("12:34");
    unitUnderTest = useCommonPrintService();
  });

  afterEach(() => {
    vi.useRealTimers();
    vi.restoreAllMocks();
    vi.clearAllMocks();
  });

  describe("createBarcode", () => {
    it("should_returnJpegBarcode_when_givenValidUwbSchnellmeldung", () => {
      const result = unitUnderTest.createBarcode(
        validWahl,
        MeldungsArtEnum.Schnellmeldung,
        WahlbezirksArtEnum.UWB,
        "12"
      );

      expect(document.createElement).toHaveBeenCalledExactlyOnceWith("canvas");
      expect(mockDefinitions.jsBarcode).toHaveBeenCalledExactlyOnceWith(
        expect.anything(),
        "BTW2625.09.2026-S-SBZ-12",
        { displayValue: false }
      );
      expect(toDataUrl).toHaveBeenCalledExactlyOnceWith("image/jpeg");
      expect(result).toStrictEqual(mockedDataUrl);
      expect(mockDefinitions.addNotification).not.toHaveBeenCalled();
    });

    it("should_useBriefwahlbezirkAndNiederschriftAbbreviations_when_givenBwbAndNiederschrift", () => {
      unitUnderTest.createBarcode(
        validWahl,
        MeldungsArtEnum.Niederschrift,
        WahlbezirksArtEnum.BWB,
        "12"
      );

      expect(mockDefinitions.jsBarcode).toHaveBeenCalledExactlyOnceWith(
        expect.anything(),
        "BTW2625.09.2026-N-BWBZ-12",
        { displayValue: false }
      );
    });

    it("should_preserveLeadingZerosInWahlbezirkNumber_when_numberIsNumeric", () => {
      unitUnderTest.createBarcode(
        validWahl,
        MeldungsArtEnum.Schnellmeldung,
        WahlbezirksArtEnum.UWB,
        "0012"
      );

      expect(mockDefinitions.jsBarcode).toHaveBeenCalledWith(
        expect.anything(),
        "BTW2625.09.2026-S-SBZ-0012",
        { displayValue: false }
      );
    });

    it.each([
      ["kennzeichenIsMissing", { ...validWahl, kennzeichen: "" }, "12"],
      ["wahlbezirkNummerIsNotNumeric", validWahl, "abc"],
      ["wahlbezirkNummerIsZero", validWahl, "0"],
    ])(
      "should_returnEmptyStringAndNotifyWarning_when_%s",
      (_condition, wahl, wahlbezirkNummer) => {
        const result = unitUnderTest.createBarcode(
          wahl,
          MeldungsArtEnum.Schnellmeldung,
          WahlbezirksArtEnum.UWB,
          wahlbezirkNummer
        );

        expect(result).toStrictEqual("");
        expect(mockDefinitions.addNotification).toHaveBeenCalledExactlyOnceWith(
          "Fehler beim Erstellen des Barcodes",
          UserNotificationCategoryEnum.WARNING
        );
        expect(mockDefinitions.jsBarcode).not.toHaveBeenCalled();
      }
    );

    it("should_notifyWarning_when_wahltagCannotBeFormatted", () => {
      mockDefinitions.toGermanDate.mockReturnValue(undefined);

      const result = unitUnderTest.createBarcode(
        validWahl,
        MeldungsArtEnum.Schnellmeldung,
        WahlbezirksArtEnum.UWB,
        "12"
      );

      expect(result).toStrictEqual("");
      expect(mockDefinitions.addNotification).toHaveBeenCalledExactlyOnceWith(
        "Fehler beim Erstellen des Barcodes",
        UserNotificationCategoryEnum.WARNING
      );
      expect(mockDefinitions.jsBarcode).not.toHaveBeenCalled();
    });
  });

  describe("createFooter", () => {
    it("should_returnFooterWithO_when_schnellmeldungIsValide", () => {
      const status = {
        schnellmeldung: {
          validierungsstatus: MeldungValidierungsstatusEnum.Valide,
        },
      } as Status;

      const result = unitUnderTest.createFooter(
        status,
        MeldungsArtEnum.Schnellmeldung
      );

      expect(result).toStrictEqual(`${mockedUuid}, 25.09.2026 12:34 O`);
      expect(mockDefinitions.toGermanDate).toHaveBeenCalledExactlyOnceWith(
        mockedNow
      );
      expect(mockDefinitions.toHhMm).toHaveBeenCalledExactlyOnceWith(mockedNow);
    });

    it("should_returnFooterWithM_when_schnellmeldungIsNotValide", () => {
      const status = {
        schnellmeldung: {
          validierungsstatus: MeldungValidierungsstatusEnum.Invalide,
        },
      } as Status;

      const result = unitUnderTest.createFooter(
        status,
        MeldungsArtEnum.Schnellmeldung
      );

      expect(result).toStrictEqual(`${mockedUuid}, 25.09.2026 12:34 M`);
    });

    it.each([
      ["statusIsUndefined", undefined],
      ["schnellmeldungIsMissing", {} as Status],
      ["validierungsstatusIsMissing", { schnellmeldung: {} } as Status],
    ])("should_returnUndefined_when_%s", (_condition, status) => {
      const result = unitUnderTest.createFooter(
        status,
        MeldungsArtEnum.Schnellmeldung
      );

      expect(result).toBeUndefined();
    });

    it("should_returnEmptyString_when_meldungsartIsNiederschrift", () => {
      const result = unitUnderTest.createFooter(
        undefined,
        MeldungsArtEnum.Niederschrift
      );

      expect(result).toStrictEqual("");
    });
  });
});
