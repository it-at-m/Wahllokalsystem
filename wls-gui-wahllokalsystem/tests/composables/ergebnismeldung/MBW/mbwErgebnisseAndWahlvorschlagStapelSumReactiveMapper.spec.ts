import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { describe, expect, it } from "vitest";
import { computed, nextTick, ref } from "vue";

import { useStringNumberMapTools } from "@/composables/common/stringNumberMapTools.ts";
import { useMbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper } from "@/composables/ergebnismeldung/MBW/mbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper.ts";

const { createWahlvorschlag, prepareWahlvorschlag } =
  useWahlvorschlaegeTestDataFactory();

describe("mbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper.ts", () => {
  describe("wahlvorschlaegeErgebnisseStapelAAndB", () => {
    it("should_returnErgebnisseForBothStapel_when_wahlvorschlaegeAndSumsAreGiven", () => {
      const ersterWahlvorschlag = prepareWahlvorschlag()
        .identifikator("erster-wahlvorschlag")
        .build();
      const zweiterWahlvorschlag = prepareWahlvorschlag()
        .identifikator("zweiter-wahlvorschlag")
        .build();

      const unitUnderTest =
        useMbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper(
          computed(() => [ersterWahlvorschlag, zweiterWahlvorschlag]),
          computed(() =>
            useStringNumberMapTools(
              new Map([
                [ersterWahlvorschlag.identifikator, 12],
                [zweiterWahlvorschlag.identifikator, 34],
              ])
            )
          ),
          computed(() =>
            useStringNumberMapTools(
              new Map([
                [ersterWahlvorschlag.identifikator, 56],
                [zweiterWahlvorschlag.identifikator, 78],
              ])
            )
          )
        );

      expect(
        unitUnderTest.wahlvorschlaegeErgebnisseStapelAAndB.value
      ).toStrictEqual([
        {
          wahlvorschlag: ersterWahlvorschlag,
          ergebnisStapelA: {
            ergebnis: 12,
            wahlvorschlagsOrdnungszahl: null,
            wahlvorschlagID: null,
            numIndex: null,
            kandidatID: null,
          },
          ergebnisStapelB: {
            ergebnis: 56,
            wahlvorschlagsOrdnungszahl: null,
            wahlvorschlagID: null,
            numIndex: null,
            kandidatID: null,
          },
        },
        {
          wahlvorschlag: zweiterWahlvorschlag,
          ergebnisStapelA: {
            ergebnis: 34,
            wahlvorschlagsOrdnungszahl: null,
            wahlvorschlagID: null,
            numIndex: null,
            kandidatID: null,
          },
          ergebnisStapelB: {
            ergebnis: 78,
            wahlvorschlagsOrdnungszahl: null,
            wahlvorschlagID: null,
            numIndex: null,
            kandidatID: null,
          },
        },
      ]);
    });

    it("should_returnZeroForBothStapel_when_wahlvorschlagIsNotPresentInSumTools", () => {
      const wahlvorschlag = createWahlvorschlag();
      const unitUnderTest =
        useMbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper(
          computed(() => [wahlvorschlag]),
          computed(() => useStringNumberMapTools(new Map())),
          computed(() => useStringNumberMapTools(new Map()))
        );

      expect(
        unitUnderTest.wahlvorschlaegeErgebnisseStapelAAndB.value
      ).toMatchObject([
        {
          wahlvorschlag,
          ergebnisStapelA: { ergebnis: 0 },
          ergebnisStapelB: { ergebnis: 0 },
        },
      ]);
    });

    it("should_returnEmptyList_when_noWahlvorschlaegeAreGiven", () => {
      const unitUnderTest =
        useMbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper(
          computed<Wahlvorschlag[]>(() => []),
          computed(() => useStringNumberMapTools(new Map())),
          computed(() => useStringNumberMapTools(new Map()))
        );

      expect(
        unitUnderTest.wahlvorschlaegeErgebnisseStapelAAndB.value
      ).toStrictEqual([]);
    });

    it("should_recalculateResults_when_reactiveInputValuesChange", async () => {
      const ersterWahlvorschlag = prepareWahlvorschlag()
        .identifikator("erster-wahlvorschlag")
        .build();
      const zweiterWahlvorschlag = prepareWahlvorschlag()
        .identifikator("zweiter-wahlvorschlag")
        .build();

      const wahlvorschlaege = ref([ersterWahlvorschlag]);
      const sumtoolStapelA = ref(
        useStringNumberMapTools(
          new Map([[ersterWahlvorschlag.identifikator, 12]])
        )
      );
      const sumtoolStapelB = ref(
        useStringNumberMapTools(
          new Map([[ersterWahlvorschlag.identifikator, 56]])
        )
      );
      const unitUnderTest =
        useMbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper(
          computed(() => wahlvorschlaege.value),
          computed(() => sumtoolStapelA.value),
          computed(() => sumtoolStapelB.value)
        );

      expect(
        unitUnderTest.wahlvorschlaegeErgebnisseStapelAAndB.value
      ).toMatchObject([
        {
          wahlvorschlag: ersterWahlvorschlag,
          ergebnisStapelA: { ergebnis: 12 },
          ergebnisStapelB: { ergebnis: 56 },
        },
      ]);

      wahlvorschlaege.value = [zweiterWahlvorschlag];
      sumtoolStapelA.value = useStringNumberMapTools(
        new Map([[zweiterWahlvorschlag.identifikator, 34]])
      );
      sumtoolStapelB.value = useStringNumberMapTools(
        new Map([[zweiterWahlvorschlag.identifikator, 78]])
      );
      await nextTick();

      expect(
        unitUnderTest.wahlvorschlaegeErgebnisseStapelAAndB.value
      ).toMatchObject([
        {
          wahlvorschlag: zweiterWahlvorschlag,
          ergebnisStapelA: { ergebnis: 34 },
          ergebnisStapelB: { ergebnis: 78 },
        },
      ]);
    });
  });
});
