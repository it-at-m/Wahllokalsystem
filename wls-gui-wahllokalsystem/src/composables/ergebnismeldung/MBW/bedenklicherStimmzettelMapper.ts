import type { BedenklicherStimmzettelDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { BedenklicherStimmzettel } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/BedenklicherStimmzettel.ts";

import {
  BedenklicherStimmzettelDTOSupplementsEnum,
  BedenklicherStimmzettelDTOValidityEnum,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { SupplementEnum } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/SupplementEnum.ts";
import { ValidityEnum } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/ValidityEnum.ts";

const SUPPLEMENT_DTO_ENUM_TO_MODEL_ENUM: Record<
  BedenklicherStimmzettelDTOSupplementsEnum,
  SupplementEnum
> = {
  [BedenklicherStimmzettelDTOSupplementsEnum.TooManyListenkreuze]:
    SupplementEnum.TOO_MANY_LISTENKREUZE,
  [BedenklicherStimmzettelDTOSupplementsEnum.TooManySingleKandidatVotes]:
    SupplementEnum.TOO_MANY_SINGLE_KANDIDAT_VOTES,
};

const SUPPLEMENT_MODEL_ENUM_TO_DTO_ENUM: Record<
  SupplementEnum,
  BedenklicherStimmzettelDTOSupplementsEnum
> = {
  [SupplementEnum.TOO_MANY_LISTENKREUZE]:
    BedenklicherStimmzettelDTOSupplementsEnum.TooManyListenkreuze,
  [SupplementEnum.TOO_MANY_SINGLE_KANDIDAT_VOTES]:
    BedenklicherStimmzettelDTOSupplementsEnum.TooManySingleKandidatVotes,
};

const SUPPLEMENT_MODEL_ENUM_TO_DISPLAY_STRING: Record<SupplementEnum, string> =
  {
    [SupplementEnum.TOO_MANY_LISTENKREUZE]: "Zu viele Listenkreuze",
    [
      SupplementEnum.TOO_MANY_SINGLE_KANDIDAT_VOTES
    ]: "Mehr als 3 Stimmen bei einer Kandidatin oder einem Kandidaten",
  };

const VALIDITY_DTO_ENUM_TO_MODEL_ENUM: Record<
  BedenklicherStimmzettelDTOValidityEnum,
  ValidityEnum
> = {
  [BedenklicherStimmzettelDTOValidityEnum.Valid]: ValidityEnum.VALID,
  [BedenklicherStimmzettelDTOValidityEnum.PartialValid]:
    ValidityEnum.PARTIAL_VALID,
  [BedenklicherStimmzettelDTOValidityEnum.Invalid]: ValidityEnum.INVALID,
};
const VALIDITY_MODEL_ENUM_TO_DTO_ENUM: Record<
  ValidityEnum,
  BedenklicherStimmzettelDTOValidityEnum
> = {
  [ValidityEnum.VALID]: BedenklicherStimmzettelDTOValidityEnum.Valid,
  [ValidityEnum.PARTIAL_VALID]:
    BedenklicherStimmzettelDTOValidityEnum.PartialValid,
  [ValidityEnum.INVALID]: BedenklicherStimmzettelDTOValidityEnum.Invalid,
};

const VALIDITY_MODEL_ENUM_TO_DISPLAY_STRING: Record<ValidityEnum, string> = {
  [ValidityEnum.VALID]: "Gültig",
  [ValidityEnum.PARTIAL_VALID]: "Teilweise gültig",
  [ValidityEnum.INVALID]: "Komplett ungültig",
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
    if (!bedenklicherStimmzettel.validity) {
      throw new Error("Validity ungültig");
    }
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

  function validityEnumToDisplayString(
    gueltigkeit: ValidityEnum | null
  ): string {
    if (!gueltigkeit) return "";
    return VALIDITY_MODEL_ENUM_TO_DISPLAY_STRING[gueltigkeit];
  }

  function supplementEnumToDisplayString(
    zusatz: SupplementEnum | null
  ): string {
    if (!zusatz) return "";
    return SUPPLEMENT_MODEL_ENUM_TO_DISPLAY_STRING[zusatz];
  }

  return {
    toModel,
    toDTO,
    validityEnumToDisplayString,
    supplementEnumToDisplayString,
  };
}
