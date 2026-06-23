import type { BeanstandeteWahlbriefeDTO } from "@/api/wls-clients/generated-briefwahl-api";
import type { BeanstandeteWahlbriefe } from "@/types/briefwahl/BeanstandeteWahlbriefe.ts";

import { useBeanstandeteWahlbriefeTestDataFactory } from "@tests/utils/briefwahl/BeanstandeteWahlbriefeTestDataFactory.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useBeanstandeteWahlbriefeMapper } from "@/composables/briefwahl/beanstandeteWahlbriefeMapper.ts";
import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";

const { prepareBeanstandeteWahlbriefeDTO, prepareBeanstandeteWahlbriefe } =
  useBeanstandeteWahlbriefeTestDataFactory();
const { generateRandomString } = useCommonTestDataFactory();

const {
  toModel,
  zurueckweisungsgrundStringToEnumValue,
  zurueckweisungsgrundEnumToDisplayString,
} = useBeanstandeteWahlbriefeMapper();

describe("beanstandeteWahlbriefeMapper.ts", () => {
  describe("toModel", () => {
    it("should_returnModel_when_givenDto", () => {
      const wahlID1 = generateRandomString(6);
      const wahlID2 = generateRandomString(6);
      const gruendeDTO = [
        "ZUGELASSEN",
        "UNTERSCHRIFT_FEHLT",
        "KEIN_ORIGINAL_SCHEIN",
      ];
      const gruendeModel = [
        ZurueckweisungsgrundEnum.Zugelassen,
        ZurueckweisungsgrundEnum.UnterschriftFehlt,
        ZurueckweisungsgrundEnum.KeinOriginalSchein,
      ];

      const beanstandeteWahlbirefeDTO: BeanstandeteWahlbriefeDTO["beanstandeteWahlbriefe"] =
        {};
      beanstandeteWahlbirefeDTO[wahlID1] = gruendeDTO;
      beanstandeteWahlbirefeDTO[wahlID2] = gruendeDTO;
      const dto: BeanstandeteWahlbriefeDTO = prepareBeanstandeteWahlbriefeDTO()
        .beanstandeteWahlbriefe(beanstandeteWahlbirefeDTO)
        .build();

      const model: BeanstandeteWahlbriefe = prepareBeanstandeteWahlbriefe()
        .wahlbezirkID(dto.wahlbezirkID)
        .waehlerverzeichnisNummer(dto.waehlerverzeichnisNummer)
        .beanstandeteWahlbriefe(new Map<string, ZurueckweisungsgrundEnum[]>())
        .build();
      model.beanstandeteWahlbriefe.set(wahlID1, gruendeModel);
      model.beanstandeteWahlbriefe.set(wahlID2, gruendeModel);

      expect(toModel(dto)).toStrictEqual(model);
    });
  });

  describe("zurueckweisungsgrundStringToEnumValue", () => {
    it.each([
      ["Zugelassen", ZurueckweisungsgrundEnum.Zugelassen],
      [
        "Wahlschein ungültig laut Liste",
        ZurueckweisungsgrundEnum.ScheinUngueltig,
      ],
      ["Kein Original-Wahlschein", ZurueckweisungsgrundEnum.KeinOriginalSchein],
      [
        "Unterschrift auf Wahlschein fehlt",
        ZurueckweisungsgrundEnum.UnterschriftFehlt,
      ],
      ["Stimmzettelumschlag fehlt", ZurueckweisungsgrundEnum.UmschlagFehlt],
      [
        "Wahlbrief und Stimmzettelumschlag offen",
        ZurueckweisungsgrundEnum.WahlbriefUndUmschlagOffen,
      ],
      [
        "Wahlscheine ungleich Stimmzettelumschläge",
        ZurueckweisungsgrundEnum.ScheineUngleichUmschlaege,
      ],
      [
        "Nicht-amtlicher Stimmzettelumschlag",
        ZurueckweisungsgrundEnum.UmschlagNichtAmtlich,
      ],
      [
        "Stimmzettelumschlag gefährdet Wahlgeheimnis",
        ZurueckweisungsgrundEnum.UmschlagGefaehrdetWahlgeheimnis,
      ],
      [
        "Gegenstand im Stimmzettelumschlag",
        ZurueckweisungsgrundEnum.GegenstandImUmschlag,
      ],
      [
        "Für diese Wahl nicht wahlberechtigt",
        ZurueckweisungsgrundEnum.NichtWahlberechtigt,
      ],
    ])(
      "should_convert'%s'CorrectlyToEnumValue_whenGivenString",
      (input, expected) => {
        expect(zurueckweisungsgrundStringToEnumValue(input)).toBe(expected);
      }
    );

    it("should_throwError_when_givenInvalidString", () => {
      expect(() =>
        zurueckweisungsgrundStringToEnumValue("invalid string")
      ).toThrow("Ungültiger Zurückweisungsgrund");
    });
  });

  describe("zurueckweisungsgrundEnumToDisplayString", () => {
    it.each([
      [ZurueckweisungsgrundEnum.Zugelassen, "Zugelassen"],
      [
        ZurueckweisungsgrundEnum.ScheinUngueltig,
        "Wahlschein ungültig laut Liste",
      ],
      [ZurueckweisungsgrundEnum.KeinOriginalSchein, "Kein Original-Wahlschein"],
      [
        ZurueckweisungsgrundEnum.UnterschriftFehlt,
        "Unterschrift auf Wahlschein fehlt",
      ],
      [ZurueckweisungsgrundEnum.UmschlagFehlt, "Stimmzettelumschlag fehlt"],
      [
        ZurueckweisungsgrundEnum.WahlbriefUndUmschlagOffen,
        "Wahlbrief und Stimmzettelumschlag offen",
      ],
      [
        ZurueckweisungsgrundEnum.ScheineUngleichUmschlaege,
        "Wahlscheine ungleich Stimmzettelumschläge",
      ],
      [
        ZurueckweisungsgrundEnum.UmschlagNichtAmtlich,
        "Nicht-amtlicher Stimmzettelumschlag",
      ],
      [
        ZurueckweisungsgrundEnum.UmschlagGefaehrdetWahlgeheimnis,
        "Stimmzettelumschlag gefährdet Wahlgeheimnis",
      ],
      [
        ZurueckweisungsgrundEnum.GegenstandImUmschlag,
        "Gegenstand im Stimmzettelumschlag",
      ],
      [
        ZurueckweisungsgrundEnum.NichtWahlberechtigt,
        "Für diese Wahl nicht wahlberechtigt",
      ],
    ])(
      "should_convert'%s'CorrectlyToString_whenGivenEnumValue",
      (input, expected) => {
        expect(zurueckweisungsgrundEnumToDisplayString(input)).toBe(expected);
      }
    );

    it("should_returnEmptyString_when_givenInvalidEnumValue", () => {
      expect(zurueckweisungsgrundEnumToDisplayString(null)).toBe("");
    });
  });
});
