import { createTestingPinia } from "@pinia/testing";
import { usePersistedStimmzettelTestDataFactory } from "@tests/utils/dse/PersistedStimmzettelTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useTheBeschlussFassenTabUtils } from "@/composables/dse/beschlussfassung/theBeschlussFassenTabUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  mapGruendeToBeschlussgrundOptions: (gruende: string[]) =>
    gruende.map((g) => ({ grund: g, selected: false })),
  setSystemBeschlussgruendeTrueWhenFoundInStimmzettel: vi.fn(),
  setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel: vi.fn(),
  setBeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList: vi.fn(),
  mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText: vi.fn(),
}));

vi.mock(
  "@/composables/dse/beschlussfassung/beschlussgrundOptionTools.ts",
  () => {
    return {
      useBeschlussgrundOptionTools: () => ({
        mapGruendeToBeschlussgrundOptions:
          mockDefinitions.mapGruendeToBeschlussgrundOptions,
        setSystemBeschlussgruendeTrueWhenFoundInStimmzettel:
          mockDefinitions.setSystemBeschlussgruendeTrueWhenFoundInStimmzettel,
        setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel:
          mockDefinitions.setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel,
        setBeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList:
          mockDefinitions.setBeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList,
      }),
    };
  }
);

vi.mock(
  import("@/composables/dse/beschlussfassung/systemBeschlussgrundReasonEnumTools.ts"),
  () => {
    return {
      useSystemBeschlussgrundReasonEnumTools: () => ({
        mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText:
          mockDefinitions.mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText,
        mapSystemBeschlussgrundReasonEnumToText: vi.fn(),
      }),
    };
  }
);

const { prepareUser } = useUserTestDataFactory();
const { createPersistedStimmzettel, preparePersistedStimmzettel } =
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

    it.each([
      {
        wahlbezirksart: WahlbezirksArtEnum.UWB,
        isGueltig: true,
        expectedLength: commonGueltigGruende.length,
      },
      {
        wahlbezirksart: WahlbezirksArtEnum.UWB,
        isGueltig: false,
        expectedLength: commonUngueltigGruende.length,
      },
      {
        wahlbezirksart: WahlbezirksArtEnum.BWB,
        isGueltig: true,
        expectedLength: commonGueltigGruende.length + bwbGueltig.length,
      },
      {
        wahlbezirksart: WahlbezirksArtEnum.BWB,
        isGueltig: false,
        expectedLength: commonUngueltigGruende.length + bwbUngueltig.length,
      },
    ])(
      "should_returnCorrectBeschlussGruende_when_isBwbIs'$isBwb'AndStimmzettelIsGueltigIs'$isGueltig'",
      ({ wahlbezirksart, isGueltig, expectedLength }) => {
        userStore.user = prepareUser().wahlbezirksArt(wahlbezirksart).build();

        mockDefinitions.setBeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList.mockReturnValue(
          []
        );

        const result =
          unitUnderTest.updateBeschlussgruendeBasedOnStimmzettelAndGueltigkeit(
            isGueltig,
            createPersistedStimmzettel()
          );

        expect(result.beschlussgruende).toHaveLength(expectedLength);
      }
    );

    it("should_returnEmptyList_when_stimmzettelGueltigkeitIsNull", () => {
      const result =
        unitUnderTest.updateBeschlussgruendeBasedOnStimmzettelAndGueltigkeit(
          null,
          createPersistedStimmzettel()
        );

      expect(result).toStrictEqual({ andererGrund: "", beschlussgruende: [] });
    });
  });

  describe("isStimmzettelGueltigBasedOnVormerkungsgruenden", () => {
    it("should_returnTrue_when_stimmzettelHasNoBeschlussgruende", () => {
      const stimmzettel = preparePersistedStimmzettel()
        .systemBeschlussvorschlag([])
        .wahlvorstandBeschlussvorschlag([])
        .build();

      const result =
        unitUnderTest.isStimmzettelGueltigBasedOnVormerkungsgruenden(
          stimmzettel
        );

      expect(result).toStrictEqual(true);
    });
    it("should_returnTrue_when_stimmzettelHasNoUngueltigeBeschlussgruende", () => {
      const stimmzettel = preparePersistedStimmzettel()
        .systemBeschlussvorschlag([
          { reason: SystemBeschlussgrundReasonEnum.EinzelneStimmenUngueltig },
        ])
        .wahlvorstandBeschlussvorschlag([
          {
            text: "keine Reststimmenvergabe möglich, Einzelstimmen und mehrere Kopfleistenkreuze",
          },
        ])
        .build();

      mockDefinitions.mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText.mockReturnValueOnce(
        "einzelne Stimmen ungültig"
      );

      const result =
        unitUnderTest.isStimmzettelGueltigBasedOnVormerkungsgruenden(
          stimmzettel
        );

      expect(result).toStrictEqual(true);
    });

    it("should_returnFalse_when_stimmzettelHasUngueltigeWahlvorstandBeschlussgruende", () => {
      const stimmzettel = preparePersistedStimmzettel()
        .systemBeschlussvorschlag([])
        .wahlvorstandBeschlussvorschlag([
          {
            text: "Stimmzettel ist nicht amtlich hergestellt (zum Beispiel von einer anderen Gemeinde)",
          },
        ])
        .build();

      const result =
        unitUnderTest.isStimmzettelGueltigBasedOnVormerkungsgruenden(
          stimmzettel
        );

      expect(result).toStrictEqual(false);
    });

    it("should_returnFalse_when_stimmzettelHasUngueltigeSystemBeschlussgruende", () => {
      const stimmzettel = preparePersistedStimmzettel()
        .systemBeschlussvorschlag([
          {
            reason:
              SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenOderListenkreuze,
          },
        ])
        .wahlvorstandBeschlussvorschlag([])
        .build();

      mockDefinitions.mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText.mockReturnValueOnce(
        "mehr als 80 Einzelstimmen oder mehrere Kopfleistenkreuze ohne Einzelstimmen"
      );

      const result =
        unitUnderTest.isStimmzettelGueltigBasedOnVormerkungsgruenden(
          stimmzettel
        );

      expect(result).toStrictEqual(false);
    });
  });
});
