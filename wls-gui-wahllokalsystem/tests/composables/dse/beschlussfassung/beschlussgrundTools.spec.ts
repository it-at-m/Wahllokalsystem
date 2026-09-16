import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useBeschlussgrundTools } from "@/composables/dse/beschlussfassung/beschlussgrundTools.ts";
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";
import { WahlvorstandBeschlussvorschlaegeEnum } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussvorschlaegeEnum.ts";

const {
  createStimmzettelSystemBeschlussgrund,
  createStimmzettelWahlvorstandBeschlussgrund,
} = useStimmzettelTestDataFactory();

describe("useBeschlussgrundTools.ts", () => {
  let unitUnderTest: ReturnType<typeof useBeschlussgrundTools>;

  beforeEach(() => {
    unitUnderTest = useBeschlussgrundTools();
  });

  describe("sortWahlvorstandBeschlussgruende", () => {
    const wvGrund1 = {
      ...createStimmzettelWahlvorstandBeschlussgrund(),
      text: "kaffee ausgeschüttet",
    };
    const wvGrund2 = {
      ...createStimmzettelWahlvorstandBeschlussgrund(),
      text: WahlvorstandBeschlussvorschlaegeEnum.NichtAmtlicherStimmzettel,
    };
    const wvGrund3 = {
      ...createStimmzettelWahlvorstandBeschlussgrund(),
      text: WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleNichtZweifelsfreiErkennbar,
    };

    it.each([
      {
        text: "NotSorted",
        wvBeschlussgruende: [wvGrund2, wvGrund1, wvGrund3],
      },
      {
        text: "Sorted",
        wvBeschlussgruende: [wvGrund1, wvGrund2, wvGrund3],
      },
    ])(
      `should_returnSortedWahlvorstandBeschlussgruende_when_givenListOfWahlvorstandBeschlussgruendeThatIs'$text'`,
      ({ wvBeschlussgruende }) => {
        const expectedResult = [wvGrund1, wvGrund2, wvGrund3];

        const result =
          unitUnderTest.sortWahlvorstandBeschlussgruende(wvBeschlussgruende);

        expect(result).toStrictEqual(expectedResult);
      }
    );

    it("should_returnEmptyList_when_givenEmptyList", () => {
      const result = unitUnderTest.sortWahlvorstandBeschlussgruende([]);

      expect(result).toStrictEqual([]);
    });
  });

  describe("sortSystemBeschlussgruende", () => {
    const systemGrund1 = {
      ...createStimmzettelSystemBeschlussgrund(),
      reason: SystemBeschlussgrundReasonEnum.EinzelneStimmenUngueltig,
    };
    const systemGrund2 = {
      ...createStimmzettelSystemBeschlussgrund(),
      reason: SystemBeschlussgrundReasonEnum.KeineReststimmenvergabeMoeglich,
    };
    const systemGrund3 = {
      ...createStimmzettelSystemBeschlussgrund(),
      reason:
        SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenOderListenkreuze,
    };

    it.each([
      {
        text: "NotSorted",
        systemBeschlussgruende: [systemGrund2, systemGrund1, systemGrund3],
      },
      {
        text: "Sorted",
        systemBeschlussgruende: [systemGrund1, systemGrund2, systemGrund3],
      },
    ])(
      `should_returnSortedSystemBeschlussgruende_when_givenListOfSystemBeschlussgruendeThatIs'$text'`,
      ({ systemBeschlussgruende }) => {
        const expectedResult = [systemGrund1, systemGrund2, systemGrund3];

        const result = unitUnderTest.sortSystemBeschlussgruende(
          systemBeschlussgruende
        );

        expect(result).toStrictEqual(expectedResult);
      }
    );

    it("should_returnEmptyList_when_givenEmptyList", () => {
      const result = unitUnderTest.sortSystemBeschlussgruende([]);

      expect(result).toStrictEqual([]);
    });
  });
});
