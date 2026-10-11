import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/common/MeldungValidierungsstatusEnum.ts";

const nonValidAlias = "M";

const enumToShortAlias: Readonly<
  Record<MeldungValidierungsstatusEnum, string>
> = {
  INVALIDE: nonValidAlias,
  VALIDE: "O",
  NICHT_GESENDET: nonValidAlias,
  NICHT_VALIDIERT: nonValidAlias,
};

export function useMeldungValidierungsstatusEnumMapper() {
  return {
    enumToShortAlias,
  };
}
