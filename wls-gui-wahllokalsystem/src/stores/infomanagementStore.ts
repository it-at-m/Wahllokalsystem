import type { Konfigurationsparameter } from "@/types/infomanagement/Konfigurationsparameter.ts";

import { defineStore, storeToRefs } from "pinia";
import { computed, ref } from "vue";

import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";
import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useKonfigurationsparameterService } from "@/composables/infomanagement/konfigurationsparameterService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export const storeID = "infomanagement";
const { getKonfigurationsparameter } = useKonfigurationsparameterService();
const { registerStoreHMR } = useHmrUpdate();
const { isValidDate } = useDateTimeUtils();

const KONFIG_KEY_CHECK_ANWESENHEIT = "MELDUNGSZEIT_ANWESENHEIT_CHECK";

const DEFAULT_TIME = "00:00:00";

export const useInfomanagementStore = defineStore(storeID, () => {
  const { currentUserWahltag } = storeToRefs(useUserStore());
  const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());

  const konfigurationsparameter = ref<Konfigurationsparameter[] | null>(null);

  const dateTimeToCheckAnwesenheit = computed(() => {
    const timeToCheckAnwesenheit = konfigurationsparameter.value?.find(
      (parameter) => parameter.schluessel === KONFIG_KEY_CHECK_ANWESENHEIT
    )?.wert;

    if (timeToCheckAnwesenheit && currentUserWahltag.value) {
      const dateToCheckAnwesenheit = new Date(
        `${currentUserWahltag.value}T${timeToCheckAnwesenheit}`
      );

      return isValidDate(dateToCheckAnwesenheit)
        ? dateToCheckAnwesenheit
        : undefined;
    } else {
      return undefined;
    }
  });

  const fruehesteEroeffnungsuhrzeit = computed(() => {
    switch (currentUserWahlbezirksArt.value) {
      case WahlbezirksArtEnum.UWB:
        return _fruehesteEroeffnungsuhrzeitUWB.value;
      case WahlbezirksArtEnum.BWB:
        return _fruehesteEroeffnungsuhrzeitBWB.value;
      default:
        return DEFAULT_TIME;
    }
  });

  const fruehesteSchliessungsuhrzeit = computed(() => {
    switch (currentUserWahlbezirksArt.value) {
      case WahlbezirksArtEnum.UWB:
        return _fruehesteSchliessungsuhrzeitUWB.value;
      case WahlbezirksArtEnum.BWB:
        return _fruehesteSchliessungsuhrzeitBWB.value;
      default:
        return DEFAULT_TIME;
    }
  });

  async function initKonfigurationsparameter(sendNotification = true) {
    try {
      konfigurationsparameter.value =
        await getKonfigurationsparameter(sendNotification);
    } catch (error) {
      konfigurationsparameter.value = null;
      throw error;
    }
  }

  /** FRUEHESTE_EROEFFNUNGSZEIT bezeichnet den frühesten Wert, zu dem die Wahlhandlung eröffnet werden kann. */
  const _fruehesteEroeffnungsuhrzeitUWB = computed(() => {
    return _getKonfigParamValueOrDefault(
      "FRUEHESTE_EROEFFNUNGSZEIT_UW",
      DEFAULT_TIME
    );
  });

  /** FRUEHESTE_SCHLIESSUNGSZEIT bezeichnet den spätesten Wert, zu dem die Wahlhandlung eröffnet werden kann. */
  const _fruehesteSchliessungsuhrzeitUWB = computed(() => {
    return _getKonfigParamValueOrDefault(
      "FRUEHESTE_SCHLIESSUNGSZEIT_UW",
      DEFAULT_TIME
    );
  });

  /** FRUEHESTE_EROEFFNUNGSZEIT bezeichnet den frühesten Wert, zu dem die Wahlhandlung eröffnet werden kann. */
  const _fruehesteEroeffnungsuhrzeitBWB = computed(() => {
    return _getKonfigParamValueOrDefault(
      "FRUEHESTE_EROEFFNUNGSZEIT_BW",
      DEFAULT_TIME
    );
  });

  /** FRUEHESTE_SCHLIESSUNGSZEIT bezeichnet den spätesten Wert, zu dem die Wahlhandlung eröffnet werden kann. */
  const _fruehesteSchliessungsuhrzeitBWB = computed(() => {
    return _getKonfigParamValueOrDefault(
      "FRUEHESTE_SCHLIESSUNGSZEIT_BW",
      DEFAULT_TIME
    );
  });

  function _getKonfigParamValueOrDefault(
    schluessel: string,
    defaultValue: string
  ) {
    const param = konfigurationsparameter.value?.find(
      (param) => param.schluessel === schluessel
    );
    return param?.wert || defaultValue;
  }

  return {
    konfigurationsparameter,
    dateTimeToCheckAnwesenheit,
    initKonfigurationsparameter,
    fruehesteEroeffnungsuhrzeit,
    fruehesteSchliessungsuhrzeit,
  };
});

registerStoreHMR(useInfomanagementStore);
