import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

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
  ])(`should_throwError_when_userHas'$invalid'Username`, async ({ value }) => {
    const userDto = prepareUserDTO()
      .username(value as string)
      .build();

    expect(() => validUserDtoOrThrow(userDto)).toThrow(
      "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: username"
    );
  });

  it.each([
    { invalid: "Empty", value: "" },
    { invalid: "Blank", value: " " },
    { invalid: "Null", value: null },
    { invalid: "Undefined", value: undefined },
  ])(`should_notThrowError_when_userHas'$invalid'Email`, async ({ value }) => {
    const userDto = prepareUserDTO()
      .email(value as string)
      .build();

    const result = validUserDtoOrThrow(userDto);

    expect(result).toStrictEqual(userDto);
  });

  it.each([
    { invalid: "Null", value: null },
    { invalid: "Undefined", value: undefined },
  ])(
    `should_throwError_when_userHas'$invalid'userEnabled`,
    async ({ value }) => {
      const userDto = prepareUserDTO()
        .userEnabled(value as unknown as boolean)
        .build();

      expect(() => validUserDtoOrThrow(userDto)).toThrow(
        "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: userEnabled"
      );
    }
  );

  it.each([
    { invalid: "Empty", value: "" },
    { invalid: "Blank", value: " " },
    { invalid: "Null", value: null },
    { invalid: "Undefined", value: undefined },
  ])("should_throwError_when_userHas'$invalid'WahltagId", async ({ value }) => {
    const userDto = prepareUserDTO()
      .wahltagID(value as string)
      .build();

    expect(() => validUserDtoOrThrow(userDto)).toThrow(
      "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: wahltagID"
    );
  });

  it.each([
    { invalid: "Empty", value: "" },
    { invalid: "Blank", value: " " },
    { invalid: "Null", value: null },
    { invalid: "Undefined", value: undefined },
  ])("should_throwError_when_userHas'$invalid'Wahltag", async ({ value }) => {
    const userDto = prepareUserDTO()
      .wahltag(value as string)
      .build();

    expect(() => validUserDtoOrThrow(userDto)).toThrow(
      "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: wahltag"
    );
  });

  it.each([
    { invalid: "Empty", value: "" },
    { invalid: "Blank", value: " " },
    { invalid: "Null", value: null },
    { invalid: "Undefined", value: undefined },
  ])(
    "should_throwError_when_userHas'$invalid'WahlbezirkID",
    async ({ value }) => {
      const userDto = prepareUserDTO()
        .wahlbezirkID(value as string)
        .build();

      expect(() => validUserDtoOrThrow(userDto)).toThrow(
        "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: wahlbezirkID"
      );
    }
  );

  it.each([
    { invalid: "Empty", value: "" },
    { invalid: "Blank", value: " " },
    { invalid: "Null", value: null },
    { invalid: "Undefined", value: undefined },
  ])(
    "should_throwError_when_userHas'$invalid'WahlbezirkNummer",
    async ({ value }) => {
      const userDto = prepareUserDTO()
        .wahlbezirkNummer(value as string)
        .build();

      expect(() => validUserDtoOrThrow(userDto)).toThrow(
        "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: wahlbezirkNummer"
      );
    }
  );

  it.each([
    { invalid: "Null", value: null },
    { invalid: "Undefined", value: undefined },
  ])(
    `should_throwError_when_userHas'$invalid'WahlbezirksArt`,
    async ({ value }) => {
      const userDto = prepareUserDTO()
        .wahlbezirksArt(value as unknown as WahlbezirksArtEnum)
        .build();

      expect(() => validUserDtoOrThrow(userDto)).toThrow(
        "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: wahlbezirksArt"
      );
    }
  );

  it.each([
    { invalid: "Empty", value: "" },
    { invalid: "Blank", value: " " },
    { invalid: "Null", value: null },
    { invalid: "Undefined", value: undefined },
  ])("should_throwError_when_userHas'$invalid'Pin", async ({ value }) => {
    const userDto = prepareUserDTO()
      .pin(value as string)
      .build();

    expect(() => validUserDtoOrThrow(userDto)).toThrow(
      "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: pin"
    );
  });

  it("should_throwError_when_userHasNoAuthorities", () => {
    const userDto = prepareUserDTO().authorities(new Set<string>()).build();

    expect(() => validUserDtoOrThrow(userDto)).toThrow(
      "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: authorities"
    );
  });

  it.each([
    { invalid: "Empty", value: "" },
    { invalid: "Blank", value: " " },
    { invalid: "Null", value: null },
    { invalid: "Undefined", value: undefined },
  ])(
    "should_throwError_when_userHas'$invalid'WbidWahlnummer",
    async ({ value }) => {
      const userDto = prepareUserDTO()
        .wbid_wahlnummer(value as string)
        .build();

      expect(() => validUserDtoOrThrow(userDto)).toThrow(
        "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: wbid_wahlnummer"
      );
    }
  );
});
