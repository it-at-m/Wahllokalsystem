import type { BedenklicherStimmzettelDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { BedenklicherStimmzettel } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/BedenklicherStimmzettel.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import {
  BedenklicherStimmzettelDTOSupplementsEnum,
  BedenklicherStimmzettelDTOValidityEnum,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { SupplementEnum } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/SupplementEnum.ts";
import { ValidityEnum } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/ValidityEnum.ts";

const { generateRandomNumber, getRandomItem } = useCommonTestDataFactory();

export function useBedenklicherStimmzettelTestDataFactory() {
  function createBedenklicherStimmzettelDTO(): BedenklicherStimmzettelDTO {
    return {
      orderIndex: generateRandomNumber(3),
      supplements: [
        getRandomItem(Object.values(BedenklicherStimmzettelDTOSupplementsEnum)),
        getRandomItem(Object.values(BedenklicherStimmzettelDTOSupplementsEnum)),
        getRandomItem(Object.values(BedenklicherStimmzettelDTOSupplementsEnum)),
      ],
      validity: getRandomItem(
        Object.values(BedenklicherStimmzettelDTOValidityEnum)
      ),
    };
  }

  function createBedenklicherStimmzettel(): BedenklicherStimmzettel {
    return {
      orderIndex: generateRandomNumber(3),
      supplements: [
        getRandomItem(Object.values(SupplementEnum)),
        getRandomItem(Object.values(SupplementEnum)),
        getRandomItem(Object.values(SupplementEnum)),
      ],
      validity: getRandomItem(Object.values(ValidityEnum)),
    };
  }

  function prepareBedenklicherStimmzettelDTO(): Builder<BedenklicherStimmzettelDTO> {
    return proxyBuilder<BedenklicherStimmzettelDTO>(
      createBedenklicherStimmzettelDTO()
    );
  }

  function prepareBedenklicherStimmzettel(): Builder<BedenklicherStimmzettel> {
    return proxyBuilder<BedenklicherStimmzettel>(
      createBedenklicherStimmzettel()
    );
  }

  return {
    createBedenklicherStimmzettelDTO,
    createBedenklicherStimmzettel,
    prepareBedenklicherStimmzettelDTO,
    prepareBedenklicherStimmzettel,
  };
}
