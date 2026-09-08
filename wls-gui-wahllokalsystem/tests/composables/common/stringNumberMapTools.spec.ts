import { beforeEach, describe, expect, it } from "vitest";

import { useStringNumberMapTools } from "@/composables/common/stringNumberMapTools.ts";

describe("stringNumberMapTools", () => {
  let container: Map<string, number>;
  let unitUnderTest: ReturnType<typeof useStringNumberMapTools>;

  beforeEach(() => {
    container = new Map<string, number>();
    unitUnderTest = useStringNumberMapTools(container);
  });

  describe("add", () => {
    it("should_setValue_when_keyDoesNotExist", () => {
      unitUnderTest.add("key", 5);

      expect(container.get("key")).toStrictEqual(5);
    });

    it("should_addValue_when_keyAlreadyExists", () => {
      container.set("key", 5);

      unitUnderTest.add("key", 3);

      expect(container.get("key")).toStrictEqual(8);
    });

    it("should_addNegativeValue_when_keyAlreadyExists", () => {
      container.set("key", 5);

      unitUnderTest.add("key", -3);

      expect(container.get("key")).toStrictEqual(2);
    });
  });

  describe("getOrDefault", () => {
    it("should_returnValue_when_keyExists", () => {
      container.set("key", 0);

      expect(unitUnderTest.getOrDefault("key")).toStrictEqual(0);
    });

    it("should_returnZero_when_keyDoesNotExistAndNoDefaultValueIsGiven", () => {
      expect(unitUnderTest.getOrDefault("key")).toStrictEqual(0);
    });

    it("should_returnDefaultValue_when_keyDoesNotExist", () => {
      expect(unitUnderTest.getOrDefault("key", 5)).toStrictEqual(5);
    });
  });

  describe("sum", () => {
    it("should_returnZero_when_containerIsEmpty", () => {
      expect(unitUnderTest.sum()).toStrictEqual(0);
    });

    it("should_returnSum_when_containerContainsMultipleValues", () => {
      container.set("first", 4);
      container.set("second", 7);
      container.set("third", 2);

      expect(unitUnderTest.sum()).toStrictEqual(13);
    });

    it("should_includeNegativeAndZeroValues_when_containerContainsThem", () => {
      container.set("first", 4);
      container.set("second", -7);
      container.set("third", 0);

      expect(unitUnderTest.sum()).toStrictEqual(-3);
    });
  });
});
