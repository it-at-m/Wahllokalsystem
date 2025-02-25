import { onMounted, onUnmounted, ref } from "vue";

export function useInterval(callback: () => void, delay: number) {
  const intervalId = ref<null | number>(null);

  const start = () => {
    if (intervalId.value === null) {
      intervalId.value = window.setInterval(callback, delay);
    }
  };

  const stop = () => {
    if (intervalId.value !== null) {
      clearInterval(intervalId.value);
      intervalId.value = null;
    }
  };

  onMounted(() => {
    start();
  });

  onUnmounted(() => {
    stop();
  });

  return { start, stop };
}
