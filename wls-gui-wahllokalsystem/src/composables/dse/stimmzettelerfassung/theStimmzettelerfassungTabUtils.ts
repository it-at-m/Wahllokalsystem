import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Ref } from "vue";

import { computed, onActivated, readonly, ref } from "vue";

import { useStimmzettelTools } from "@/composables/dse/stimmzettelerfassung/stimmzettelTools.ts";
import { useStimmzettelDataService } from "@/composables/experimental/stimzettelDataService.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { isSamePersistedStimmzettel } = useStimmzettelTools();

export function useTheStimmzettelerfassungTabUtils(
  wahlID: string,
  wahlbezirkID: string,
  teamID: string
) {
  const { getStimmzettelOfTeam, setStimmzettel } = useStimmzettelDataService(
    wahlID,
    wahlbezirkID
  );
  const { addNotification } = useUserNotificationService();

  const isStimmzettelLoading = ref(false);
  const savedStimmzettel: Ref<PersistedStimmzettel[]> = ref([]);

  const hasStimmzettel = computed(() => savedStimmzettel.value.length > 0);

  onActivated(async () => {
    await _loadStimmzettel();
  });

  async function saveOrUpdateStimmzettel(
    stimmzettelToSave: PersistedStimmzettel
  ) {
    try {
      const newStimmzettelCollectionToSave = [...savedStimmzettel.value];
      const stimmzettelExistsIndex = newStimmzettelCollectionToSave.findIndex(
        (savedStimmzettel) =>
          isSamePersistedStimmzettel(savedStimmzettel, stimmzettelToSave)
      );

      if (stimmzettelExistsIndex !== -1) {
        newStimmzettelCollectionToSave[stimmzettelExistsIndex] =
          stimmzettelToSave;
      } else {
        newStimmzettelCollectionToSave.push(stimmzettelToSave);
      }

      await setStimmzettel(stimmzettelToSave);
      addNotification(
        "Speichern des Stimmzettels erfolgreich",
        UserNotificationCategoryEnum.SUCCESS
      );
      savedStimmzettel.value = newStimmzettelCollectionToSave;
    } catch {
      addNotification(
        "Fehler beim Speichern des Stimmzettels",
        UserNotificationCategoryEnum.ERROR
      );
    }
  }

  async function _loadStimmzettel() {
    isStimmzettelLoading.value = true;
    try {
      savedStimmzettel.value = await getStimmzettelOfTeam(teamID);
    } finally {
      isStimmzettelLoading.value = false;
    }
  }

  return {
    isStimmzettelLoading: readonly(isStimmzettelLoading),
    savedStimmzettel: computed(() => savedStimmzettel.value),
    hasStimmzettel,
    saveOrUpdateStimmzettel,
  };
}
