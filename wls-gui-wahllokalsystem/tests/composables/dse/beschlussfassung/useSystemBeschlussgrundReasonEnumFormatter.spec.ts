import { describe, expect, it } from "vitest";

import { useSystemBeschlussgrundReasonEnumFormatter } from "@/composables/dse/beschlussfassung/useSystemBeschlussgrundReasonEnumFormatter.ts";
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";

describe("useSystemBeschlussgrundReasonEnumFormatter.ts", () => {
  const {
    mapSystemBeschlussgrundReasonEnumToText,
    mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText,
  } = useSystemBeschlussgrundReasonEnumFormatter();

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
        "zu viele Einzelstimmen oder mehrere Kopfleistenkreuze ohne Einzelstimmen",
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
