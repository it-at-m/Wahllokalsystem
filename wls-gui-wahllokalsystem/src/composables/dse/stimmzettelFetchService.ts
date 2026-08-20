import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { ComputedRef } from "vue";

import { computed, ref } from "vue";

import {
  Configuration,
  StimmzettelControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useStimmzettelMapper } from "@/composables/dse/stimmzettelMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

interface WrappedFunctionWithInProgressFlag<
  ARGUMENTS extends unknown[],
  RESULT_TYPE,
> {
  isInProgress: ComputedRef<boolean>;
  action: (...args: ARGUMENTS) => Promise<RESULT_TYPE>;
}

export function useStimmzettelFetchService() {
  const { addNotification } = useUserNotificationService();
  const { axiosConfigWrapper, getNullOn204OrElseResponseData } =
    useCommonApiUtils();
  const { toModel, toDTO } = useStimmzettelMapper();

  const ergebnismeldungConfiguration = new Configuration({
    basePath: ERGEBNISMELDUNG_SERVICE_API_URL,
  });

  const stimmzettelControllerApi = new StimmzettelControllerApi(
    ergebnismeldungConfiguration
  );

  const latestStimmzettelState = ref<Stimmzettel[] | undefined>(undefined);
  const lastLoadedAnzahlStimmzettel = ref<number | undefined>(undefined);

  const loadStimmzettelWithInProgress =
    _enrichWithInProgressFlag(_loadStimmzettel);
  const loadAnzahlStimmzettelWithInProgress = _enrichWithInProgressFlag(
    _loadAnzahlStimmzettel
  );
  const saveStimmzettelWithInProgress =
    _enrichWithInProgressFlag(_saveStimmzettel);

  async function _loadStimmzettel(
    wahlID: string,
    wahlbezirkID: string,
    teamID: string,
    sendNotification = true
  ): Promise<void> {
    try {
      const response = await stimmzettelControllerApi.getStimmzettel(
        wahlID,
        wahlbezirkID,
        teamID
      );
      const responseData = getNullOn204OrElseResponseData(response);
      latestStimmzettelState.value =
        responseData?.map((stimmzettelDTO) => toModel(stimmzettelDTO)) ?? [];
    } catch (error) {
      if (sendNotification) {
        addNotification(
          "Abrufen der Stimmzettel ist fehlgeschlagen",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw error;
    }
  }

  async function _saveStimmzettel(
    wahlID: string,
    wahlbezirkID: string,
    teamID: string,
    stimmzettelList: Stimmzettel[],
    sendNotification = true
  ) {
    try {
      const dtosToSend = stimmzettelList.map((stimmzettel) =>
        toDTO(stimmzettel)
      );
      await stimmzettelControllerApi.postStimmzettel(
        wahlID,
        wahlbezirkID,
        teamID,
        dtosToSend
      );
      latestStimmzettelState.value = stimmzettelList;
      if (sendNotification) {
        addNotification(
          "Speichern der Stimmzettel erfolgreich",
          UserNotificationCategoryEnum.SUCCESS
        );
      }
    } catch (error) {
      if (sendNotification) {
        addNotification(
          "Speichern der Stimmzettel ist fehlgeschlagen",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw error;
    }
  }

  async function _loadAnzahlStimmzettel(
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = true
  ): Promise<void> {
    try {
      const result = await stimmzettelControllerApi.getAnzahlStimmzettel(
        wahlID,
        wahlbezirkID,
        axiosConfigWrapper().requestAsOnlineOnly()
      );
      lastLoadedAnzahlStimmzettel.value = result.data;
    } catch (error) {
      if (sendNotification) {
        addNotification(
          "Abrufen der Anzahl der Stimmzettel ist fehlgeschlagen",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw error;
    }
  }

  function _enrichWithInProgressFlag<ARGUMENTS extends unknown[], RESULT_TYPE>(
    asyncFunctionToWrap: (...args: ARGUMENTS) => Promise<RESULT_TYPE>
  ): WrappedFunctionWithInProgressFlag<ARGUMENTS, RESULT_TYPE> {
    const isInProgress = ref(false);

    async function wrapped(...args: ARGUMENTS): Promise<RESULT_TYPE> {
      isInProgress.value = true;
      try {
        return (await asyncFunctionToWrap(...args)) as RESULT_TYPE;
      } finally {
        isInProgress.value = false;
      }
    }

    return {
      isInProgress: computed(() => isInProgress.value),
      action: wrapped,
    };
  }

  return {
    isLoadingAnzahlStimmzettel:
      loadAnzahlStimmzettelWithInProgress.isInProgress,
    isLoadingStimmzettel: loadStimmzettelWithInProgress.isInProgress,
    isSavingStimmzettel: saveStimmzettelWithInProgress.isInProgress,

    lastLoadedAnzahlStimmzettel,
    latestStimmzettelState,

    loadAnzahlStimmzettel: loadAnzahlStimmzettelWithInProgress.action,
    loadStimmzettel: loadStimmzettelWithInProgress.action,
    saveStimmzettel: saveStimmzettelWithInProgress.action,
  };
}
