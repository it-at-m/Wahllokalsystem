import { describe, expect, it } from "vitest";

import { useSystemBeschlussgrundReasonEnumTools } from "@/composables/dse/stimmzettelerfassung/systemBeschlussgrundReasonEnumTools.ts";
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";

describe("systemBeschlussgrundReasonEnumTools.ts", () => {
  const { mapSystemBeschlussgrundReasonEnumToText } =
    useSystemBeschlussgrundReasonEnumTools();

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
