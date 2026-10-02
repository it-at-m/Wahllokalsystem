import type { BeschlussfassungDialogDetails } from "@/types/dse/beschlussfassung/BeschlussfassungDialogDetails.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useBeschlussgrundOptionTestDataFactory } from "@tests/utils/dse/BeschlussgrundOptionTestDataFactory.ts";

const { generateRandomBoolean, generateRandomString } =
  useCommonTestDataFactory();

const { createBeschlussgrundOption } = useBeschlussgrundOptionTestDataFactory();

export function useBeschlussfassungDialogDetailsTestDataFactory() {
  function createBeschlussfassungDialogDetails(): BeschlussfassungDialogDetails {
    return {
      isStimmzettelGueltig: generateRandomBoolean(),
      beschlussgruende: [createBeschlussgrundOption()],
      andererGrund: generateRandomString(5),
      andererGrundChecked: generateRandomBoolean(),
      beschlussText: generateRandomString(5),
    };
  }

  function prepareBeschlussfassungDialogDetails(): Builder<BeschlussfassungDialogDetails> {
    return proxyBuilder<BeschlussfassungDialogDetails>(
      createBeschlussfassungDialogDetails()
    );
  }

  return {
    createBeschlussfassungDialogDetails,
    prepareBeschlussfassungDialogDetails,
  };
}
