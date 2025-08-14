import { useVorfaelleundvorkommnisseTestDataFactory } from "@tests/utils/vorfaelleundvorkommnisse/VorfaelleundvorkommnisseTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useEreignisComparator } from "@/composables/vorfaelleundvorkommnisse/ereignisUtils.ts";

const { prepareEreignis } = useVorfaelleundvorkommnisseTestDataFactory();

describe("ereignisUtils.ts", () => {
  const { compareEreignisseByUhrzeit } = useEreignisComparator();

  describe("compareEreignisse", () => {
    it("should_compareEreignisseByUhrzeit_when_uhrzeitIsDifferent", () => {
      const ereignis1 = prepareEreignis()
        .beschreibung("Beschreibung1")
        .ereignisart("VORFALL")
        .uhrzeit(new Date("2025-04-28T08:15:00"))
        .build();
      const ereignis2 = prepareEreignis()
        .beschreibung("Beschreibung2")
        .ereignisart("VORFALL")
        .uhrzeit(new Date("2025-04-28T10:00:00"))
        .build();

      expect(compareEreignisseByUhrzeit(ereignis1, ereignis2)).toBeLessThan(0);
      expect(compareEreignisseByUhrzeit(ereignis2, ereignis1)).toBeGreaterThan(
        0
      );
    });

    it("should_compareEreignisseByUhrzeit_when_uhrzeitIsTheSame", () => {
      const ereignis1 = prepareEreignis()
        .beschreibung("Beschreibung1")
        .ereignisart("VORFALL")
        .uhrzeit(new Date("2025-04-28T08:15:00"))
        .build();
      const ereignis2 = prepareEreignis()
        .beschreibung("Beschreibung2")
        .ereignisart("VORFALL")
        .uhrzeit(new Date("2025-04-28T08:15:00"))
        .build();

      expect(compareEreignisseByUhrzeit(ereignis1, ereignis2)).toBe(0);
    });
  });
});
