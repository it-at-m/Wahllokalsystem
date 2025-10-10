import type {
  BeanstandeteWahlbriefeCreateDTO,
  BeanstandeteWahlbriefeDTO,
} from "@/api/wls-clients/generated-briefwahl-api";
import type { BeanstandeteWahlbriefe } from "@/types/briefwahl/BeanstandeteWahlbriefe.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";

const { generateRandomString, generateRandomNumber, getRandomItem } =
  useCommonTestDataFactory();

export function useBeanstandeteWahlbriefeTestDataFactory() {
  function createBeanstandeteWahlbriefe(): BeanstandeteWahlbriefe {
    return {
      wahlbezirkID: generateRandomString(10),
      waehlerverzeichnisNummer: generateRandomNumber(1),
      beanstandeteWahlbriefe: _createRandomBeanstandeteWahlbriefe(),
    };
  }

  function createBeanstandeteWahlbriefeDTO(): BeanstandeteWahlbriefeDTO {
    return {
      wahlbezirkID: generateRandomString(10),
      waehlerverzeichnisNummer: generateRandomNumber(1),
      beanstandeteWahlbriefe: _createRandomBeanstandeteWahlbriefeForDTO(),
    };
  }

  function createBeanstandeteWahlbriefeCreateDTO(): BeanstandeteWahlbriefeCreateDTO {
    return {
      beanstandeteWahlbriefe: _createRandomBeanstandeteWahlbriefeForDTO(),
    };
  }

  function prepareBeanstandeteWahlbriefe(): Builder<BeanstandeteWahlbriefe> {
    return proxyBuilder<BeanstandeteWahlbriefe>(createBeanstandeteWahlbriefe());
  }

  function prepareBeanstandeteWahlbriefeDTO(): Builder<BeanstandeteWahlbriefeDTO> {
    return proxyBuilder<BeanstandeteWahlbriefeDTO>(
      createBeanstandeteWahlbriefeDTO()
    );
  }

  function prepareBeanstandeteWahlbriefeCreateDTO(): Builder<BeanstandeteWahlbriefeCreateDTO> {
    return proxyBuilder<BeanstandeteWahlbriefeCreateDTO>(
      createBeanstandeteWahlbriefeCreateDTO()
    );
  }

  function createRandomBeanstandeteWahlbriefeValues(
    length?: number
  ): ZurueckweisungsgrundEnum[] {
    const counter = length ?? 3;
    const result: ZurueckweisungsgrundEnum[] = [];

    for (let i = 0; i < counter; i++) {
      result.push(getRandomItem(Object.values(ZurueckweisungsgrundEnum)));
    }
    return result;
  }

  function _createRandomBeanstandeteWahlbriefe() {
    const wahlID1 = generateRandomString(6);
    const wahlID2 = generateRandomString(6);
    const briefe = new Map<string, ZurueckweisungsgrundEnum[]>();
    briefe.set(wahlID1, createRandomBeanstandeteWahlbriefeValues(5));
    briefe.set(wahlID2, createRandomBeanstandeteWahlbriefeValues(5));

    return briefe;
  }

  function _createRandomBeanstandeteWahlbriefeForDTO() {
    const wahlID1 = generateRandomString(6);
    const wahlID2 = generateRandomString(6);
    const result: BeanstandeteWahlbriefeCreateDTO["beanstandeteWahlbriefe"] =
      {};
    result[wahlID1] = createRandomBeanstandeteWahlbriefeValues(5);
    result[wahlID2] = createRandomBeanstandeteWahlbriefeValues(5);

    return result;
  }

  return {
    createRandomBeanstandeteWahlbriefeValues,
    createBeanstandeteWahlbriefe,
    createBeanstandeteWahlbriefeDTO,
    createBeanstandeteWahlbriefeCreateDTO,
    prepareBeanstandeteWahlbriefe,
    prepareBeanstandeteWahlbriefeCreateDTO,
    prepareBeanstandeteWahlbriefeDTO,
  };
}
