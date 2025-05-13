import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { User } from "@/types/User.ts";

const { generateRandomString } = useCommonTestDataFactory();

export function useUserTestDataFactory() {
  function createUserWithRandomWahlbezirkID(): User {
    const user = new User();
    user.wahlbezirkID = generateRandomString(10);
    return user;
  }

  function createUserWithUndefinedWahlbezirkID(): User {
    const user = new User();
    user.wahlbezirkID = undefined;
    return user;
  }

  return {
    createUserWithRandomWahlbezirkID,
    createUserWithUndefinedWahlbezirkID,
  };
}
