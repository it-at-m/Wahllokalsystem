import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";

const SYSTEM_BESCHLUSSGRUND_REASON_ENUM_TO_BESCHLUSSGRUND_TEXT: Record<
  SystemBeschlussgrundReasonEnum,
  string
> = {
  ZU_VIELE_EINZELSTIMMEN_ABER_IM_GESAMTSTIMMENLIMIT:
    "Zu viele Einzelstimmen, aber im Gesamtstimmenlimit",
  KEINE_RESTSTIMMENVERGABE_MOEGLICH: "Keine Reststimmenvergabe möglich",
  EINZELNE_STIMMEN_UNGUELTIG: "Einzelne Stimmen ungültig",
  ZU_VIELE_EINZELSTIMMEN_ODER_LISTENKREUZE:
    "Zu viele Einzelstimmen oder Listenkreuze",
};

export function useSystemBeschlussgrundReasonEnumTools() {
  function mapSystemBeschlussgrundReasonEnumToText(
    systemBeschlussgrund: SystemBeschlussgrundReasonEnum
  ): string {
    return SYSTEM_BESCHLUSSGRUND_REASON_ENUM_TO_BESCHLUSSGRUND_TEXT[
      systemBeschlussgrund
    ];
  }

  return {
    mapSystemBeschlussgrundReasonEnumToText,
  };
}
