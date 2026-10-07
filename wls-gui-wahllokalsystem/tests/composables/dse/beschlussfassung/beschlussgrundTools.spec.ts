import { createTestingPinia } from "@pinia/testing";
import { useBeschlussgrundTestDataFactory } from "@tests/utils/dse/BeschlussgrundTestDataFacytory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useBeschlussgrundTools } from "@/composables/dse/beschlussfassung/beschlussgrundTools.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";
import { WahlvorstandBeschlussvorschlaegeEnum } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussvorschlaegeEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText: vi.fn(),
}));

vi.mock(
  import("@/composables/dse/beschlussfassung/useSystemBeschlussgrundReasonEnumFormatter.ts"),
  () => ({
    useSystemBeschlussgrundReasonEnumFormatter: () => ({
      mapSystemBeschlussgrundReasonEnumToText: vi.fn(),
      mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText:
        mockDefinitions.mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText,
    }),
  })
);

const { createSystemBeschlussgrund, createWahlvorstandBeschlussgrund } =
  useBeschlussgrundTestDataFactory();

describe("useBeschlussgrundTools.ts", () => {
  let unitUnderTest: ReturnType<typeof useBeschlussgrundTools>;
  let userStore: ReturnType<typeof useUserStore>;
  const { prepareUser } = useUserTestDataFactory();

  beforeEach(() => {
    const pinia = createTestingPinia({ stubActions: false, createSpy: vi.fn });
    userStore = useUserStore(pinia);
    unitUnderTest = useBeschlussgrundTools();
  });

  describe("sortWahlvorstandBeschlussgruende", () => {
    const wvGrund1 = {
      ...createWahlvorstandBeschlussgrund(),
      text: "kaffee ausgeschüttet",
    };
    const wvGrund2 = {
      ...createWahlvorstandBeschlussgrund(),
      text: WahlvorstandBeschlussvorschlaegeEnum.NichtAmtlicherStimmzettel,
    };
    const wvGrund3 = {
      ...createWahlvorstandBeschlussgrund(),
      text: WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleNichtZweifelsfreiErkennbar,
    };

    it.each([
      {
        text: "NotSorted",
        wvBeschlussgruende: [wvGrund2, wvGrund1, wvGrund3],
      },
      {
        text: "Sorted",
        wvBeschlussgruende: [wvGrund1, wvGrund2, wvGrund3],
      },
    ])(
      `should_returnSortedWahlvorstandBeschlussgruende_when_givenListOfWahlvorstandBeschlussgruendeThatIs'$text'`,
      ({ wvBeschlussgruende }) => {
        const expectedResult = [wvGrund1, wvGrund2, wvGrund3];
        const arrayBeforeSort = wvBeschlussgruende.slice();

        const result =
          unitUnderTest.sortWahlvorstandBeschlussgruende(wvBeschlussgruende);

        expect(result).toStrictEqual(expectedResult);
        expect(wvBeschlussgruende).toStrictEqual(arrayBeforeSort);
      }
    );

    it("should_returnEmptyList_when_givenEmptyList", () => {
      const result = unitUnderTest.sortWahlvorstandBeschlussgruende([]);

      expect(result).toStrictEqual([]);
    });
  });

  describe("sortSystemBeschlussgruende", () => {
    const systemGrund1 = {
      ...createSystemBeschlussgrund(),
      reason: SystemBeschlussgrundReasonEnum.EinzelneStimmenUngueltig,
    };
    const systemGrund2 = {
      ...createSystemBeschlussgrund(),
      reason: SystemBeschlussgrundReasonEnum.KeineReststimmenvergabeMoeglich,
    };
    const systemGrund3 = {
      ...createSystemBeschlussgrund(),
      reason:
        SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenOderListenkreuze,
    };

    it.each([
      {
        text: "NotSorted",
        systemBeschlussgruende: [systemGrund2, systemGrund1, systemGrund3],
      },
      {
        text: "Sorted",
        systemBeschlussgruende: [systemGrund1, systemGrund2, systemGrund3],
      },
    ])(
      `should_returnSortedSystemBeschlussgruende_when_givenListOfSystemBeschlussgruendeThatIs'$text'`,
      ({ systemBeschlussgruende }) => {
        const expectedResult = [systemGrund1, systemGrund2, systemGrund3];
        const arrayBeforeSort = systemBeschlussgruende.slice();

        const result = unitUnderTest.sortSystemBeschlussgruende(
          systemBeschlussgruende
        );

        expect(result).toStrictEqual(expectedResult);
        expect(systemBeschlussgruende).toStrictEqual(arrayBeforeSort);
      }
    );

    it("should_returnEmptyList_when_givenEmptyList", () => {
      const result = unitUnderTest.sortSystemBeschlussgruende([]);

      expect(result).toStrictEqual([]);
    });
  });

  describe("getBeschlussgrundEnumValueAsString", () => {
    afterEach(() => {
      vi.clearAllMocks();
    });

    it.each([
      SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenAberImGesamtstimmenlimit,
      SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenOderListenkreuze,
      SystemBeschlussgrundReasonEnum.EinzelneStimmenUngueltig,
      SystemBeschlussgrundReasonEnum.KeineReststimmenvergabeMoeglich,
    ])("should_returnCorrespondingString%s_when_givenEnumValue%s", (input) => {
      mockDefinitions.mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText.mockReturnValue(
        "mocked Systemgrund"
      );
      expect(
        unitUnderTest.getBeschlussgrundEnumValueAsString(input)
      ).toStrictEqual("mocked Systemgrund");
      expect(
        mockDefinitions.mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText
      ).toHaveBeenCalledWith(input);
    });

    it.each([
      "1234",
      "irgendein text",
      WahlvorstandBeschlussvorschlaegeEnum.StimmzettelMitBesonderemZusatz,
      WahlvorstandBeschlussvorschlaegeEnum.BriefwahlMehrereStimmzettelInUmschlagUnterschiedlichGekennzeichnet,
      WahlvorstandBeschlussvorschlaegeEnum.BriefwahlMehrereStimmzettelInUmschlagLeerUndGekennzeichnet,
      WahlvorstandBeschlussvorschlaegeEnum.BriefwahlMehrereStimmzettelInUmschlagIdentischGekennzeichnet,
      WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleIstZweifelsfreiErkennbar,
      WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleNichtZweifelsfreiErkennbar,
      WahlvorstandBeschlussvorschlaegeEnum.NichtAmtlicherStimmzettel,
    ])("should_returnInputString_when_givenNonEnumValue", (input) => {
      expect(
        unitUnderTest.getBeschlussgrundEnumValueAsString(input)
      ).toStrictEqual(input);

      expect(
        mockDefinitions.mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText
      ).not.toHaveBeenCalled();
    });
  });

  describe("getBeschlussGruendeBasedOnGueltigkeit", () => {
    it("should_returnCommonGueltig_when_UWBAndGueltig", () => {
      userStore.user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.UWB)
        .build();

      const result = unitUnderTest.getBeschlussGruendeBasedOnGueltigkeit(true);
      const expectedResult = [
        WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleIstZweifelsfreiErkennbar,
        SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenAberImGesamtstimmenlimit,
        SystemBeschlussgrundReasonEnum.KeineReststimmenvergabeMoeglich,
        SystemBeschlussgrundReasonEnum.EinzelneStimmenUngueltig,
      ];

      expect(result).toEqual(expectedResult);
    });

    it("should_returnCommonUngueltig_when_UWBAndUngueltig", () => {
      userStore.user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.UWB)
        .build();

      const result = unitUnderTest.getBeschlussGruendeBasedOnGueltigkeit(false);
      const expectedResult = [
        WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleNichtZweifelsfreiErkennbar,
        SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenOderListenkreuze,
        WahlvorstandBeschlussvorschlaegeEnum.StimmzettelMitBesonderemZusatz,
        WahlvorstandBeschlussvorschlaegeEnum.NichtAmtlicherStimmzettel,
        SystemBeschlussgrundReasonEnum.KeineGueltigenStimmen,
      ];

      expect(result).toEqual(expectedResult);
    });

    it("should_returnCommonPlusBwbGueltig_when_BWBAndGueltig", () => {
      userStore.user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.BWB)
        .build();

      const result = unitUnderTest.getBeschlussGruendeBasedOnGueltigkeit(true);
      const expectedResult = [
        WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleIstZweifelsfreiErkennbar,
        SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenAberImGesamtstimmenlimit,
        SystemBeschlussgrundReasonEnum.KeineReststimmenvergabeMoeglich,
        SystemBeschlussgrundReasonEnum.EinzelneStimmenUngueltig,
        WahlvorstandBeschlussvorschlaegeEnum.BriefwahlMehrereStimmzettelInUmschlagIdentischGekennzeichnet,
        WahlvorstandBeschlussvorschlaegeEnum.BriefwahlMehrereStimmzettelInUmschlagLeerUndGekennzeichnet,
      ];

      expect(result).toEqual(expectedResult);
    });

    it("should_returnCommonPlusBwbUngueltig_when_BWBAndUngueltig", () => {
      userStore.user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.BWB)
        .build();

      const result = unitUnderTest.getBeschlussGruendeBasedOnGueltigkeit(false);
      const expectedResult = [
        WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleNichtZweifelsfreiErkennbar,
        SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenOderListenkreuze,
        WahlvorstandBeschlussvorschlaegeEnum.StimmzettelMitBesonderemZusatz,
        WahlvorstandBeschlussvorschlaegeEnum.NichtAmtlicherStimmzettel,
        SystemBeschlussgrundReasonEnum.KeineGueltigenStimmen,
        WahlvorstandBeschlussvorschlaegeEnum.BriefwahlMehrereStimmzettelInUmschlagUnterschiedlichGekennzeichnet,
      ];

      expect(result).toEqual(expectedResult);
    });
  });
});
