import type { Stimmabgabevermerke } from "@/types/stimmabgabevermerke/Stimmabgabevermerke.ts";

import { useStimmabgabevermerkeTestDataFactory } from "@tests/utils/stimmabgabevermerke/StimmabgabevermerkeTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import {
  EingenommenerWahlscheinDTOStimmzettelartEnum,
  StimmzettelDTOStimmzettelartEnum,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useStimmabgabevermerkeMapper } from "@/composables/stimmabgabevermerke/stimmabgabevermerkeMapper.ts";
import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";

const { toModel } = useStimmabgabevermerkeMapper();

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
});
