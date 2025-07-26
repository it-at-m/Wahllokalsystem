import type { Ref } from "vue";

import { computed, ref, watch } from "vue";

export function useDateAndTime(initState: Ref<Date | undefined>) {
  const dateOnly = ref<Date | undefined>(initState.value);
  const timeOnly = ref<Date | undefined>(initState.value);

  watch(initState, (value, oldValue) => {
    console.debug(
      `useDateAndTime - watch of date - value: ${value}, oldValue: ${oldValue}, dateAndTimeCombined: ${dateAndTimeCombined.value}`
    );
    if (value?.getTime() !== dateAndTimeCombined.value?.getTime()) {
      syncChronoComponents(value);
    }
  });

  function syncChronoComponents(date: Date | undefined) {
    dateOnly.value = date;
    timeOnly.value = date;
  }

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
      console.debug(
        `dateAndTimeCombined - result: ${JSON.stringify(combinedDate)}`
      );
      return combinedDate;
    } else {
      console.debug(`dateAndTimeCombined - result: null`);
      return null;
    }
  });

  return {
    dateOnly,
    timeOnly,
    dateAndTimeCombined,
    syncChronoComponents,
  };
}
