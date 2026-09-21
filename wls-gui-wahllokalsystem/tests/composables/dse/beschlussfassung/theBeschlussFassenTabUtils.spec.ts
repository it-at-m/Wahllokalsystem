import { createTestingPinia } from "@pinia/testing";
import { usePersistedStimmzettelTestDataFactory } from "@tests/utils/dse/PersistedStimmzettelTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useTheBeschlussFassenTabUtils } from "@/composables/dse/beschlussfassung/theBeschlussFassenTabUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { prepareUser } = useUserTestDataFactory();
const { preparePersistedStimmzettel, createPersistedStimmzettel } =
  usePersistedStimmzettelTestDataFactory();

describe("theBeschlussFassenTabUtils.ts", () => {
  let userStore: ReturnType<typeof useUserStore>;
  let unitUnderTest: ReturnType<typeof useTheBeschlussFassenTabUtils>;

  beforeEach(() => {
    const testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
    userStore = useUserStore(testPinia);

    unitUnderTest = useTheBeschlussFassenTabUtils();
  });

  describe("updateBeschlussgruendeBasedOnStimmzettelAndGueltigkeit", () => {
    const commonGueltigGruende = [
      "Wählerwille ist zweifelsfrei erkennbar (lila Notiz auf dem Stimmzettel)",
      "Mehr als 3 Stimmen bei mind. einer Person und 80 Stimmen gesamt nicht überschritten",
      "keine Reststimmenvergabe möglich, Einzelstimmen und mehrere Kopfleistenkreuze",
      "einzelne Stimmen ungültig",
    ];
    const commonUngueltigGruende = [
      "Wählerwille ist nicht zweifelsfrei erkennbar",
      "mehr als 80 Einzelstimmen oder mehrere Kopfleistenkreuze ohne Einzelstimmen",
      "Stimmzettel ist mit einem besonderen Merkmal, Zusatz oder Vorbehalt versehen",
      "Stimmzettel ist nicht amtlich hergestellt (zum Beispiel von einer anderen Gemeinde)",
    ];
    const bwbGueltig = [
      "Mehrere gleich gekennzeichnete Stimmzettel im Umschlag",
      "Mehrere Stimmzettel im Umschlag, einer gekennzeichnet, die anderen leer",
    ];
    const bwbUngueltig = [
      "Mehrere unterschiedlich gekennzeichnete Stimmzettel im Umschlag",
    ];
    const customGrund = "kaffee ausgeschüttet";

    function setUserBwb(isBwb: boolean) {
      userStore.user = prepareUser()
        .wahlbezirksArt(isBwb ? WahlbezirksArtEnum.BWB : WahlbezirksArtEnum.UWB)
        .build();
    }

    const systemBeschlussgrundMatchingGueltigeGruende = {
      reason: SystemBeschlussgrundReasonEnum.KeineReststimmenvergabeMoeglich,
    };
    const systembeschlussgrundMatchingUngueltigeGruende = {
      reason:
        SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenOderListenkreuze,
    };
    const wahlvorstandBeschlussgrundMatchingGueltigeGruende = {
      text: commonGueltigGruende[0],
    };
    const wahlvorstandBeschlussgrundMatchingUngueltigeGruende = {
      text: commonUngueltigGruende[0],
    };

    const mockedStimmzettel = preparePersistedStimmzettel()
      .systemBeschlussvorschlag([
        systemBeschlussgrundMatchingGueltigeGruende,
        systembeschlussgrundMatchingUngueltigeGruende,
      ])
      .wahlvorstandBeschlussvorschlag([
        wahlvorstandBeschlussgrundMatchingGueltigeGruende,
        wahlvorstandBeschlussgrundMatchingUngueltigeGruende,
      ])
      .build();
    const mockedStimmzettelWithAnderenGruenden = preparePersistedStimmzettel()
      .systemBeschlussvorschlag([])
      .wahlvorstandBeschlussvorschlag([{ text: customGrund }])
      .build();

    it.each([
      {
        isBwb: false,
        isGueltig: true,
        expectedLength: commonGueltigGruende.length,
      },
      {
        isBwb: false,
        isGueltig: false,
        expectedLength: commonUngueltigGruende.length,
      },
      {
        isBwb: true,
        isGueltig: true,
        expectedLength: commonGueltigGruende.length + bwbGueltig.length,
      },
      {
        isBwb: true,
        isGueltig: false,
        expectedLength: commonUngueltigGruende.length + bwbUngueltig.length,
      },
    ])(
      "should_returnCorrectBeschlussGruende_when_isBwbIs'$isBwb'AndStimmzettelIsGueltigIs'$isGueltig'",
      ({ isBwb, isGueltig, expectedLength }) => {
        setUserBwb(isBwb);

        const result =
          unitUnderTest.updateBeschlussgruendeBasedOnStimmzettelAndGueltigkeit(
            isGueltig,
            createPersistedStimmzettel()
          );

        expect(result.beschlussgruende).toHaveLength(expectedLength);
      }
    );

    it("should_returnSelectedGueltigGruende_when_stimmzettelIsGueltigAndSomeGruendeAreSelectedInStimmzettel", () => {
      setUserBwb(true);

      const result =
        unitUnderTest.updateBeschlussgruendeBasedOnStimmzettelAndGueltigkeit(
          true,
          mockedStimmzettel
        );

      expect(result.beschlussgruende).toHaveLength(
        commonGueltigGruende.length + bwbGueltig.length
      );

      const selected = result.beschlussgruende
        .filter((option) => option.selected)
        .map((option) => option.grund);
      expect(selected).toEqual(
        expect.arrayContaining([
          commonGueltigGruende[2],
          commonGueltigGruende[0],
        ])
      );
      expect(selected).not.toEqual(
        expect.arrayContaining([
          commonUngueltigGruende[1],
          commonUngueltigGruende[0],
        ])
      );
    });

    it("should_returnSelectedUngueltigGruende_when_stimmzettelIsUngueltigAndSomeGruendeAreSelectedInStimmzettel", () => {
      setUserBwb(true);

      const result =
        unitUnderTest.updateBeschlussgruendeBasedOnStimmzettelAndGueltigkeit(
          false,
          mockedStimmzettel
        );

      expect(result.beschlussgruende).toHaveLength(
        commonUngueltigGruende.length + bwbUngueltig.length
      );

      const selected = result.beschlussgruende
        .filter((o) => o.selected)
        .map((o) => o.grund);
      expect(selected).toEqual(
        expect.arrayContaining([
          commonUngueltigGruende[1],
          commonUngueltigGruende[0],
        ])
      );
      expect(selected).not.toEqual(
        expect.arrayContaining([
          commonGueltigGruende[2],
          commonGueltigGruende[0],
        ])
      );
    });

    it("should_returnAndereGruende_when_stimmzettelGueltigkeitIs'$isGueltig'AndCustomGruendeAreSelectedInStimmzettel", () => {
      setUserBwb(true);

      const result =
        unitUnderTest.updateBeschlussgruendeBasedOnStimmzettelAndGueltigkeit(
          true,
          mockedStimmzettelWithAnderenGruenden
        );

      expect(result.beschlussgruende).toHaveLength(
        commonGueltigGruende.length + bwbGueltig.length
      );
      expect(result.beschlussgruende.some((o) => o.selected)).toBe(false);
      expect(result.andererGrund).toBe(customGrund);
    });

    it("should_returnEmptyList_when_stimmzettelGueltigkeitIsNull", () => {
      const result =
        unitUnderTest.updateBeschlussgruendeBasedOnStimmzettelAndGueltigkeit(
          null,
          createPersistedStimmzettel()
        );

      expect(result).toStrictEqual({ andererGrund: "", beschlussgruende: [] });
    });
  });
});
