import { usePersistedStimmzettelTestDataFactory } from "@tests/utils/dse/PersistedStimmzettelTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useBeschlussgrundOptionTools } from "@/composables/dse/beschlussfassung/beschlussgrundOptionTools.ts";
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";

const { preparePersistedStimmzettel } =
  usePersistedStimmzettelTestDataFactory();

describe("beschlussgrundOptionTools.ts", () => {
  let unitUnderTest: ReturnType<typeof useBeschlussgrundOptionTools>;

  beforeEach(() => {
    unitUnderTest = useBeschlussgrundOptionTools();
  });

  describe("mapGruendeToBeschlussgrundOptions", () => {
    it("should_mapGruendeToOptionsWithSelectedFalse_whenGivenListOfStrings", () => {
      const inputs = ["A", "B", "C"];

      const result = unitUnderTest.mapGruendeToBeschlussgrundOptions(inputs);

      expect(result).toStrictEqual([
        { grund: "A", selected: false },
        { grund: "B", selected: false },
        { grund: "C", selected: false },
      ]);
    });

    it("should_returnEmptyArray_when_inputIsEmpty", () => {
      const result = unitUnderTest.mapGruendeToBeschlussgrundOptions([]);
      expect(result).toStrictEqual([]);
    });
  });

  describe("setSystemBeschlussgruendeTrueWhenFoundInStimmzettel", () => {
    it("should_selectMatchingOptions_when_systemReasonsMatchOptions", () => {
      const stimmzettel = preparePersistedStimmzettel()
        .systemBeschlussvorschlag([
          {
            reason:
              SystemBeschlussgrundReasonEnum.KeineReststimmenvergabeMoeglich,
          },
          {
            reason:
              SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenAberImGesamtstimmenlimit,
          },
        ])
        .build();

      const options = unitUnderTest.mapGruendeToBeschlussgrundOptions([
        "keine Reststimmenvergabe möglich, Einzelstimmen und mehrere Kopfleistenkreuze",
        "Mehr als 3 Stimmen bei mind. einer Person und 80 Stimmen gesamt nicht überschritten",
        "einzelne Stimmen ungültig",
      ]);

      unitUnderTest.setSystemBeschlussgruendeTrueWhenFoundInStimmzettel(
        stimmzettel.systemBeschlussvorschlag,
        options
      );

      const selected = options.filter((o) => o.selected).map((o) => o.grund);
      expect(selected).toEqual(
        expect.arrayContaining([
          "keine Reststimmenvergabe möglich, Einzelstimmen und mehrere Kopfleistenkreuze",
          "Mehr als 3 Stimmen bei mind. einer Person und 80 Stimmen gesamt nicht überschritten",
        ])
      );
    });

    it("should_notSetAnyOptionTrue_when_systemReasonsDoNotMatchAnyOption", () => {
      const stimmzettel = preparePersistedStimmzettel()
        .systemBeschlussvorschlag([
          {
            reason:
              SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenOderListenkreuze,
          },
        ])
        .build();

      const options = unitUnderTest.mapGruendeToBeschlussgrundOptions([
        "Wählerwille ist zweifelsfrei erkennbar (lila Notiz auf dem Stimmzettel)",
        "keine Reststimmenvergabe möglich, Einzelstimmen und mehrere Kopfleistenkreuze",
      ]);

      unitUnderTest.setSystemBeschlussgruendeTrueWhenFoundInStimmzettel(
        stimmzettel.systemBeschlussvorschlag,
        options
      );

      expect(options.some((o) => o.selected)).toBe(false);
    });
  });

  describe("setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel", () => {
    it("should_selectMatchingOptions_when_wahlvorstandTextsMatchOptions", () => {
      const stimmzettel = preparePersistedStimmzettel()
        .wahlvorstandBeschlussvorschlag([
          {
            text: "Wählerwille ist nicht zweifelsfrei erkennbar",
          },
          {
            text: "Stimmzettel ist nicht amtlich hergestellt (zum Beispiel von einer anderen Gemeinde)",
          },
        ])
        .build();

      const options = unitUnderTest.mapGruendeToBeschlussgrundOptions([
        "Wählerwille ist nicht zweifelsfrei erkennbar",
        "Stimmzettel ist nicht amtlich hergestellt (zum Beispiel von einer anderen Gemeinde)",
        "mehr als 80 Einzelstimmen oder mehrere Kopfleistenkreuze ohne Einzelstimmen",
      ]);

      unitUnderTest.setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel(
        stimmzettel.wahlvorstandBeschlussvorschlag,
        options
      );

      const selected = options.filter((o) => o.selected).map((o) => o.grund);
      expect(selected).toEqual(
        expect.arrayContaining([
          "Wählerwille ist nicht zweifelsfrei erkennbar",
          "Stimmzettel ist nicht amtlich hergestellt (zum Beispiel von einer anderen Gemeinde)",
        ])
      );
    });

    it("should_notSetAnyOptionTrue_when_wahlvorstandTextsDoNotMatchAnyOption", () => {
      const stimmzettel = preparePersistedStimmzettel()
        .wahlvorstandBeschlussvorschlag([
          {
            text: "custom anderer Grund",
          },
        ])
        .build();

      const options = unitUnderTest.mapGruendeToBeschlussgrundOptions([
        "Wählerwille ist nicht zweifelsfrei erkennbar",
      ]);

      unitUnderTest.setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel(
        stimmzettel.wahlvorstandBeschlussvorschlag,
        options
      );

      expect(options.some((o) => o.selected)).toBe(false);
    });
  });

  describe("setBeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList", () => {
    it("should_returnOnlyNonMatchingTexts_when_notAllOptionsAreMatching", () => {
      const stimmzettel = preparePersistedStimmzettel()
        .wahlvorstandBeschlussvorschlag([
          { text: "custom-1" },
          { text: "Wählerwille ist nicht zweifelsfrei erkennbar" },
          { text: "custom-2" },
        ])
        .systemBeschlussvorschlag([
          {
            reason:
              SystemBeschlussgrundReasonEnum.KeineReststimmenvergabeMoeglich,
          },
        ])
        .build();

      const options = unitUnderTest.mapGruendeToBeschlussgrundOptions([
        "Wählerwille ist nicht zweifelsfrei erkennbar",
        "mehr als 80 Einzelstimmen oder mehrere Kopfleistenkreuze ohne Einzelstimmen",
      ]);

      const result =
        unitUnderTest.setBeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList(
          stimmzettel.wahlvorstandBeschlussvorschlag,
          stimmzettel.systemBeschlussvorschlag,
          options
        );

      expect(result).toStrictEqual(
        "custom-1, custom-2, keine Reststimmenvergabe möglich, Einzelstimmen und mehrere Kopfleistenkreuze"
      );
    });

    it("should_returnEmptyList_when_allTextsMatchOptions", () => {
      const stimmzettel = preparePersistedStimmzettel()
        .wahlvorstandBeschlussvorschlag([
          { text: "Wählerwille ist nicht zweifelsfrei erkennbar" },
        ])
        .systemBeschlussvorschlag([
          {
            reason:
              SystemBeschlussgrundReasonEnum.KeineReststimmenvergabeMoeglich,
          },
        ])
        .build();

      const options = unitUnderTest.mapGruendeToBeschlussgrundOptions([
        "Wählerwille ist nicht zweifelsfrei erkennbar",
        "keine Reststimmenvergabe möglich, Einzelstimmen und mehrere Kopfleistenkreuze",
      ]);

      const result =
        unitUnderTest.setBeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList(
          stimmzettel.wahlvorstandBeschlussvorschlag,
          stimmzettel.systemBeschlussvorschlag,
          options
        );

      expect(result).toStrictEqual("");
    });
  });
});
