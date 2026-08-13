import { ref } from "vue";

export function useDoSth() {
  async function doSth(argument1: number): Promise<number> {
    return await Promise.resolve(argument1 * 2);
  }

  return {
    doSth,
  };
}

export function useWithProgress() {
  const { doSth } = useDoSth();

  const isInProgress = ref(false);

  async function doWithIsInProgress(argument1: number): Promise<number> {
    isInProgress.value = true;
    try {
      return await doSth(argument1);
    } catch {
      throw new Error("DoSth failed");
    } finally {
      isInProgress.value = false;
    }
  }

  return {
    isInProgress,
    doWithIsInProgress,
  };
}
