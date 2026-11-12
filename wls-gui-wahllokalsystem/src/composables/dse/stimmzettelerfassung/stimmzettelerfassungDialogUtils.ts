import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { ComputedRef } from "vue";

import { computed } from "vue";

import { useStimmzettelManager } from "@/composables/dse/stimmzettelerfassung/stimmzettelManager.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

export function useStimmzettelerfassungDialogUtils(
  stimmzettelkennung: ComputedRef<number>,
  wahlvorschlaege: Wahlvorschlag[],
  wahlID: string
) {
  const stimmzettelManager = useStimmzettelManager(
    stimmzettelkennung,
    wahlvorschlaege,
    wahlID
  );

  const stimmzettelGueltigkeit = computed(
    () =>
      stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmzettel.value
        .gueltigkeit
  );

  const isSaveDisabled = computed(() => {
    if (
      stimmzettelManager.stimmzettelBeforeEdit.value !== null &&
      stimmzettelManager.bearbeitenDialogStimmzettelUtils.hasAnyValuesSet
        .value &&
      !stimmzettelManager.hasStimmzettelBeenEdited.value
    )
      return true;

    return (
      !stimmzettelManager.bearbeitenDialogStimmzettelUtils.hasAnyValuesSet
        .value &&
      stimmzettelGueltigkeit.value !== StimmzettelGueltigkeitEnum.Leer &&
      stimmzettelGueltigkeit.value !==
        StimmzettelGueltigkeitEnum.BwbPseudoStimmzettelLeererUmschlag &&
      stimmzettelGueltigkeit.value !==
        StimmzettelGueltigkeitEnum.BeschlussAusstehend &&
      !!stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmzettel.value
    );
  });

  const isResetDisabled = computed(() => {
    if (stimmzettelManager.stimmzettelBeforeEdit.value !== null) {
      return !stimmzettelManager.hasStimmzettelBeenEdited.value;
    }
    return !stimmzettelManager.bearbeitenDialogStimmzettelUtils.hasAnyValuesSet
      .value;
  });

  const isCancelButtonDisabled = computed(
    () => stimmzettelManager.hasStimmzettelBeenEdited.value
  );

  function onResetClicked() {
    if (
      stimmzettelManager.hasStimmzettelBeenEdited.value &&
      stimmzettelManager.stimmzettelBeforeEdit.value !== null
    ) {
      stimmzettelManager.bearbeitenDialogStimmzettelUtils.resetStimmzettelAndHistory(
        stimmzettelManager.stimmzettelBeforeEdit.value
      );
    } else {
      stimmzettelManager.bearbeitenDialogStimmzettelUtils.resetStimmzettelAndHistory();
    }
  }

  return {
    stimmzettelGueltigkeit,
    isSaveDisabled,
    isResetDisabled,
    isCancelButtonDisabled,
    stimmzettelManager,
    onResetClicked,
  };
}
