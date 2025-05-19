import { useUserTestDataFactory } from "@tests/utils/common/UserTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useUserService } from "@/composables/user/userService.ts";
import { User } from "@/types/User.ts";

const mockDefinitions = vi.hoisted(() => ({
  user: vi.fn(),
  toModel: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-auth-api", () => ({
  UserControllerApi: vi.fn().mockImplementation(() => ({
    user: mockDefinitions.user,
  })),
  Configuration: vi.fn(),
}));
vi.mock("@/composables/user/userMapper.ts", () => ({
  useUserMapper: () => ({
    toModel: mockDefinitions.toModel,
  }),
}));

const { getUser } = useUserService();
const {
  createUserDtoWithRandomValues,
  mapDtoWbIdWahlnummerToModelWahlMetaData,
} = useUserTestDataFactory();

describe("userService.ts", () => {
  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getUser", () => {
    it("should_returnUser_when_apiCalledSuccesfully", async () => {
      const userDto = createUserDtoWithRandomValues();
      const mockedMappeduser: User = {
        username: userDto.username,
        email: userDto.email,
        userEnabled: userDto.userEnabled,
        wahltagID: userDto.wahltagID,
        wahltag: userDto.wahltag,
        wahlbezirkID: userDto.wahlbezirkID,
        wahlbezirkNummer: userDto.wahlbezirkNummer,
        wahlbezirksArt: userDto.wahlbezirksArt,
        pin: userDto.pin,
        authorities: userDto.authorities,
        wahlMetaData: mapDtoWbIdWahlnummerToModelWahlMetaData(
          userDto.wbid_wahlnummer
        ),
      };

      mockDefinitions.user.mockReturnValue({ status: 200, data: userDto });
      mockDefinitions.toModel.mockReturnValue(mockedMappeduser);

      const result = await getUser();

      expect(mockDefinitions.user.mock.calls.length).toStrictEqual(1);
      expect(result).toEqual(mockedMappeduser);
    });

    it("should_throwError_when_apiCallFailed", async () => {
      mockDefinitions.user.mockRejectedValue(
        new Error("mocked api call failed")
      );

      await expect(getUser()).rejects.toThrow("Fehler beim Laden des Users.");
      expect(mockDefinitions.toModel).not.toHaveBeenCalled();
    });
  });
});
