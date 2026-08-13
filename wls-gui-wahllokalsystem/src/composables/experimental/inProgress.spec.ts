import { describe, it } from "vitest";

import { useWithInProgress } from "@/composables/experimental/inProgress.ts";

describe("test", () => {
  it("case", async () => {
    const functionToWrap = async () => {
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve(Math.random());
        }, 4000);
      });
    };

    const functionToWrap2 = (a1: number, a2: number, a3: number) =>
      Promise.resolve(a1 + a2 + a3);

    const wrapped = useWithInProgress().createWrappedFunction(functionToWrap2);
    const { isInProgress, action } = wrapped;

    console.log(wrapped);

    console.log(isInProgress.value);
    const promise = action(1, 2, 3);
    console.log(isInProgress.value);
    const result = await promise;
    console.log(isInProgress.value);
    console.log(result);
  });
});
