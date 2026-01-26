import type { ElectionWorkflow } from "@/types/navigation/ElectionWorkflow.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useCommonErgebnismeldungTestDataFactory } from "@tests/utils/ergebnismeldung/common/commonErgebnismeldungTestDataFactory.ts";

const { generateRandomBoolean } = useCommonTestDataFactory();
const { createBezirkUndWahlID } = useCommonErgebnismeldungTestDataFactory();

export function useWorkflowTestDataFactory() {
  function createElectionWorkflow(): ElectionWorkflow {
    return {
      bezirkUndWahlID: createBezirkUndWahlID(),
      isSchnellmeldungDone: generateRandomBoolean(),
      isNiederschriftDone: generateRandomBoolean(),
      stepsDone: {},
    };
  }

  function prepareElectionWorkflow(): Builder<ElectionWorkflow> {
    return proxyBuilder<ElectionWorkflow>(createElectionWorkflow());
  }

  return {
    createElectionWorkflow,
    prepareElectionWorkflow,
  };
}
