import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useUserService } from "@/composables/user/userService.ts";

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
const { prepareUserDTO, mapUserDtoToUser } = useUserTestDataFactory();

describe("userService.ts", () => {
  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getUser", () => {
    it("should_returnUser_when_apiCalledSuccesfully", async () => {
      const userDto = prepareUserDTO().build();
      const mockedMappeduser = mapUserDtoToUser(userDto);

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
