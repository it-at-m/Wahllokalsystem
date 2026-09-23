import { usePersistedStimmzettelTestDataFactory } from "@tests/utils/dse/PersistedStimmzettelTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useBeschlussgrundOptionTools } from "@/composables/dse/beschlussfassung/beschlussgrundOptionTools.ts";
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";
import { WahlvorstandBeschlussvorschlaegeEnum } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussvorschlaegeEnum.ts";

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
        SystemBeschlussgrundReasonEnum.KeineReststimmenvergabeMoeglich,
        SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenAberImGesamtstimmenlimit,
      ]);

      unitUnderTest.setSystemBeschlussgruendeTrueWhenFoundInStimmzettel(
        stimmzettel.systemBeschlussvorschlag,
        options
      );

      const selected = options.filter((o) => o.selected).map((o) => o.grund);
      expect(selected).toEqual(
        expect.arrayContaining([
          SystemBeschlussgrundReasonEnum.KeineReststimmenvergabeMoeglich,
          SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenAberImGesamtstimmenlimit,
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
        WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleIstZweifelsfreiErkennbar,
        SystemBeschlussgrundReasonEnum.KeineReststimmenvergabeMoeglich,
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
            text: WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleNichtZweifelsfreiErkennbar,
          },
          {
            text: WahlvorstandBeschlussvorschlaegeEnum.NichtAmtlicherStimmzettel,
          },
        ])
        .build();

      const options = unitUnderTest.mapGruendeToBeschlussgrundOptions([
        WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleNichtZweifelsfreiErkennbar,
        WahlvorstandBeschlussvorschlaegeEnum.NichtAmtlicherStimmzettel,
        SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenOderListenkreuze,
      ]);

      unitUnderTest.setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel(
        stimmzettel.wahlvorstandBeschlussvorschlag,
        options
      );

      const selected = options.filter((o) => o.selected).map((o) => o.grund);
      expect(selected).toEqual(
        expect.arrayContaining([
          WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleNichtZweifelsfreiErkennbar,
          WahlvorstandBeschlussvorschlaegeEnum.NichtAmtlicherStimmzettel,
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
        WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleNichtZweifelsfreiErkennbar,
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
          {
            text: WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleNichtZweifelsfreiErkennbar,
          },
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
        WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleNichtZweifelsfreiErkennbar,
        SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenOderListenkreuze,
      ]);

      const result =
        unitUnderTest.setBeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList(
          stimmzettel.wahlvorstandBeschlussvorschlag,
          stimmzettel.systemBeschlussvorschlag,
          options
        );

      expect(result).toStrictEqual(
        `custom-1, custom-2, ${SystemBeschlussgrundReasonEnum.KeineReststimmenvergabeMoeglich}`
      );
    });

    it("should_returnEmptyList_when_allTextsMatchOptions", () => {
      const stimmzettel = preparePersistedStimmzettel()
        .wahlvorstandBeschlussvorschlag([
          {
            text: WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleNichtZweifelsfreiErkennbar,
          },
        ])
        .systemBeschlussvorschlag([
          {
            reason:
              SystemBeschlussgrundReasonEnum.KeineReststimmenvergabeMoeglich,
          },
        ])
        .build();

      const options = unitUnderTest.mapGruendeToBeschlussgrundOptions([
        WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleNichtZweifelsfreiErkennbar,
        SystemBeschlussgrundReasonEnum.KeineReststimmenvergabeMoeglich,
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
