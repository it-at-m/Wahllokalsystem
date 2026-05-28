import type { StimmabgabevermerkeDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Stimmabgabevermerke } from "@/types/stimmabgabevermerke/Stimmabgabevermerke.ts";
import type { Stimmzettel } from "@/types/stimmabgabevermerke/Stimmzettel.ts";
import type { Vermerke } from "@/types/stimmabgabevermerke/Vermerke.ts";

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

      const dto = prepareStimmabgabevermerkeDTO()
        .vermerke([vermerkDTO])
        .eingenommeneWahlscheine([eingenommenerWahlscheinDTO])
        .build();

      const model: Stimmabgabevermerke = {
        wahlID: dto.wahlID,
        waehlerverzeichnisNummer: dto.waehlerverzeichnisNummer,
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
        wahlbezirkID: dto.wahlbezirkID,
        eingenommeneWahlscheine: new Map([
          [
            EingenommenerWahlscheinStimmzettelartEnum.Klein,
            eingenommenerWahlscheinDTO.anzahl,
          ],
        ]),
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

      const model: Stimmabgabevermerke = {
        wahlID: "wahl123",
        waehlerverzeichnisNummer: 12345,
        vermerke: [vermerk],
        wahlbezirkID: "bezirk123",
        eingenommeneWahlscheine: new Map([
          [EingenommenerWahlscheinStimmzettelartEnum.Klein, 4],
        ]),
      };

      const expectedDTO: StimmabgabevermerkeDTO = {
        wahlID: model.wahlID,
        waehlerverzeichnisNummer: model.waehlerverzeichnisNummer,
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
        wahlbezirkID: model.wahlbezirkID,
        eingenommeneWahlscheine: [
          {
            stimmzettelart: EingenommenerWahlscheinDTOStimmzettelartEnum.Klein,
            anzahl: 4,
          },
        ],
      };

      const result = toDto(model);

      expect(result).toStrictEqual(expectedDTO);
    });
  });
});
