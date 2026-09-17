import { describe, expect, it } from "vitest";

import { useSystemBeschlussgrundReasonEnumTools } from "@/composables/dse/stimmzettelerfassung/systemBeschlussgrundReasonEnumTools.ts";
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";

describe("systemBeschlussgrundReasonEnumTools.ts", () => {
  const {
    mapSystemBeschlussgrundReasonEnumToText,
    mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText,
  } = useSystemBeschlussgrundReasonEnumTools();

  describe("mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText", () => {
    it.each([
      [
        SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenAberImGesamtstimmenlimit,
        "Mehr als 3 Stimmen bei mind. einer Person und 80 Stimmen gesamt nicht überschritten",
      ],
      [
        SystemBeschlussgrundReasonEnum.KeineReststimmenvergabeMoeglich,
        "keine Reststimmenvergabe möglich, Einzelstimmen und mehrere Kopfleistenkreuze",
      ],
      [
        SystemBeschlussgrundReasonEnum.EinzelneStimmenUngueltig,
        "einzelne Stimmen ungültig",
      ],
      [
        SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenOderListenkreuze,
        "mehr als 80 Einzelstimmen oder mehrere Kopfleistenkreuze ohne Einzelstimmen",
      ],
    ])("should_map'%s'CorrectlyToString_when_enumGiven", (input, expected) => {
      expect(
        mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText(input)
      ).toBe(expected);
    });
  });

  describe("mapSystemBeschlussgrundReasonEnumToText", () => {
    it.each([
      [
        SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenAberImGesamtstimmenlimit,
        "Zu viele Einzelstimmen, aber im Gesamtstimmenlimit",
      ],
      [
        SystemBeschlussgrundReasonEnum.KeineReststimmenvergabeMoeglich,
        "Keine Reststimmenvergabe möglich",
      ],
      [
        SystemBeschlussgrundReasonEnum.EinzelneStimmenUngueltig,
        "Einzelne Stimmen ungültig",
      ],
      [
        SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenOderListenkreuze,
        "Zu viele Einzelstimmen oder Listenkreuze",
      ],
    ])("should_map'%s'CorrectlyToString_when_enumGiven", (input, expected) => {
      expect(mapSystemBeschlussgrundReasonEnumToText(input)).toBe(expected);
    });
  });
});
