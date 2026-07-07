import { useAxiosTestDataFactory } from "@tests/utils/common/AxiosTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import {
  afterAll,
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";

import { useRolesService } from "@/composables/user/rolesService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  toModel: vi.fn(),
  getRoleMappings: vi.fn(),
  configurationConstructor: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-auth-api", () => ({
  RolesControllerApi: class {
    getRoleMappings = mockDefinitions.getRoleMappings;
  },
  Configuration: mockDefinitions.configurationConstructor,
}));
vi.mock(import("@/composables/user/rolesMapper.ts"), () => ({
  useRolesMapper: () => ({
    toModel: mockDefinitions.toModel,
  }),
}));
vi.mock(
  import("@/composables/userNotification/userNotificationService.ts"),
  () => ({
    useUserNotificationService: () => ({
      addNotification: mockDefinitions.addNotification,
    }),
  })
);

const { createRoleMappingDTO, createRoleMapping } = useUserTestDataFactory();
const { createAxiosResponse } = useAxiosTestDataFactory();

describe("rolesService.ts", () => {
  let unitUnderTest: ReturnType<typeof useRolesService>;

  beforeEach(() => {
    unitUnderTest = useRolesService();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe("getRoles", () => {
    it("should_returnRoleMapping_when_apiCallReturnedDTO", async () => {
      const mockedDTO = createRoleMappingDTO();
      const mockedResponse = createAxiosResponse({
        status: 200,
        data: mockedDTO,
      });
      mockDefinitions.getRoleMappings.mockReturnValue(mockedResponse);

      const mockedMappedDTO = createRoleMapping();
      mockDefinitions.toModel.mockReturnValue(mockedMappedDTO);

      const result = await unitUnderTest.getRoles();
      expect(result).toStrictEqual(mockedMappedDTO);
    });

    it("should_throwErrorAndSendNotification_when_apiCalledThrewError", async () => {
      const mockedApiError = new Error("mocked api error");
      mockDefinitions.getRoleMappings.mockRejectedValue(mockedApiError);

      await expect(unitUnderTest.getRoles()).rejects.toThrowError(
        mockedApiError
      );
      expect(mockDefinitions.addNotification.mock.calls).toStrictEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
    });
  });

  describe("createEmptyMapping", () => {
    it("should_createAnObjectWithAllValuesEmptyString_when_called", () => {
      const result = unitUnderTest.createEmptyMapping();

      Object.entries(result).forEach(([key, value]) => {
        expect(value, `${key} is not an empty string`).toStrictEqual("");
      });
    });
  });
});
