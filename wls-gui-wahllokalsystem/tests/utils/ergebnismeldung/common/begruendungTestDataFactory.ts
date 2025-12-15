import type { BegruendungDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Begruendung } from "@/types/ergebnisermittlung/Begruendung.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useCommonErgebnismeldungTestDataFactory } from "@tests/utils/ergebnismeldung/common/commonErgebnismeldungTestDataFactory.ts";

import { BezirkUndWahlIDStapelartDTOStapelartEnum as DtoStapelArtEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { createBezirkUndWahlIDStapelartDTO } =
  useCommonErgebnismeldungTestDataFactory();
const { generateRandomString } = useCommonTestDataFactory();

export function useBegruendungTestDataFactory() {
  function createBegruendungDTO(): BegruendungDTO {
    return {
      bezirkUndWahlIDStapelart: createBezirkUndWahlIDStapelartDTO(
        DtoStapelArtEnum.ObwA
      ),
      grund: generateRandomString(15),
    };
  }

  function createBegruendung(): Begruendung {
    return {
      wahlID: generateRandomString(10),
      stapelart: StapelArtEnum.ObwA,
      grund: generateRandomString(15),
    };
  }

  function prepareBegruendungDTO(): Builder<BegruendungDTO> {
    return proxyBuilder<BegruendungDTO>(createBegruendungDTO());
  }

  function prepareBegruendung(): Builder<Begruendung> {
    return proxyBuilder<Begruendung>(createBegruendung());
  }

  return {
    createBegruendungDTO,
    createBegruendung,
    prepareBegruendungDTO,
    prepareBegruendung,
  };
}
