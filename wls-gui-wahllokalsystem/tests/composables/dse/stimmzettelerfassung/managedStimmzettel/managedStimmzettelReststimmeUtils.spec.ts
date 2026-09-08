import { useManagedStimmzettelTestDataFactory } from "@tests/utils/dse/ManagedStimmzettelTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeAll, beforeEach, describe, expect, it } from "vitest";
import { ref } from "vue";

import { useManagedStimmzettelReststimmeUtils } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/managedStimmzettelReststimmeUtils.ts";
import { useKopfdatenStore } from "@/stores/kopfdatenStore.ts";
import { KopfdatenStimmzettelgebietsartEnum } from "@/types/kopfdaten/KopfdatenStimmzettelgebietsartEnum.ts";

describe("managedStimmzettelReststimmeUtils.ts", () => {
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

  describe("resetError", () => {
    it.each([true, false])(
      "should_setSystemErrorFalse_when_calledAndCurrentErrorStateIs'%s'",
      (isErrorSet) => {
        const unitUnderTest = useManagedStimmzettelReststimmeUtils(
          ref(prepareManagedStimmzettelStimmzettel().build()),
          ref(3),
          1
        );
        unitUnderTest.hasSystemErrorToManyListenKreuze.value = isErrorSet;

        unitUnderTest.resetError();

        expect(unitUnderTest.hasSystemErrorToManyListenKreuze.value).toBe(
          false
        );
      }
    );
  });
});
