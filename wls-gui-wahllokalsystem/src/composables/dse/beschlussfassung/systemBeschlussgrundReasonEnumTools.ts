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

const SYSTEM_BESCHLUSSGRUND_REASON_ENUM_TO_BESCHLUSSVORSCHLAG_TEXT: Record<
  SystemBeschlussgrundReasonEnum,
  string
> = {
  ZU_VIELE_EINZELSTIMMEN_ABER_IM_GESAMTSTIMMENLIMIT:
    "Mehr als 3 Stimmen bei mind. einer Person und 80 Stimmen gesamt nicht überschritten",
  KEINE_RESTSTIMMENVERGABE_MOEGLICH:
    "keine Reststimmenvergabe möglich, Einzelstimmen und mehrere Kopfleistenkreuze",
  EINZELNE_STIMMEN_UNGUELTIG: "einzelne Stimmen ungültig",
  ZU_VIELE_EINZELSTIMMEN_ODER_LISTENKREUZE:
    "mehr als 80 Einzelstimmen oder mehrere Kopfleistenkreuze ohne Einzelstimmen",
};

export function useSystemBeschlussgrundReasonEnumTools() {
  function mapSystemBeschlussgrundReasonEnumToText(
    systemBeschlussgrund: SystemBeschlussgrundReasonEnum
  ): string {
    return SYSTEM_BESCHLUSSGRUND_REASON_ENUM_TO_BESCHLUSSGRUND_TEXT[
      systemBeschlussgrund
    ];
  }

  function mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText(
    systemBeschlussgrund: SystemBeschlussgrundReasonEnum
  ): string {
    return SYSTEM_BESCHLUSSGRUND_REASON_ENUM_TO_BESCHLUSSVORSCHLAG_TEXT[
      systemBeschlussgrund
    ];
  }

  return {
    mapSystemBeschlussgrundReasonEnumToText,
    mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText,
  };
}
