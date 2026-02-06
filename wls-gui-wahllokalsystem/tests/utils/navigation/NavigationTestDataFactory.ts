import type { ElectionWorkflowState } from "@/types/navigation/ElectionWorkflowState.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useCommonErgebnismeldungTestDataFactory } from "@tests/utils/ergebnismeldung/common/commonErgebnismeldungTestDataFactory.ts";

const { generateRandomBoolean } = useCommonTestDataFactory();
const { createBezirkUndWahlID } = useCommonErgebnismeldungTestDataFactory();

export function useWorkflowTestDataFactory() {
  function createElectionWorkflow(): ElectionWorkflowState {
    return {
      bezirkUndWahlID: createBezirkUndWahlID(),
      isSchnellmeldungDone: generateRandomBoolean(),
      isNiederschriftDone: generateRandomBoolean(),
      stepsDone: {},
    };
  }

  function prepareElectionWorkflow(): Builder<ElectionWorkflowState> {
    return proxyBuilder<ElectionWorkflowState>(createElectionWorkflow());
  }

  return {
    createElectionWorkflow,
    prepareElectionWorkflow,
  };
}
