import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { describe, expect, it } from "vitest";
import { ref } from "vue";

import { useStimmzettelGueltigeKandidatenstimmenAnzeigenCardUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelGueltigeKandidatenstimmenAnzeigenCardUtils.ts";

const { prepareWahlvorschlag, prepareKandidat } =
  useWahlvorschlaegeTestDataFactory();

const {
  preparePersistedStimmzettel,
  preparePersistedStimmzettelWahlvorschlag,
  preparePersistedStimmzettelKandidat,
} = useStimmzettelTestDataFactory();

describe("stimmzettelGueltigeKandidatenstimmenUtils", () => {
  it("should_calculateGueltigeKandidatenstimmen_ when_votesAcrossMultipleStimmzettel", () => {
    const wv1 = prepareWahlvorschlag()
      .identifikator("wv1")
      .ordnungszahl(1)
      .kandidaten([
        prepareKandidat().identifikator("k1").build(),
        prepareKandidat().identifikator("k2").build(),
      ])
      .build();
    const wv2 = prepareWahlvorschlag()
      .identifikator("wv2")
      .ordnungszahl(2)
      .kandidaten([prepareKandidat().identifikator("k3").build()])
      .build();

    const stimmzettel1 = preparePersistedStimmzettel()
      .wahlvorschlaege([
        preparePersistedStimmzettelWahlvorschlag()
          .wahlvorschlagID("wv1")
          .kandidaten([
            preparePersistedStimmzettelKandidat()
              .kandidatId("k1")
              .votesByVoter(3)
              .votesByWahlvorschlag(2)
              .build(),
            preparePersistedStimmzettelKandidat()
              .kandidatId("k2")
              .votesByVoter(1)
              .votesByWahlvorschlag(null)
              .build(),
          ])
          .build(),
        preparePersistedStimmzettelWahlvorschlag()
          .wahlvorschlagID("wv2")
          .kandidaten([
            preparePersistedStimmzettelKandidat()
              .kandidatId("k3")
              .votesByVoter(null)
              .votesByWahlvorschlag(4)
              .build(),
          ])
          .build(),
      ])
      .build();

    const stimmzettel2 = preparePersistedStimmzettel()
      .wahlvorschlaege([
        preparePersistedStimmzettelWahlvorschlag()
          .wahlvorschlagID("wv1")
          .kandidaten([
            preparePersistedStimmzettelKandidat()
              .kandidatId("k1")
              .votesByVoter(2)
              .votesByWahlvorschlag(1)
              .build(),
            preparePersistedStimmzettelKandidat()
              .kandidatId("k2")
              .votesByVoter(null)
              .votesByWahlvorschlag(2)
              .build(),
          ])
          .build(),
        preparePersistedStimmzettelWahlvorschlag()
          .wahlvorschlagID("wv2")
          .kandidaten([
            preparePersistedStimmzettelKandidat()
              .kandidatId("k3")
              .votesByVoter(1)
              .votesByWahlvorschlag(null)
              .build(),
          ])
          .build(),
      ])
      .build();

    const stimmzettelListe = [stimmzettel1, stimmzettel2];
    const wahlvorschlaege = [wv1, wv2];

    const { wahlvorschlaegeWithKandidatenErgebnissen } =
      useStimmzettelGueltigeKandidatenstimmenAnzeigenCardUtils(
        ref(stimmzettelListe),
        ref(wahlvorschlaege)
      );

    const result = wahlvorschlaegeWithKandidatenErgebnissen.value;
    expect(result).toHaveLength(2);

    const resultWv1 = result.find((r) => r.identifikator === "wv1");
    if (resultWv1) {
      expect(resultWv1.ordnungszahl).toBe(1);
      expect(resultWv1.kandidatenErgebnisse).toHaveLength(2);
      const resultWv1K1 = resultWv1.kandidatenErgebnisse.find(
        (ke) => ke.kandidat.identifikator === "k1"
      );
      const resultWv1K2 = resultWv1.kandidatenErgebnisse.find(
        (ke) => ke.kandidat.identifikator === "k2"
      );
      if (resultWv1K1) {
        expect(resultWv1K1.ergebnis).toStrictEqual({
          wahlvorschlagID: "wv1",
          kandidatID: "k1",
          wahlvorschlagsOrdnungszahl: 1,
          ergebnis: 8,
          numIndex: null,
        });
      }
      if (resultWv1K2) {
        expect(resultWv1K2.ergebnis).toStrictEqual({
          wahlvorschlagID: "wv1",
          kandidatID: "k2",
          wahlvorschlagsOrdnungszahl: 1,
          ergebnis: 3,
          numIndex: null,
        });
      }
    }

    const resultWv2 = result.find((r) => r.identifikator === "wv2");
    if (resultWv2) {
      expect(resultWv2.ordnungszahl).toBe(2);
      expect(resultWv2.kandidatenErgebnisse).toHaveLength(1);
      const resultWv2K3 = resultWv2.kandidatenErgebnisse.find(
        (ke) => ke.kandidat.identifikator === "k3"
      );
      if (resultWv2K3) {
        expect(resultWv2K3.ergebnis).toStrictEqual({
          wahlvorschlagID: "wv2",
          kandidatID: "k3",
          wahlvorschlagsOrdnungszahl: 2,
          ergebnis: 5,
          numIndex: null,
        });
      }
    }
  });

  it("should_ignoreNonMatchingEntriesAndTreatNullVotesAsZero_when_votesAreNull", () => {
    const wv = prepareWahlvorschlag()
      .identifikator("wvX")
      .ordnungszahl(99)
      .kurzname("WVX")
      .kandidaten([
        prepareKandidat().identifikator("kx").build(),
        prepareKandidat().identifikator("ky").build(),
      ])
      .build();

    const stimmzettel = preparePersistedStimmzettel()
      .wahlvorschlaege([
        preparePersistedStimmzettelWahlvorschlag()
          .wahlvorschlagID("wvX")
          .kandidaten([
            preparePersistedStimmzettelKandidat()
              .kandidatId("kx")
              .votesByVoter(null)
              .votesByWahlvorschlag(null)
              .build(),
          ])
          .build(),
        preparePersistedStimmzettelWahlvorschlag()
          .wahlvorschlagID("otherWV")
          .kandidaten([
            preparePersistedStimmzettelKandidat()
              .kandidatId("kx")
              .votesByVoter(100)
              .votesByWahlvorschlag(100)
              .build(),
          ])
          .build(),
      ])
      .build();

    const { wahlvorschlaegeWithKandidatenErgebnissen } =
      useStimmzettelGueltigeKandidatenstimmenAnzeigenCardUtils(
        ref([stimmzettel]),
        ref([wv])
      );

    const result = wahlvorschlaegeWithKandidatenErgebnissen.value[0];
    expect(result.identifikator).toBe("wvX");
    expect(result.ordnungszahl).toBe(99);
    expect(result.kandidatenErgebnisse).toHaveLength(2);

    const kandidat1 = result.kandidatenErgebnisse.find(
      (ke) => ke.kandidat.identifikator === "kx"
    );
    const kandidat2 = result.kandidatenErgebnisse.find(
      (ke) => ke.kandidat.identifikator === "ky"
    );

    if (kandidat1) {
      expect(kandidat1.ergebnis).toStrictEqual({
        wahlvorschlagID: "wvX",
        kandidatID: "kx",
        wahlvorschlagsOrdnungszahl: 99,
        ergebnis: 0,
        numIndex: null,
      });
    }

    if (kandidat2) {
      expect(kandidat2.ergebnis).toStrictEqual({
        wahlvorschlagID: "wvX",
        kandidatID: "ky",
        wahlvorschlagsOrdnungszahl: 99,
        ergebnis: 0,
        numIndex: null,
      });
    }
  });
});
