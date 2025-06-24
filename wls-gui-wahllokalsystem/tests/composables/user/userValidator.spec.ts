import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useUserValidator } from "@/composables/user/userValidator.ts";

const { prepareUserDTO } = useUserTestDataFactory();
const { validUserDtoOrThrow } = useUserValidator();

describe("userValidator.ts", () => {
  it("should_returnUserDto_when_userIsValid", () => {
    const userDto = prepareUserDTO().build();

    const result = validUserDtoOrThrow(userDto);

    expect(result).toStrictEqual(userDto);
  });

  it.each([
    { invalid: "Empty", value: "" },
    { invalid: "Blank", value: " " },
    { invalid: "Null", value: null },
    { invalid: "Undefined", value: undefined },
  ])("should_throwError_when_userHas'$invalid'Field", async ({ value }) => {
    const userDto = prepareUserDTO()
      .username(value as string)
      .build();

    expect(() => validUserDtoOrThrow(userDto)).toThrow(
      "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: username"
    );
  });

  it("should_throwError_when_userHasNoAuthorities", () => {
    const userDto = prepareUserDTO().authorities(new Set<string>()).build();

    expect(() => validUserDtoOrThrow(userDto)).toThrow(
      "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: authorities"
    );
  });
});
