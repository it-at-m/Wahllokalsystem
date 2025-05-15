import type { UserDTO } from "@/api/wls-clients/generated-auth-api";

import {
  defaultCatchHandler,
  defaultResponseHandler,
  fetchConfig,
} from "@/api/fetch-utils";
import { useUserMapper } from "@/composables/user/userMapper.ts";
import { User } from "@/types/User";

const { toModel } = useUserMapper();

/**
 * Retrieves the user data via the userinfo route of the API gateway. The SSO client must be configured so that
 * that the claims offered by Keycloak (see API definition) are correctly delivered in the protocol mapper.
 * You can check which mappers are set in Keycloak UI or the local development stack files under /stack/keycloak.
 * For testdata you might need to create custom user attributes and mappers manually.
 *
 * API-Definition (internal only): https://wiki.muenchen.de/betriebshandbuch/wiki/Red_Hat_Single_Sign-On_(Keycloak)#Scopes
 */
export function getUser(): Promise<User> {
  return fetch("api/sso/userinfo", fetchConfig())
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Laden des Users ist ein Fehler aufgetreten."
      );
      return response.json();
    })
    .then((json: UserDTO) => toModel(json));
}
