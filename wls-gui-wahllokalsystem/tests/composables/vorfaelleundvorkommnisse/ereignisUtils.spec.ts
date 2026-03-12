import { useVorfaelleundvorkommnisseTestDataFactory } from "@tests/utils/vorfaelleundvorkommnisse/VorfaelleundvorkommnisseTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useEreignisUtils } from "@/composables/vorfaelleundvorkommnisse/ereignisUtils.ts";
import { EreignisartEnum } from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";

const { prepareWahlbezirkEreignisse, prepareEreignis } =
  useVorfaelleundvorkommnisseTestDataFactory();

describe("ereignisUtils.ts", () => {
  const { hasDoneVorkommnisse, compareEreignisseByUhrzeit } =
    useEreignisUtils();

  describe("hasDoneVorkommnisse", () => {
    it.each([
      [
        {
          ereignisse: prepareWahlbezirkEreignisse()
            .ereigniseintraege([])
            .keineVorkommnisse(true)
            .build(),
          expected: true,
        },
      ],
      [
        {
          ereignisse: prepareWahlbezirkEreignisse()
            .ereigniseintraege([])
            .keineVorkommnisse(false)
            .build(),
          expected: false,
        },
      ],
      [
        {
          ereignisse: prepareWahlbezirkEreignisse()
            .ereigniseintraege([
              prepareEreignis().ereignisart(EreignisartEnum.Vorkommnis).build(),
            ])
            .keineVorkommnisse(false)
            .build(),
          expected: true,
        },
      ],
      [
        {
          ereignisse: prepareWahlbezirkEreignisse()
            .ereigniseintraege([
              prepareEreignis().ereignisart(EreignisartEnum.Vorfall).build(),
            ])
            .keineVorkommnisse(false)
            .build(),
          expected: false,
        },
      ],
    ])(
      "should_return'$expected'_when_ereignisseIs'$ereignisse'",
      ({ ereignisse, expected }) => {
        const result = hasDoneVorkommnisse(ereignisse);

        expect(result).toEqual(expected);
      }
    );
  });

  describe("compareEreignisse", () => {
    it("should_compareEreignisseByUhrzeit_when_uhrzeitIsDifferent", () => {
      const ereignis1 = prepareEreignis()
        .beschreibung("Beschreibung1")
        .ereignisart(EreignisartEnum.Vorfall)
        .uhrzeit(new Date("2025-04-28T08:15:00"))
        .build();
      const ereignis2 = prepareEreignis()
        .beschreibung("Beschreibung2")
        .ereignisart(EreignisartEnum.Vorfall)
        .uhrzeit(new Date("2025-04-28T10:00:00"))
        .build();

      expect(compareEreignisseByUhrzeit(ereignis1, ereignis2)).toBeLessThan(0);
      expect(compareEreignisseByUhrzeit(ereignis2, ereignis1)).toBeGreaterThan(
        0
      );
    });

    it("should_equalEreignisse_when_uhrzeitIsTheSame", () => {
      const ereignis1 = prepareEreignis()
        .beschreibung("Beschreibung1")
        .ereignisart(EreignisartEnum.Vorfall)
        .uhrzeit(new Date("2025-04-28T08:15:00"))
        .build();
      const ereignis2 = prepareEreignis()
        .beschreibung("Beschreibung2")
        .ereignisart(EreignisartEnum.Vorfall)
        .uhrzeit(new Date("2025-04-28T08:15:00"))
        .build();

      expect(compareEreignisseByUhrzeit(ereignis1, ereignis2)).toBe(0);
    });

    it("should_placeEreignisseWithoutUhrzeitLast_when_compared", () => {
      const withTime = prepareEreignis()
        .ereignisart(EreignisartEnum.Vorfall)
        .uhrzeit(new Date("2025-04-28T08:15:00"))
        .build();

      const withoutTime = prepareEreignis()
        .ereignisart(EreignisartEnum.Vorfall)
        .uhrzeit(undefined)
        .build();

      expect(compareEreignisseByUhrzeit(withTime, withoutTime)).toBeLessThan(0);
      expect(compareEreignisseByUhrzeit(withoutTime, withTime)).toBeGreaterThan(
        0
      );
    });
  });
});
