import type { StimmabgabevermerkeDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Stimmabgabevermerke } from "@/types/stimmabgabevermerke/Stimmabgabevermerke.ts";
import type { Stimmzettel } from "@/types/stimmabgabevermerke/Stimmzettel.ts";
import type { Vermerke } from "@/types/stimmabgabevermerke/Vermerke.ts";
import type { Wahldaten } from "@/types/stimmabgabevermerke/Wahldaten.ts";

import { useStimmabgabevermerkeTestDataFactory } from "@tests/utils/stimmabgabevermerke/StimmabgabevermerkeTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import {
  EingenommenerWahlscheinDTOStimmzettelartEnum,
  StimmzettelDTOStimmzettelartEnum,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useStimmabgabevermerkeMapper } from "@/composables/stimmabgabevermerke/stimmabgabevermerkeMapper.ts";
import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";

const { toModel, toDto } = useStimmabgabevermerkeMapper();

const {
  prepareStimmabgabevermerkeDTO,
  prepareWahldatenDTO,
  prepareStimmzettelDTO,
  prepareVermerkDTO,
} = useStimmabgabevermerkeTestDataFactory();

describe("stimmabgabevermerkeMapper.ts", () => {
  describe("toModel", () => {
    it("should_returnModel_when_dtoIsGiven", () => {
      const stimmzettelDTO = prepareStimmzettelDTO()
        .stimmzettelart(StimmzettelDTOStimmzettelartEnum.Klein)
        .build();

      const vermerkDTO = prepareVermerkDTO()
        .stimmzettel([stimmzettelDTO])
        .build();

      const eingenommenerWahlscheinDTO = {
        stimmzettelart: EingenommenerWahlscheinDTOStimmzettelartEnum.Klein,
        anzahl: 4,
      };

      const wahldatenDTO = prepareWahldatenDTO()
        .vermerke([vermerkDTO])
        .eingenommeneWahlscheine([eingenommenerWahlscheinDTO])
        .build();

      const dto = prepareStimmabgabevermerkeDTO()
        .wahldaten([wahldatenDTO])
        .build();

      const model: Stimmabgabevermerke = {
        anzahlBlaetter: dto.anzahlBlaetter,
        waehlerverzeichnisNummer: dto.waehlerverzeichnisNummer,
        wahlbezirkID: dto.wahlbezirkID,
        wahldaten: [
          {
            wahlID: wahldatenDTO.wahlID,
            waehlerverzeichnisNummer: wahldatenDTO.waehlerverzeichnisNummer,
            vermerke: [
              {
                blattnummer: vermerkDTO.blattnummer,
                stimmzettel: [
                  {
                    anzahl: stimmzettelDTO.anzahl,
                    stimmzettelart: StimmzettelStimmzettelartEnum.Klein,
                  },
                ],
              },
            ],
            wahlbezirkID: wahldatenDTO.wahlbezirkID,
            eingenommeneWahlscheine: new Map([
              [
                EingenommenerWahlscheinStimmzettelartEnum.Klein,
                eingenommenerWahlscheinDTO.anzahl,
              ],
            ]),
          },
        ],
      };

      const result = toModel(dto);

      expect(result).toStrictEqual(model);
    });
  });

  describe("toDTO", () => {
    it("should_returnDTO_when_modelIsGiven", () => {
      const stimmzettel: Stimmzettel = {
        anzahl: null,
        stimmzettelart: StimmzettelStimmzettelartEnum.Klein,
      };

      const vermerk: Vermerke = {
        blattnummer: 1,
        stimmzettel: [stimmzettel],
      };

      const wahldaten: Wahldaten = {
        wahlID: "wahl123",
        waehlerverzeichnisNummer: 12345,
        vermerke: [vermerk],
        wahlbezirkID: "bezirk123",
        eingenommeneWahlscheine: new Map([
          [EingenommenerWahlscheinStimmzettelartEnum.Klein, 4],
        ]),
      };

      const model: Stimmabgabevermerke = {
        anzahlBlaetter: 2,
        waehlerverzeichnisNummer: 12345,
        wahlbezirkID: "bezirk123",
        wahldaten: [wahldaten],
      };

      const expectedDTO: StimmabgabevermerkeDTO = {
        anzahlBlaetter: model.anzahlBlaetter,
        waehlerverzeichnisNummer: model.waehlerverzeichnisNummer,
        wahlbezirkID: model.wahlbezirkID,
        wahldaten: [
          {
            wahlID: wahldaten.wahlID,
            waehlerverzeichnisNummer: wahldaten.waehlerverzeichnisNummer,
            vermerke: [
              {
                blattnummer: vermerk.blattnummer,
                stimmzettel: [
                  {
                    anzahl: 0,
                    stimmzettelart: StimmzettelDTOStimmzettelartEnum.Klein,
                  },
                ],
              },
            ],
            wahlbezirkID: wahldaten.wahlbezirkID,
            eingenommeneWahlscheine: [
              {
                stimmzettelart:
                  EingenommenerWahlscheinDTOStimmzettelartEnum.Klein,
                anzahl: 4,
              },
            ],
          },
        ],
      };

      const result = toDto(model);

      expect(result).toStrictEqual(expectedDTO);
    });
  });
});
