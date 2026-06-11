import type { BedenklicherStimmzettelDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { BedenklicherStimmzettel } from "@/types/ergebnismeldung/MBW/BedenklicherStimmzettel.ts";

import {
  BedenklicherStimmzettelDTOSupplementsEnum,
  BedenklicherStimmzettelDTOValidityEnum,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { SupplementEnum } from "@/types/ergebnismeldung/MBW/SupplementEnum.ts";
import { ValidityEnum } from "@/types/ergebnismeldung/MBW/validityEnum.ts";

const SUPPLEMENT_DTO_ENUM_TO_MODEL_ENUM: Record<
  BedenklicherStimmzettelDTOSupplementsEnum,
  SupplementEnum
> = {
  TOO_MANY_LISTENKREUZE: SupplementEnum.TOO_MANY_LISTENKREUZE,
  TOO_MANY_SINGLE_KANDIDAT_VOTES: SupplementEnum.TOO_MANY_SINGLE_KANDIDAT_VOTES,
};
const SUPPLEMENT_MODEL_ENUM_TO_DTO_ENUM: Record<
  SupplementEnum,
  BedenklicherStimmzettelDTOSupplementsEnum
> = {
  TOO_MANY_LISTENKREUZE: BedenklicherStimmzettelDTOSupplementsEnum.Listenkreuze,
  TOO_MANY_SINGLE_KANDIDAT_VOTES:
    BedenklicherStimmzettelDTOSupplementsEnum.SingleKandidatVotes,
};

const VALIDITY_DTO_ENUM_TO_MODEL_ENUM: Record<
  BedenklicherStimmzettelDTOValidityEnum,
  ValidityEnum
> = {
  VALID: ValidityEnum.VALID,
  PARTIAL_VALID: ValidityEnum.PARTIAL_VALID,
  INVALID: ValidityEnum.INVALID,
};
const VALIDITY_MODEL_ENUM_TO_DTO_ENUM: Record<
  ValidityEnum,
  BedenklicherStimmzettelDTOValidityEnum
> = {
  VALID: BedenklicherStimmzettelDTOValidityEnum.Valid,
  PARTIAL_VALID: BedenklicherStimmzettelDTOValidityEnum.PartialValid,
  INVALID: BedenklicherStimmzettelDTOValidityEnum.Invalid,
};

export function useBedenklicherStimmzettelMapper() {
  function toModel(
    bedenklicherStimmzettelDTO: BedenklicherStimmzettelDTO
  ): BedenklicherStimmzettel {
    return {
      orderIndex: bedenklicherStimmzettelDTO.orderIndex,
      supplements: _supplementsDtoArrayToModelArray(
        bedenklicherStimmzettelDTO.supplements
      ),
      validity:
        VALIDITY_DTO_ENUM_TO_MODEL_ENUM[bedenklicherStimmzettelDTO.validity],
    };
  }

  function toDTO(
    bedenklicherStimmzettel: BedenklicherStimmzettel
  ): BedenklicherStimmzettelDTO {
    return {
      orderIndex: bedenklicherStimmzettel.orderIndex,
      supplements: _supplementsModelArrayToDtoArray(
        bedenklicherStimmzettel.supplements
      ),
      validity:
        VALIDITY_MODEL_ENUM_TO_DTO_ENUM[bedenklicherStimmzettel.validity],
    };
  }

  function _supplementsDtoArrayToModelArray(
    supplements: BedenklicherStimmzettelDTOSupplementsEnum[] | undefined
  ): SupplementEnum[] {
    if (!supplements) {
      return [];
    } else {
      return supplements.map(
        (dtoValue) => SUPPLEMENT_DTO_ENUM_TO_MODEL_ENUM[dtoValue]
      );
    }
  }

  function _supplementsModelArrayToDtoArray(
    supplements: SupplementEnum[]
  ): BedenklicherStimmzettelDTOSupplementsEnum[] {
    return supplements.map(
      (modelValue) => SUPPLEMENT_MODEL_ENUM_TO_DTO_ENUM[modelValue]
    );
  }

  return {
    toModel,
    toDTO,
  };
}
