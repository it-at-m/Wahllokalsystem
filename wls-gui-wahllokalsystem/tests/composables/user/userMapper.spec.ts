import type { UserDTO } from "@/api/wls-clients/generated-auth-api";
import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useUserMapper } from "@/composables/user/userMapper.ts";

describe("userMapper.ts", () => {
  const { validateDtoAndMapToModel } = useUserMapper();
  const { prepareUserDTO, mapValidUserDtoToUser } = useUserTestDataFactory();

  describe("validateDtoAndMapToModel", () => {
    it("should_returnModel_when_givenValidDto", () => {
      const validDto: UserDTO = prepareUserDTO().build();
      const expectedUser = mapValidUserDtoToUser(validDto);

      const result = validateDtoAndMapToModel(validDto);

      expect(result).toEqual(expectedUser);
    });

    it.each([
      { invalid: "Empty", value: "" },
      { invalid: "Blank", value: " " },
      { invalid: "Null", value: null },
      { invalid: "Undefined", value: undefined },
    ])(
      `should_throwError_when_userHas'$invalid'Username`,
      async ({ value }) => {
        const invalidDto = prepareUserDTO()
          .username(value as string)
          .build();

        expect(() => validateDtoAndMapToModel(invalidDto)).toThrow(
          "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: Error: username is blank"
        );
      }
    );

    it.each([
      { invalid: "Empty", value: "" },
      { invalid: "Blank", value: " " },
      { invalid: "Null", value: null },
      { invalid: "Undefined", value: undefined },
    ])(
      `should_notThrowError_when_userHas'$invalid'Email`,
      async ({ value }) => {
        const validDto = prepareUserDTO()
          .email(value as string)
          .build();
        const expectedUser = mapValidUserDtoToUser(validDto);

        const result = validateDtoAndMapToModel(validDto);

        expect(result).toStrictEqual(expectedUser);
      }
    );

    it.each([
      { invalid: "Null", value: null },
      { invalid: "Undefined", value: undefined },
    ])(
      `should_throwError_when_userHas'$invalid'userEnabled`,
      async ({ value }) => {
        const invalidDto = prepareUserDTO()
          .userEnabled(value as unknown as boolean)
          .build();

        expect(() => validateDtoAndMapToModel(invalidDto)).toThrow(
          "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: Error: userEnabled is undefined"
        );
      }
    );

    it.each([
      { invalid: "Empty", value: "" },
      { invalid: "Blank", value: " " },
      { invalid: "Null", value: null },
      { invalid: "Undefined", value: undefined },
    ])(
      "should_throwError_when_userHas'$invalid'WahltagId",
      async ({ value }) => {
        const invalidDto = prepareUserDTO()
          .wahltagID(value as string)
          .build();

        expect(() => validateDtoAndMapToModel(invalidDto)).toThrow(
          "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: Error: wahltagID is blank"
        );
      }
    );

    it.each([
      { invalid: "Empty", value: "" },
      { invalid: "Blank", value: " " },
      { invalid: "Null", value: null },
      { invalid: "Undefined", value: undefined },
    ])("should_throwError_when_userHas'$invalid'Wahltag", async ({ value }) => {
      const invalidDto = prepareUserDTO()
        .wahltag(value as string)
        .build();

      expect(() => validateDtoAndMapToModel(invalidDto)).toThrow(
        "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: Error: wahltag is blank"
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
        const invalidDto = prepareUserDTO()
          .wahlbezirkID(value as string)
          .build();

        expect(() => validateDtoAndMapToModel(invalidDto)).toThrow(
          "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: Error: wahlbezirkID is blank"
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
        const invalidDto = prepareUserDTO()
          .wahlbezirkNummer(value as string)
          .build();

        expect(() => validateDtoAndMapToModel(invalidDto)).toThrow(
          "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: Error: wahlbezirkNummer is blank"
        );
      }
    );

    it.each([
      { invalid: "Null", value: null },
      { invalid: "Undefined", value: undefined },
    ])(
      `should_throwError_when_userHas'$invalid'WahlbezirksArt`,
      async ({ value }) => {
        const invalidDto = prepareUserDTO()
          .wahlbezirksArt(value as unknown as WahlbezirksArtEnum)
          .build();

        expect(() => validateDtoAndMapToModel(invalidDto)).toThrow(
          "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: Error: wahlbezirksArt is blank"
        );
      }
    );

    it.each([
      { invalid: "Empty", value: "" },
      { invalid: "Blank", value: " " },
      { invalid: "Null", value: null },
      { invalid: "Undefined", value: undefined },
    ])("should_throwError_when_userHas'$invalid'Pin", async ({ value }) => {
      const invalidDto = prepareUserDTO()
        .pin(value as string)
        .build();

      expect(() => validateDtoAndMapToModel(invalidDto)).toThrow(
        "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: Error: pin is blank"
      );
    });

    it("should_throwError_when_userHasNoAuthorities", () => {
      const invalidDto = prepareUserDTO()
        .authorities(new Set<string>())
        .build();

      expect(() => validateDtoAndMapToModel(invalidDto)).toThrow(
        "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: Error: authorities is empty"
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
        const invalidDto = prepareUserDTO()
          .wbid_wahlnummer(value as string)
          .build();

        expect(() => validateDtoAndMapToModel(invalidDto)).toThrow(
          "Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: Error: wbid_wahlnummer is blank"
        );
      }
    );
  });
});
