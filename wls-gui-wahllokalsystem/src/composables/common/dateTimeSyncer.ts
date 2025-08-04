import type { ComputedRef } from "vue";

import { computed, ref, watch } from "vue";

export function useDateTimeSyncer(initState: ComputedRef<Date | undefined>) {
  const dateOnly = ref<Date | undefined>(initState.value);
  const timeOnly = ref<Date | undefined>(initState.value);

  watch(initState, (value) => {
    if (value?.getTime() !== dateAndTimeCombined.value?.getTime()) {
      _syncChronoComponents(value);
    }
  });

  const dateAndTimeCombined = computed(() => {
    if (dateOnly.value && timeOnly.value) {
      const combinedDate = new Date();
      combinedDate.setFullYear(
        dateOnly.value.getFullYear(),
        dateOnly.value.getMonth(),
        dateOnly.value.getDate()
      );
      combinedDate.setHours(
        timeOnly.value.getHours(),
        timeOnly.value.getMinutes(),
        timeOnly.value.getSeconds(),
        timeOnly.value.getMilliseconds()
      );
      return combinedDate;
    } else {
      return null;
    }
  });

  function _syncChronoComponents(date: Date | undefined) {
    dateOnly.value = date;
    timeOnly.value = date;
  }

  return {
    dateOnly,
    timeOnly,
    dateAndTimeCombined,
  };
}
