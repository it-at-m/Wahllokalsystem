import { describe, expect, it } from "vitest";

import {
  EreignisartEnum,
  getEreignisArtForDateRelatedToSchliessungsuhrzeit,
} from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";

describe("Ereignisart.ts", () => {
  describe("getEreignisArtForDateRelatedToSchliessungsuhrzeit", () => {
    it("should_returnVorfall_when_schliessungsuhrzeitIsNotSet", () => {
      const result = getEreignisArtForDateRelatedToSchliessungsuhrzeit(
        new Date(),
        undefined
      );

      expect(result).toStrictEqual(EreignisartEnum.Vorfall);
    });

    it("should_returnVorfall_when_schliessungsuhrzeitIsAfterEreignisDate", () => {
      const schliessungsuhrzeit = new Date();
      const result = getEreignisArtForDateRelatedToSchliessungsuhrzeit(
        new Date(schliessungsuhrzeit.getTime() - 1),
        schliessungsuhrzeit
      );

      expect(result).toStrictEqual(EreignisartEnum.Vorfall);
    });

    it("should_returnVorfall_when_schliessungsuhrzeitIsEqualEreignisDate", () => {
      const schliessungsuhrzeit = new Date();
      const result = getEreignisArtForDateRelatedToSchliessungsuhrzeit(
        schliessungsuhrzeit,
        schliessungsuhrzeit
      );

      expect(result).toStrictEqual(EreignisartEnum.Vorfall);
    });

    it("should_returnVorkommnis_when_schliesssungsuhrzeitIsBeforeEreignisDate", () => {
      const schliessungsuhrzeit = new Date();
      const result = getEreignisArtForDateRelatedToSchliessungsuhrzeit(
        new Date(schliessungsuhrzeit.getTime() + 1),
        schliessungsuhrzeit
      );

      expect(result).toStrictEqual(EreignisartEnum.Vorkommnis);
    });
  });
});
