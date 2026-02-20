import { beforeEach, describe, expect, it } from "vitest";

import { useRules } from "@/composables/common/rules.ts";

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
      it.each([
        { descriptionValueIs: "valueIisUndefined", value: undefined },
        { descriptionValueIs: "valueIsNull", value: null },
        { descriptionValueIs: "valueIsEmptyString", value: "" },
        {
          descriptionValueIs: "valueDoesNotEqualRequired",
          value: requiredText + requiredText,
        },
      ])(
        "should_returnFalse_when_'$descriptionValueIs'ButRequiredTextIsNot",
        ({ value }) => {
          const rule = requiredTextRuleBuilder(requiredText);

          expect(rule(value)).toStrictEqual(false);
        }
      );

      it("should_returnTrue_whenValueIsEqualRequiredText", () => {
        const rule = requiredTextRuleBuilder(requiredText);

        expect(rule(requiredText)).toStrictEqual(true);
      });
    });
  });
});
