import { createTestingPinia } from "@pinia/testing";
import { usePersistedStimmzettelTestDataFactory } from "@tests/utils/dse/PersistedStimmzettelTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useBeschlussgrundTools } from "@/composables/dse/beschlussfassung/beschlussgrundTools.ts";
import { useTheBeschlussFassenTabUtils } from "@/composables/dse/beschlussfassung/theBeschlussFassenTabUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  mapGruendeToBeschlussgrundOptions: vi.fn((gruende: string[]) =>
    gruende.map((g) => ({ grund: g, selected: false }))
  ),
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

const { prepareUser } = useUserTestDataFactory();
const {
  createPersistedStimmzettel,
  createStimmzettelWahlvorstandBeschlussgrund,
  createStimmzettelSystemBeschlussgrund,
} = usePersistedStimmzettelTestDataFactory();

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

  describe("createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit", () => {
    it("should_callMapperAndSettersWithStimmzettelGruende_when_stimmzettelGiven", () => {
      userStore.user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.UWB)
        .build();

      const stimmzettel = createPersistedStimmzettel();
      stimmzettel.systemBeschlussvorschlag = [
        createStimmzettelSystemBeschlussgrund(),
      ];
      stimmzettel.wahlvorstandBeschlussvorschlag = [
        createStimmzettelWahlvorstandBeschlussgrund(),
      ];

      mockDefinitions.setBeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList.mockReturnValue(
        ""
      );

      const result =
        unitUnderTest.createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit(
          true,
          stimmzettel
        );

      expect(
        mockDefinitions.mapGruendeToBeschlussgrundOptions
      ).toHaveBeenCalled();
      expect(
        mockDefinitions.setSystemBeschlussgruendeTrueWhenFoundInStimmzettel
      ).toHaveBeenCalledWith(
        stimmzettel.systemBeschlussvorschlag,
        result.beschlussgruende
      );
      expect(
        mockDefinitions.setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel
      ).toHaveBeenCalledWith(
        stimmzettel.wahlvorstandBeschlussvorschlag,
        result.beschlussgruende
      );
    });

    it("should_useEmptyArraysForSetters_when_stimmzettelHasUndefinedBeschluesse", () => {
      userStore.user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.UWB)
        .build();

      const stimmzettel = createPersistedStimmzettel();
      stimmzettel.systemBeschlussvorschlag = [];
      stimmzettel.wahlvorstandBeschlussvorschlag = [];

      mockDefinitions.setBeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList.mockReturnValue(
        ""
      );

      const result =
        unitUnderTest.createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit(
          true,
          stimmzettel
        );

      expect(
        mockDefinitions.setSystemBeschlussgruendeTrueWhenFoundInStimmzettel
      ).toHaveBeenCalledWith([], result.beschlussgruende);
      expect(
        mockDefinitions.setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel
      ).toHaveBeenCalledWith([], result.beschlussgruende);
    });

    it("should_setAndererGrund_fromSetterReturnValue", () => {
      userStore.user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.BWB)
        .build();

      mockDefinitions.setBeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList.mockReturnValue(
        "grund"
      );

      const result =
        unitUnderTest.createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit(
          false,
          undefined
        );

      expect(result.andererGrund).toBe("grund");
    });

    it("should_returnEmptyList_when_stimmzettelGueltigkeitIsNull", () => {
      const result =
        unitUnderTest.createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit(
          null,
          createPersistedStimmzettel()
        );

      expect(result).toStrictEqual({ andererGrund: "", beschlussgruende: [] });
    });

    it("should_returnUngueltigeBeschlussgruendeAsUnionOfSystemAndWahlvorstandReasons_when_calledForStimmzettelThatIsUngueltig", () => {
      userStore.user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.BWB)
        .build();

      const stimmzettel = createPersistedStimmzettel();
      stimmzettel.systemBeschlussvorschlag = [];
      stimmzettel.wahlvorstandBeschlussvorschlag = [];

      const result =
        unitUnderTest.createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit(
          false,
          stimmzettel
        );

      expect(
        mockDefinitions.mapGruendeToBeschlussgrundOptions
      ).toHaveBeenCalled();
      expect(
        mockDefinitions.setSystemBeschlussgruendeTrueWhenFoundInStimmzettel
      ).toHaveBeenCalledWith([], result.beschlussgruende);
      expect(
        mockDefinitions.setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel
      ).toHaveBeenCalledWith([], result.beschlussgruende);

      const { allBeschlussGruendeUngueltig } = useBeschlussgrundTools();
      const expected = Array.from(allBeschlussGruendeUngueltig).sort();
      const actual = result.beschlussgruende.map((o) => o.grund).sort();
      expect(actual).toStrictEqual(expected);
    });
  });
});
