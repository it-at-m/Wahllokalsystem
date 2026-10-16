import { describe, expect, it } from "vitest";
import { reactive, toRaw } from "vue";

import { useCloneTools } from "@/composables/common/cloneTools.ts";

describe("cloneTools.ts", () => {
  const unitUnderTest = useCloneTools();

  describe("cloneStructured", () => {
    it("should_returnIndependentDeepClone_when_sourceIsPlainObject", () => {
      const source = {
        nestedObject: { value: "original" },
        values: [1, 2],
      };

      const result = unitUnderTest.cloneStructured(source);

      expect(result).toStrictEqual(source);
      expect(result).not.toBe(source);
      expect(result.nestedObject).not.toBe(source.nestedObject);
      expect(result.values).not.toBe(source.values);

      result.nestedObject.value = "changed";
      result.values.push(3);

      expect(source).toStrictEqual({
        nestedObject: { value: "original" },
        values: [1, 2],
      });
    });

    it("should_returnIndependentDeepClone_when_sourceIsReactiveProxy", () => {
      const source = reactive({
        nestedObject: { value: "original" },
        values: [1, 2],
      });

      const result = unitUnderTest.cloneStructured(source);

      expect(result).toStrictEqual(toRaw(source));
      expect(result).not.toBe(toRaw(source));
      expect(result.nestedObject).not.toBe(source.nestedObject);
      expect(result.values).not.toBe(source.values);

      result.nestedObject.value = "changed";
      result.values.push(3);

      expect(toRaw(source)).toStrictEqual({
        nestedObject: { value: "original" },
        values: [1, 2],
      });
    });
  });
});
