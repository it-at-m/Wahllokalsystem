import type { Ref } from "vue";

import { ref } from "vue";

interface WrappedFunctionWithInProgressFlag<
  ARGUMENTS extends unknown[],
  RESULT_TYPE,
> {
  isInProgress: Ref<boolean>;
  action: (...args: ARGUMENTS) => Promise<RESULT_TYPE>;
}

export function useWithInProgress() {
  function createWrappedFunction<ARGUMENTS extends unknown[], RESULT_TYPE>(
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
      isInProgress,
      action: wrapped,
    };
  }

  return {
    createWrappedFunction,
  };
}
