import type { ResolvedUrlDTO } from "@/api/wls-clients/generated-auth-api";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomString } = useCommonTestDataFactory();

export function useResolvedUrlTestDataFactory() {
  function createResolvedUrlDTO(): ResolvedUrlDTO {
    return {
      url: `http://${generateRandomString(20)}`,
    };
  }

  return {
    createResolvedUrlDTO,
  };
}
