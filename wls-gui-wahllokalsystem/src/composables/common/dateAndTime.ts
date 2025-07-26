import { computed, ref } from "vue";

export function useDateAndTime(date: Date | undefined) {
  const dateComponent = ref<Date | undefined>(date);
  const timeComponent = ref<Date | undefined>(date);

  const dateAndTimeCombined = computed(() => {
    if (dateComponent.value && timeComponent.value) {
      const combinedDate = new Date();
      combinedDate.setFullYear(
        dateComponent.value.getFullYear(),
        dateComponent.value.getMonth(),
        dateComponent.value.getDate()
      );
      combinedDate.setHours(
        timeComponent.value.getHours(),
        timeComponent.value.getMinutes(),
        timeComponent.value.getSeconds(),
        timeComponent.value.getMilliseconds()
      );
      return combinedDate;
    } else {
      return null;
    }
  });

  return {
    dateComponent,
    timeComponent,
    dateAndTimeCombined,
  };
}
