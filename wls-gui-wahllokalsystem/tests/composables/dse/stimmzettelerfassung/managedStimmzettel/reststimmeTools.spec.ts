import { useManagedStimmzettelTestDataFactory } from "@tests/utils/dse/ManagedStimmzettelTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeAll, beforeEach, describe, expect, it } from "vitest";
import { ref } from "vue";

import { useReststimmeTools } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/reststimmeTools.ts";
import { useKopfdatenStore } from "@/stores/kopfdatenStore.ts";
import { KopfdatenStimmzettelgebietsartEnum } from "@/types/kopfdaten/KopfdatenStimmzettelgebietsartEnum.ts";

describe("reststimmeTools.ts", () => {
  const {
    prepareManagedStimmzettelStimmzettel,
    prepareManagedStimmzettelWahlvorschlag,
    prepareManagedStimmzettelKandidat,
  } = useManagedStimmzettelTestDataFactory();

  const wahlId = "wahl-1";

  beforeAll(() => {
    setActivePinia(createPinia());
  });

  beforeEach(() => {
    const kdStore = useKopfdatenStore();
    kdStore.kopfdaten = [
      {
        wahlID: wahlId,
        wahlbezirkID: "wb-1",
        gemeinde: "",
        stimmzettelgebietsart: KopfdatenStimmzettelgebietsartEnum.Sb,
        stimmzettelgebietsnummer: "",
        stimmzettelgebietsname: "",
        wahlname: "",
        wahlbezirknummer: "",
        maximalErlaubteStimmenProWaehler: 3,
      },
    ];
  });

  afterEach(() => {
    const kdStore = useKopfdatenStore();
    kdStore.kopfdaten = [];
  });

  describe("selectWahlvorschlag", () => {
    it("should_selectWahlvorschlagAndSetReststimmen_when_called", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .einzelstimmen(null)
        .ungueltigeStimmen(0)
        .reststimmen(null)
        .durchgestrichen(false)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
        .einzelstimmen(null)
        .ungueltigeStimmen(0)
        .reststimmen(null)
        .durchgestrichen(false)
        .build();
      const wv = prepareManagedStimmzettelWahlvorschlag()
        .selected(false)
        .kandidaten([k1, k2])
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wv])
        .build();

      const { selectWahlvorschlag } = useReststimmeTools(
        wahlId,
        ref({
          einzelstimmen: 0,
          ungueltigeStimmen: 0,
          reststimmen: 0,
          streichungen: 0,
        }),
        ref(stimmzettel)
      );

      selectWahlvorschlag(wv);

      expect(k1.reststimmen).toBe(1);
      expect(k2.reststimmen).toBe(1);
      expect(wv.selected).toBe(true);
    });
  });

  describe("deselectWahlvorschlag", () => {
    it("should_deselectWahlvorschlagAndResetReststimmen_when_called", () => {
      const k1 = prepareManagedStimmzettelKandidat().reststimmen(1).build();
      const k2 = prepareManagedStimmzettelKandidat().reststimmen(1).build();
      const wv = prepareManagedStimmzettelWahlvorschlag()
        .selected(true)
        .kandidaten([k1, k2])
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wv])
        .build();

      const { deselectWahlvorschlag } = useReststimmeTools(
        wahlId,
        ref({
          einzelstimmen: 0,
          ungueltigeStimmen: 0,
          reststimmen: 2,
          streichungen: 0,
        }),
        ref(stimmzettel)
      );

      deselectWahlvorschlag(wv);

      expect(k1.reststimmen).toBe(0);
      expect(k2.reststimmen).toBe(0);
      expect(wv.selected).toBe(false);
    });
  });

  describe("updateReststimmenWhenVotesAdded", () => {
    it("should_updateReststimmen_when_called", () => {
      const k1 = prepareManagedStimmzettelKandidat().reststimmen(1).build();
      const k2 = prepareManagedStimmzettelKandidat().reststimmen(1).build();
      const wv = prepareManagedStimmzettelWahlvorschlag()
        .selected(true)
        .kandidaten([k1, k2])
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wv])
        .build();

      const stimmenSummary = ref({
        einzelstimmen: 2,
        ungueltigeStimmen: 0,
        reststimmen: 2,
        streichungen: 0,
      });
      const { updateReststimmenWhenVotesAdded } = useReststimmeTools(
        wahlId,
        stimmenSummary,
        ref(stimmzettel)
      );

      updateReststimmenWhenVotesAdded();

      expect(k1.reststimmen).toBe(1);
      expect(k2.reststimmen).toBe(0);
    });
  });

  describe("updateReststimmenWhenVotesRemoved", () => {
    it("should_updateReststimmen_when_called", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .einzelstimmen(0)
        .ungueltigeStimmen(0)
        .reststimmen(0)
        .durchgestrichen(false)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
        .einzelstimmen(0)
        .ungueltigeStimmen(0)
        .reststimmen(0)
        .durchgestrichen(false)
        .build();
      const wv = prepareManagedStimmzettelWahlvorschlag()
        .selected(true)
        .kandidaten([k1, k2])
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wv])
        .build();

      const stimmenSummary = ref({
        einzelstimmen: 2,
        ungueltigeStimmen: 0,
        reststimmen: 0,
        streichungen: 0,
      });
      const { updateReststimmenWhenVotesRemoved } = useReststimmeTools(
        wahlId,
        stimmenSummary,
        ref(stimmzettel)
      );

      updateReststimmenWhenVotesRemoved();

      const assigned = [k1.reststimmen, k2.reststimmen].filter(
        (v) => v === 1
      ).length;
      expect(assigned).toBe(1);
    });
  });
});
