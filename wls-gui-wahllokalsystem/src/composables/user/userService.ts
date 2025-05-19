import {
  Configuration,
  UserControllerApi,
} from "@/api/wls-clients/generated-auth-api";
import { useUserMapper } from "@/composables/user/userMapper.ts";
import { AUTH_SERVICE_API_URL } from "@/constants.ts";
import { User } from "@/types/User.ts";

const { toModel } = useUserMapper();

export function useUserService() {
  const userControllerApi = new UserControllerApi(
    new Configuration({
      basePath: AUTH_SERVICE_API_URL,
    })
  );

  async function getUser(): Promise<User> {
    try {
      const response = await userControllerApi.user();
      return toModel(response.data);
    } catch {
      throw new Error("Fehler beim Laden des Users.");
    }
  }

  return { getUser };
}
