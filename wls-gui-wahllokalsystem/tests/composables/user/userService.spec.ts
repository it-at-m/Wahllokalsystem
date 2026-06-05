import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useUserService } from "@/composables/user/userService.ts";

const mockDefinitions = vi.hoisted(() => ({
  user: vi.fn(),
  validateDtoAndMapToModel: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-auth-api", () => ({
  UserControllerApi: vi.fn().mockImplementation(
    class MockedUserControllerApi {
      user = mockDefinitions.user;
    } as never
  ),
  Configuration: vi.fn(),
}));
vi.mock("@/composables/user/userMapper.ts", () => ({
  useUserMapper: () => ({
    validateDtoAndMapToModel: mockDefinitions.validateDtoAndMapToModel,
  }),
}));

const { getUser } = useUserService();
const { prepareUserDTO, mapValidUserDtoToUser } = useUserTestDataFactory();
const { axiosConfigWrapper } = useCommonApiUtils();

describe("userService.ts", () => {
  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getUser", () => {
    it("should_returnUser_when_apiCalledSuccesfully", async () => {
      const userDto = prepareUserDTO().build();
      const mockedMappeduser = mapValidUserDtoToUser(userDto);

      mockDefinitions.user.mockReturnValue({ status: 200, data: userDto });
      mockDefinitions.validateDtoAndMapToModel.mockReturnValue(
        mockedMappeduser
      );

      const result = await getUser();

      expect(mockDefinitions.user.mock.calls).toStrictEqual([
        [axiosConfigWrapper().requestAsOnlineOnly()],
      ]);
      expect(result).toEqual(mockedMappeduser);
    });

    it("should_throwError_when_apiCallFailed", async () => {
      mockDefinitions.user.mockRejectedValue(
        new Error("mocked api call failed")
      );

      await expect(getUser()).rejects.toThrow("mocked api call failed");
      expect(mockDefinitions.validateDtoAndMapToModel).not.toHaveBeenCalled();
    });
  });
});
