import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import { storeToRefs } from "pinia";
import { ref } from "vue";

import { useBeschlussgrundOptionTools } from "@/composables/dse/beschlussfassung/beschlussgrundOptionTools.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";
import { WahlvorstandBeschlussvorschlaegeEnum } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussvorschlaegeEnum.ts";

const {
  mapGruendeToBeschlussgrundOptions,
  setSystemBeschlussgruendeTrueWhenFoundInStimmzettel,
  setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel,
  setBeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList,
} = useBeschlussgrundOptionTools();

export function useTheBeschlussFassenTabUtils() {
  const { isBWB } = storeToRefs(useUserStore());

  const beschlussGruende = {
    common: {
      gueltig: [
        WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleIstZweifelsfreiErkennbar,
        SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenAberImGesamtstimmenlimit,
        SystemBeschlussgrundReasonEnum.KeineReststimmenvergabeMoeglich,
        SystemBeschlussgrundReasonEnum.EinzelneStimmenUngueltig,
      ],
      ungueltig: [
        WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleNichtZweifelsfreiErkennbar,
        SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenOderListenkreuze,
        WahlvorstandBeschlussvorschlaegeEnum.StimmzettelMitBesonderemZusatz,
        WahlvorstandBeschlussvorschlaegeEnum.NichtAmtlicherStimmzettel,
      ],
    },
    bwb: {
      gueltig: [
        WahlvorstandBeschlussvorschlaegeEnum.BriefwahlMehrereStimmzettelInUmschlagIdentischGekennzeichnet,
        WahlvorstandBeschlussvorschlaegeEnum.BriefwahlMehrereStimmzettelInUmschlagLeerUndGekennzeichnet,
      ],
      ungueltig: [
        WahlvorstandBeschlussvorschlaegeEnum.BriefwahlMehrereStimmzettelInUmschlagUnterschiedlichGekennzeichnet,
      ],
    },
  };

  function createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit(
    isStimmzettelGueltig: boolean | null,
    stimmzettel: PersistedStimmzettel | undefined
  ) {
    if (isStimmzettelGueltig === null)
      return { andererGrund: "", beschlussgruende: [] };

    const gruendeList =
      _getBeschlussGruendeBasedOnGueltigkeit(isStimmzettelGueltig);
    const beschlussgrundOptions = ref(
      mapGruendeToBeschlussgrundOptions(gruendeList)
    );

    setSystemBeschlussgruendeTrueWhenFoundInStimmzettel(
      stimmzettel?.systemBeschlussvorschlag ?? [],
      beschlussgrundOptions.value
    );
    setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel(
      stimmzettel?.wahlvorstandBeschlussvorschlag ?? [],
      beschlussgrundOptions.value
    );

    const andererGrund =
      setBeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList(
        stimmzettel?.wahlvorstandBeschlussvorschlag ?? [],
        stimmzettel?.systemBeschlussvorschlag ?? [],
        beschlussgrundOptions.value
      );
    const beschlussgruende = beschlussgrundOptions.value;

    return {
      andererGrund,
      beschlussgruende,
    };
  }

  function isStimmzettelGueltigBasedOnVormerkungsgruenden(
    stimmzettel: PersistedStimmzettel
  ) {
    const ungueltigOptions =
      createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit(
        false,
        undefined
      ).beschlussgruende;
    const ungueltigSet = new Set(ungueltigOptions.map((o) => o.grund));

    const hasUngueltigerSystemGrund = (
      stimmzettel.systemBeschlussvorschlag ?? []
    ).some((beschlussvorschlag) => {
      return ungueltigSet.has(beschlussvorschlag.reason);
    });

    const hasUngueltigerWahlvorstandGrund = (
      stimmzettel.wahlvorstandBeschlussvorschlag ?? []
    ).some((beschlussvorschlag) => {
      return ungueltigSet.has(beschlussvorschlag.text);
    });

    return !(hasUngueltigerSystemGrund || hasUngueltigerWahlvorstandGrund);
  }

  function _getBeschlussGruendeBasedOnGueltigkeit(isGueltig: boolean) {
    return isGueltig
      ? isBWB.value
        ? [...beschlussGruende.common.gueltig, ...beschlussGruende.bwb.gueltig]
        : beschlussGruende.common.gueltig
      : isBWB.value
        ? [
            ...beschlussGruende.common.ungueltig,
            ...beschlussGruende.bwb.ungueltig,
          ]
        : beschlussGruende.common.ungueltig;
  }

  return {
    createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit,
    isStimmzettelGueltigBasedOnVormerkungsgruenden,
  };
}
