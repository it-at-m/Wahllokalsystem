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
const KONFIG_KEY_CHECK_WAHLSCHLUSS = "MELDUNGSZEIT_WAHL_SCHLIESSEN";
const KONFIG_KEY_DELAY_BEFORE_INAKTIV = "WLK_TIME_OUT";

const DEFAULT_FRUEHESTE_EROEFFNUNGSZEIT_UW = "08:00:00";
const DEFAULT_FRUEHESTE_EROEFFNUNGSZEIT_BW = "15:00:00";
const DEFAULT_SPAETESTE_EROEFFNUNGSZEIT_UW = "17:59:00";
const DEFAULT_SPAETESTE_EROEFFNUNGSZEIT_BW = "17:59:00";
const DEFAULT_FRUEHESTE_SCHLIESSUNGSZEIT_UW = "18:00:00";
const DEFAULT_FRUEHESTE_SCHLIESSUNGSZEIT_BW = "18:00:00";
const DEFAULT_DELAY_BEFORE_INAKTIV_LOGOUT_IN_MILLISECONDS = 7200000;

export const useInfomanagementStore = defineStore(storeID, () => {
  const { currentUserWahltag } = storeToRefs(useUserStore());
  const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());

  const konfigurationsparameter = ref<Konfigurationsparameter[] | null>(null);

  const dateTimeToCheckAnwesenheit = computed(() =>
    _getDateTimeToCheck(KONFIG_KEY_CHECK_ANWESENHEIT)
  );

  const dateTimeToCheckWahlschluss = computed(() =>
    _getDateTimeToCheck(KONFIG_KEY_CHECK_WAHLSCHLUSS)
  );

  const fruehesteEroeffnungsuhrzeit = computed(() => {
    switch (currentUserWahlbezirksArt.value) {
      case WahlbezirksArtEnum.UWB:
        return _fruehesteEroeffnungsuhrzeitUWB.value;
      case WahlbezirksArtEnum.BWB:
        return _fruehesteEroeffnungsuhrzeitBWB.value;
      default:
        return "";
    }
  });

  const spaetesteEroeffnungsuhrzeit = computed(() => {
    switch (currentUserWahlbezirksArt.value) {
      case WahlbezirksArtEnum.UWB:
        return _spaetesteErfoeffnungsuhrzeitUWB.value;
      case WahlbezirksArtEnum.BWB:
        return _spaetesteErfoeffnungsuhrzeitBWB.value;
      default:
        return "";
    }
  });

  const fruehesteSchliessungsuhrzeit = computed(() => {
    switch (currentUserWahlbezirksArt.value) {
      case WahlbezirksArtEnum.UWB:
        return _fruehesteSchliessungsuhrzeitUWB.value;
      case WahlbezirksArtEnum.BWB:
        return _fruehesteSchliessungsuhrzeitBWB.value;
      default:
        return "";
    }
  });

  const delayBeforeInactiveLogoutInMilliseconds = computed(() => {
    const configValueForInactiveDelay = _getKonfigParamValue(
      KONFIG_KEY_DELAY_BEFORE_INAKTIV
    );
    if (!configValueForInactiveDelay) {
      return DEFAULT_DELAY_BEFORE_INAKTIV_LOGOUT_IN_MILLISECONDS;
    }

    const configValueParsedAsInteger = Number.parseInt(
      configValueForInactiveDelay
    );
    return Number.isInteger(configValueParsedAsInteger) &&
      configValueParsedAsInteger > 0
      ? configValueParsedAsInteger
      : DEFAULT_DELAY_BEFORE_INAKTIV_LOGOUT_IN_MILLISECONDS;
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

  const _fruehesteEroeffnungsuhrzeitUWB = computed(() => {
    return _getKonfigParamValueOrDefault(
      "FRUEHESTE_EROEFFNUNGSZEIT_UW",
      DEFAULT_FRUEHESTE_EROEFFNUNGSZEIT_UW
    );
  });

  const _spaetesteErfoeffnungsuhrzeitUWB = computed(() => {
    return _getKonfigParamValueOrDefault(
      "SPAETESTE_EROEFFNUNGSZEIT_UW",
      DEFAULT_SPAETESTE_EROEFFNUNGSZEIT_UW
    );
  });

  const _fruehesteSchliessungsuhrzeitUWB = computed(() => {
    return _getKonfigParamValueOrDefault(
      "FRUEHESTE_SCHLIESSUNGSZEIT_UW",
      DEFAULT_FRUEHESTE_SCHLIESSUNGSZEIT_UW
    );
  });

  const _fruehesteEroeffnungsuhrzeitBWB = computed(() => {
    return _getKonfigParamValueOrDefault(
      "FRUEHESTE_EROEFFNUNGSZEIT_BW",
      DEFAULT_FRUEHESTE_EROEFFNUNGSZEIT_BW
    );
  });

  const _spaetesteErfoeffnungsuhrzeitBWB = computed(() => {
    return _getKonfigParamValueOrDefault(
      "SPAETESTE_EROEFFNUNGSZEIT_BW",
      DEFAULT_SPAETESTE_EROEFFNUNGSZEIT_BW
    );
  });

  const _fruehesteSchliessungsuhrzeitBWB = computed(() => {
    return _getKonfigParamValueOrDefault(
      "FRUEHESTE_SCHLIESSUNGSZEIT_BW",
      DEFAULT_FRUEHESTE_SCHLIESSUNGSZEIT_BW
    );
  });

  function _getKonfigParamValueOrDefault(
    schluessel: string,
    defaultValue: string
  ) {
    return _getKonfigParamValue(schluessel) || defaultValue;
  }

  function _getKonfigParamValue(schluessel: string) {
    const param = konfigurationsparameter.value?.find(
      (param) => param.schluessel === schluessel
    );
    return param?.wert;
  }

  function _getDateTimeToCheck(configKey: string) {
    const timeToCheck = konfigurationsparameter.value?.find(
      (parameter) => parameter.schluessel === configKey
    )?.wert;
    if (timeToCheck && currentUserWahltag.value) {
      const dateToCheck = new Date(
        `${currentUserWahltag.value}T${timeToCheck}`
      );
      return isValidDate(dateToCheck) ? dateToCheck : undefined;
    } else {
      return undefined;
    }
  }

  return {
    konfigurationsparameter,
    delayBeforeInactiveLogoutInMilliseconds,
    dateTimeToCheckAnwesenheit,
    dateTimeToCheckWahlschluss,
    initKonfigurationsparameter,
    /** FRUEHESTE_EROEFFNUNGSZEIT bezeichnet den frühesten Wert, zu dem die Wahlhandlung eröffnet werden kann. */
    fruehesteEroeffnungsuhrzeit,
    /** SPAETESTE_EROEFFNUNGSZEIT bezeichnet den spätesten Wert, zu dem die Wahlhandlung eröffnet werden kann,
     * ohne dass die verspätete Eröffnung als Ereignis dokumentiert werden muss. */
    spaetesteEroeffnungsuhrzeit,
    /** FRUEHESTE_SCHLIESSUNGSZEIT bezeichnet den spätesten Wert, zu dem die Wahlhandlung eröffnet werden kann
     * und den frühesten Wert, zu dem die Wahlhandlung geschlossen werden kann. */
    fruehesteSchliessungsuhrzeit,
  };
});

registerStoreHMR(useInfomanagementStore);
