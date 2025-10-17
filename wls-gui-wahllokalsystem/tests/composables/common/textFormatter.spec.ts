import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it } from "vitest";

import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

describe("dateTimeFormatter.ts", () => {
  const { getStimmzettelTermForWahl } = useTextFormatter();
  const { prepareUser } = useUserTestDataFactory();
  const { prepareWahl } = useWahlTestDataFactory();

  beforeEach(() => {
    setActivePinia(createPinia());
  });

  describe("getStimmzettelTermForWahl", () => {
    it("should_returnStimmzettel_when_usersWahlbezirkArtIsUWB", () => {
      const userStore = useUserStore();
      const wahlenStore = useWahlenStore();
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      const wahl1 = prepareWahl().nummer("1").build();
      const wahl2 = prepareWahl().nummer("2").build();
      const wahl3 = prepareWahl().nummer("3").build();

      wahlenStore.wahlenState.wahlen = [wahl1, wahl2, wahl3];

      for (const wahl of wahlenStore.wahlenState.wahlen) {
        expect(getStimmzettelTermForWahl(wahl)).toStrictEqual("Stimmzettel");
      }
    });

    it("should_returnStimmzettelumschlaege_when_usersWahlbezirkArtIsBWBAndOnlyOneWahlExists", () => {
      const userStore = useUserStore();
      const wahlenStore = useWahlenStore();
      const wahl = prepareWahl().nummer("1").build();

      userStore.setUser(
        prepareUser()
          .wahlbezirksArt(WahlbezirksArtEnum.BWB)
          .wahlMetaData([
            {
              wahlbezirkID: "wahlbezirkID",
              wahlID: wahl.wahlID,
              wahlnummer: "0",
            },
          ])
          .build()
      );
      wahlenStore.wahlenState.wahlen = [wahl];

      expect(getStimmzettelTermForWahl(wahl)).toStrictEqual(
        "Stimmzettelumschläge"
      );
    });

    it("should_returnStimmzettelumschlaegeForFirstWahlAndStimmzettelForAllOtherWahlen_when_usersWahlbezirkArtIsBWBAndMultipleWahlenExists", () => {
      const userStore = useUserStore();
      const wahlenStore = useWahlenStore();
      const wahl1 = prepareWahl().nummer("1").build();
      const wahl2 = prepareWahl().nummer("2").build();
      const wahl3 = prepareWahl().nummer("3").build();

      userStore.setUser(
        prepareUser()
          .wahlbezirksArt(WahlbezirksArtEnum.BWB)
          .wahlMetaData([
            {
              wahlbezirkID: "wahlbezirkID1",
              wahlID: wahl1.wahlID,
              wahlnummer: "0",
            },
            {
              wahlbezirkID: "wahlbezirkID2",
              wahlID: wahl2.wahlID,
              wahlnummer: "1",
            },
            {
              wahlbezirkID: "wahlbezirkID3",
              wahlID: wahl3.wahlID,
              wahlnummer: "2",
            },
          ])
          .build()
      );
      wahlenStore.wahlenState.wahlen = [wahl1, wahl2, wahl3];

      for (let i = 0; i++; i <= wahlenStore.wahlenState.wahlen.length) {
        if (i == 0) {
          expect(
            getStimmzettelTermForWahl(wahlenStore.wahlenState.wahlen[i])
          ).toStrictEqual("Stimmzettelumschläge");
        } else {
          expect(
            getStimmzettelTermForWahl(wahlenStore.wahlenState.wahlen[i])
          ).toStrictEqual("Stimmzettel");
        }
      }
    });
  });
});
