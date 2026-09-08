import type { Wahlvorschlag } from "@/types/dse/stimmzettelerfassung/Wahlvorschlag.ts";

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
    prepareManagedStimmzettelKandidatForWahlvorschlag,
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

  describe("selectWahlvorschlag", () => {
    it.each([true, false])(
      "should_setSelectedTrue_when_calledAndCurrentStateIs'%s'",
      (currentWahlvorschlagSelectionState) => {
        const unitUnderTest = useManagedStimmzettelReststimmeUtils(
          ref(prepareManagedStimmzettelStimmzettel().build()),
          ref(3),
          1
        );

        const wahlvorschlag = prepareManagedStimmzettelWahlvorschlag()
          .selected(currentWahlvorschlagSelectionState)
          .build();

        unitUnderTest.selectWahlvorschlag(wahlvorschlag);

        expect(wahlvorschlag.selected).toStrictEqual(true);
      }
    );
  });

  describe("deselectWahlvorschlag", () => {
    it("should_setSelectedFalseAndRemoveAnyReststimmen_when_wahlvorschlagIsSelected", () => {
      const unitUnderTest = useManagedStimmzettelReststimmeUtils(
        ref(prepareManagedStimmzettelStimmzettel().build()),
        ref(3),
        1
      );

      const wahlvorschlag = prepareManagedStimmzettelWahlvorschlag()
        .selected(true)
        .build();

      unitUnderTest.deselectWahlvorschlag(wahlvorschlag);

      expect(wahlvorschlag.selected).toStrictEqual(false);
      wahlvorschlag.kandidaten.forEach((kandidat) =>
        expect(kandidat.reststimmen).toStrictEqual(null)
      );
    });
  });

  describe("refreshWahlvorschlaegeVotes", () => {
    it("should_giveEveryKandidatOfWahlvorschlagOneReststimme_when_oneWahlvorschlagIsSelectedAndTotalNumberOfVotesIsLargeEnough", () => {
      const wahlvorschlag1 = prepareManagedStimmzettelWahlvorschlag()
        .selected(true)
        .build();
      wahlvorschlag1.kandidaten = [
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.1")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.2")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.3")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.4")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.5")
          .nennung(1)
          .build(),
      ];

      const wahlvorschlag2 = prepareManagedStimmzettelWahlvorschlag()
        .selected(false)
        .build();
      wahlvorschlag2.kandidaten = [
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag2, "k2.1")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag2, "k2.2")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag2, "k2.3")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag2, "k2.4")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag2, "k2.5")
          .nennung(1)
          .build(),
      ];
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wahlvorschlag1, wahlvorschlag2])
        .invalideVotes(0)
        .build();

      const unitUnderTest = useManagedStimmzettelReststimmeUtils(
        ref(stimmzettel),
        ref(5),
        1
      );

      unitUnderTest.refreshWahlvorschlaegeVotes();

      wahlvorschlag1.kandidaten.forEach((kandidat) =>
        expect(kandidat.reststimmen).toStrictEqual(1)
      );
      wahlvorschlag2.kandidaten.forEach((kandidat) =>
        expect(kandidat.reststimmen).toStrictEqual(null)
      );
    });

    it("should_giveOnlySomeReststimmenInOrderToKandidaten_when_oneWahlvorschlagIsSelectedButAvailableReststimmenIsLessThanNumberOfRemainingKandidatenInList", () => {
      const wahlvorschlag1 = prepareManagedStimmzettelWahlvorschlag()
        .selected(true)
        .build();
      wahlvorschlag1.kandidaten = [
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.1")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.2")
          .nennung(1)
          .einzelstimmen(0)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.3")
          .nennung(1)
          .reststimmen(0)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.4")
          .nennung(1)
          .ungueltigeStimmen(0)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.5")
          .nennung(1)
          .build(),
      ];

      const wahlvorschlag2 = prepareManagedStimmzettelWahlvorschlag()
        .selected(false)
        .build();
      wahlvorschlag2.kandidaten = [
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k2.1")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k2.2")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k2.3")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k2.4")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k2.5")
          .nennung(1)
          .build(),
      ];
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wahlvorschlag1, wahlvorschlag2])
        .invalideVotes(0)
        .build();

      const unitUnderTest = useManagedStimmzettelReststimmeUtils(
        ref(stimmzettel),
        ref(3),
        1
      );

      unitUnderTest.refreshWahlvorschlaegeVotes();

      wahlvorschlag1.kandidaten.forEach((kandidat, index) => {
        if (index < 3) {
          expect(kandidat.reststimmen).toStrictEqual(1);
        } else {
          expect(kandidat.reststimmen).toStrictEqual(null);
        }
      });
      wahlvorschlag2.kandidaten.forEach((kandidat) =>
        expect(kandidat.reststimmen).toStrictEqual(null)
      );
    });

    it("should_ignoreKandidatWithKennzeichen_when_givingReststimmenOfSingleWahlvorschlag", () => {
      const wahlvorschlag = prepareManagedStimmzettelWahlvorschlag()
        .selected(true)
        .build();
      wahlvorschlag.kandidaten = [
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag, "k1.1").build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag, "k1.2")
          .durchgestrichen(true)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag, "k1.3")
          .einzelstimmen(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag, "k1.4")
          .ungueltigeStimmen(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag, "k1.5").build(),
      ];

      const unitUnderTest = useManagedStimmzettelReststimmeUtils(
        ref(
          prepareManagedStimmzettelStimmzettel()
            .wahlvorschlaege([wahlvorschlag])
            .invalideVotes(0)
            .build()
        ),
        ref(4),
        1
      );
      unitUnderTest.refreshWahlvorschlaegeVotes();

      expect(wahlvorschlag.kandidaten[0].reststimmen).toStrictEqual(1);
      expect(wahlvorschlag.kandidaten[1].reststimmen).toStrictEqual(null);
      expect(wahlvorschlag.kandidaten[2].reststimmen).toStrictEqual(null);
      expect(wahlvorschlag.kandidaten[3].reststimmen).toStrictEqual(null);
      expect(wahlvorschlag.kandidaten[4].reststimmen).toStrictEqual(1);
    });

    it("should_ignoreKandidatWithKennzeichen_when_givingReststimmenOfMultipleWahlvorschlaege", () => {
      const wahlvorschlag1 = prepareManagedStimmzettelWahlvorschlag()
        .selected(true)
        .build();
      wahlvorschlag1.kandidaten = [
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.1").build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.2")
          .durchgestrichen(true)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.3")
          .einzelstimmen(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.4")
          .ungueltigeStimmen(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.5").build(),
      ];
      const wahlvorschlag2 = prepareManagedStimmzettelWahlvorschlag()
        .selected(true)
        .build();
      wahlvorschlag2.kandidaten = [
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag2, "k2.1").build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag2, "k2.2")
          .durchgestrichen(true)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag2, "k2.3")
          .einzelstimmen(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag2, "k2.4")
          .ungueltigeStimmen(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag2, "k2.5").build(),
      ];

      const unitUnderTest = useManagedStimmzettelReststimmeUtils(
        ref(
          prepareManagedStimmzettelStimmzettel()
            .wahlvorschlaege([wahlvorschlag1, wahlvorschlag2])
            .invalideVotes(0)
            .build()
        ),
        ref(8),
        1
      );
      unitUnderTest.refreshWahlvorschlaegeVotes();

      expect(wahlvorschlag1.kandidaten[0].reststimmen).toStrictEqual(1);
      expect(wahlvorschlag1.kandidaten[1].reststimmen).toStrictEqual(null);
      expect(wahlvorschlag1.kandidaten[2].reststimmen).toStrictEqual(null);
      expect(wahlvorschlag1.kandidaten[3].reststimmen).toStrictEqual(null);
      expect(wahlvorschlag1.kandidaten[4].reststimmen).toStrictEqual(1);

      expect(wahlvorschlag2.kandidaten[0].reststimmen).toStrictEqual(1);
      expect(wahlvorschlag2.kandidaten[1].reststimmen).toStrictEqual(null);
      expect(wahlvorschlag2.kandidaten[2].reststimmen).toStrictEqual(null);
      expect(wahlvorschlag2.kandidaten[3].reststimmen).toStrictEqual(null);
      expect(wahlvorschlag2.kandidaten[4].reststimmen).toStrictEqual(1);
    });

    it("should_giveEveryKandidatOfWahlvorschlagOneReststimme_when_twoWahlvorschlaegeAreSelectedAndTotalNumberOfVotesIsLargeEnough", () => {
      const wahlvorschlag1 = prepareManagedStimmzettelWahlvorschlag()
        .selected(true)
        .build();
      wahlvorschlag1.kandidaten = [
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.1")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.2")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.3")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.4")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.5")
          .nennung(1)
          .build(),
      ];

      const wahlvorschlag2 = prepareManagedStimmzettelWahlvorschlag()
        .selected(true)
        .build();
      wahlvorschlag2.kandidaten = [
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k2.1")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k2.2")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k2.3")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k2.4")
          .nennung(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k2.5")
          .nennung(1)
          .build(),
      ];
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wahlvorschlag1, wahlvorschlag2])
        .invalideVotes(0)
        .build();

      const unitUnderTest = useManagedStimmzettelReststimmeUtils(
        ref(stimmzettel),
        ref(10),
        1
      );

      unitUnderTest.refreshWahlvorschlaegeVotes();

      wahlvorschlag1.kandidaten.forEach((kandidat) =>
        expect(kandidat.reststimmen).toStrictEqual(1)
      );
      wahlvorschlag2.kandidaten.forEach((kandidat) =>
        expect(kandidat.reststimmen).toStrictEqual(1)
      );
    });

    it("should_removeGivenReststimmen_when_secondWahlvorschlagIsSelectedButNotEnoughReststimmenAreGiven", () => {
      const wahlvorschlag1 = prepareManagedStimmzettelWahlvorschlag()
        .selected(true)
        .build();
      wahlvorschlag1.kandidaten = [
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.1")
          .reststimmen(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag1, "k1.2")
          .reststimmen(1)
          .build(),
      ];

      const wahlvorschlag2 = prepareManagedStimmzettelWahlvorschlag()
        .selected(true)
        .build();
      wahlvorschlag2.kandidaten = [
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag2, "k2.1")
          .einzelstimmen(1)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag2, "k2.2").build(),
      ];

      const unitUnderTest = useManagedStimmzettelReststimmeUtils(
        ref(
          prepareManagedStimmzettelStimmzettel()
            .wahlvorschlaege([wahlvorschlag1, wahlvorschlag2])
            .invalideVotes(2)
            .build()
        ),
        ref(3),
        1
      );

      unitUnderTest.refreshWahlvorschlaegeVotes();

      wahlvorschlag1.kandidaten.forEach((kandidat) =>
        expect(kandidat.reststimmen).toStrictEqual(null)
      );
      wahlvorschlag2.kandidaten.forEach((kandidat) =>
        expect(kandidat.reststimmen).toStrictEqual(null)
      );
      expect(unitUnderTest.hasSystemErrorToManyListenKreuze.value).toBe(true);
    });

    it("should_onlyGiveOneReststimmeToOneNennung_when_kandidatAlreadyGot2Einzelstimmen", () => {
      const wahlvorschlag = prepareManagedStimmzettelWahlvorschlag()
        .selected(true)
        .build();
      wahlvorschlag.kandidaten = [
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag, "k1", 1)
          .einzelstimmen(2)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag, "k1", 2).build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag, "k1", 3).build(),

        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag, "k2", 1)
          .ungueltigeStimmen(2)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag, "k2", 2).build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag, "k2", 3).build(),

        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag, "k3", 1).build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag, "k3", 2)
          .einzelstimmen(2)
          .build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag, "k3", 3).build(),

        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag, "k4", 1).build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag, "k4", 2).build(),
        _prepareKandidatWithoutAnyKennzeichen(wahlvorschlag, "k4", 3)
          .einzelstimmen(2)
          .build(),
      ];

      const unitUnderTest = useManagedStimmzettelReststimmeUtils(
        ref(
          prepareManagedStimmzettelStimmzettel()
            .wahlvorschlaege([wahlvorschlag])
            .invalideVotes(0)
            .build()
        ),
        ref(1000),
        3
      );

      unitUnderTest.refreshWahlvorschlaegeVotes();

      expect(wahlvorschlag.kandidaten[0].reststimmen).toStrictEqual(null);
      expect(wahlvorschlag.kandidaten[1].reststimmen).toStrictEqual(1);
      expect(wahlvorschlag.kandidaten[2].reststimmen).toStrictEqual(null);

      expect(wahlvorschlag.kandidaten[3].reststimmen).toStrictEqual(null);
      expect(wahlvorschlag.kandidaten[4].reststimmen).toStrictEqual(1);
      expect(wahlvorschlag.kandidaten[5].reststimmen).toStrictEqual(null);

      expect(wahlvorschlag.kandidaten[6].reststimmen).toStrictEqual(1);
      expect(wahlvorschlag.kandidaten[7].reststimmen).toStrictEqual(null);
      expect(wahlvorschlag.kandidaten[8].reststimmen).toStrictEqual(null);

      expect(wahlvorschlag.kandidaten[9].reststimmen).toStrictEqual(1);
      expect(wahlvorschlag.kandidaten[10].reststimmen).toStrictEqual(null);
      expect(wahlvorschlag.kandidaten[11].reststimmen).toStrictEqual(null);
    });

    function _prepareKandidatWithoutAnyKennzeichen(
      owningWahlvorschlag: Wahlvorschlag,
      kandidatID: string,
      nennung = 1
    ) {
      return prepareManagedStimmzettelKandidatForWahlvorschlag(
        owningWahlvorschlag
      )
        .kandidatId(kandidatID)
        .nennung(nennung)
        .durchgestrichen(false)
        .einzelstimmen(null)
        .reststimmen(null)
        .ungueltigeStimmen(null);
    }
  });
});
