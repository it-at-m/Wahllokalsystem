import { beforeEach, describe, expect, it } from "vitest";

import useRules from "@/composables/common/rules.ts";

describe("rules.ts", () => {
  describe("useRules", () => {
    let requiredTextRuleBuilder: (
      requiredText: string
    ) => (value: unknown) => boolean;

    const requiredText = "text";

    beforeEach(() => {
      requiredTextRuleBuilder = useRules().requiredText;
    });

    describe("requiredText", () => {
      it("should_returnFalse_whenValueIsUndefinedButRequiredTextIsNot", () => {
        const rule = requiredTextRuleBuilder(requiredText);

        expect(rule(undefined)).toStrictEqual(false);
      });

      it("should_returnFalse_whenValueIsNullButRequiredTextIsNot", () => {
        const rule = requiredTextRuleBuilder(requiredText);

        expect(rule(null)).toStrictEqual(false);
      });

      it("should_returnFalse_whenValueIsEmptyStringButRequiredTextIsNot", () => {
        const rule = requiredTextRuleBuilder(requiredText);

        expect(rule("")).toStrictEqual(false);
      });

      it("should_returnFalse_whenValueIsNotEqualRequiredText", () => {
        const rule = requiredTextRuleBuilder(requiredText);

        expect(rule(requiredText + "c")).toStrictEqual(false);
      });

      it("should_returnTrue_whenValueIsEqualRequiredText", () => {
        const rule = requiredTextRuleBuilder(requiredText);

        expect(rule(requiredText)).toStrictEqual(true);
      });
    });
  });
});
