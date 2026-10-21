import { isProxy, toRaw } from "vue";

export function useCloneTools() {
  function cloneStructured<T>(source: T): T {
    if (isProxy(source)) {
      return structuredClone(toRaw(source));
    } else {
      return structuredClone(source);
    }
  }

  return {
    cloneStructured,
  };
}
