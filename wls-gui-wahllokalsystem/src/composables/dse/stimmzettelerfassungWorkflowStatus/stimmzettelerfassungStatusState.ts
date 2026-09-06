import type { StimmzettelerfassungStatus } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatus.ts";

import { readonly, ref } from "vue";

import { useDseWorkflowStatusService } from "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusService.ts";

export function useStimmzettelerfassungStatusState(
  wahlID: string,
  wahlbezirkID: string
) {
  const isWorkflowStatusLoading = ref(false);
  const workflowStatus = ref<StimmzettelerfassungStatus | null>(null);

  const { loadDseWorkflowStatus } = useDseWorkflowStatusService();

  async function loadWorkflowStatus() {
    isWorkflowStatusLoading.value = true;
    try {
      workflowStatus.value = await loadDseWorkflowStatus(
        wahlID,
        wahlbezirkID,
        true
      );
    } finally {
      isWorkflowStatusLoading.value = false;
    }
  }

  return {
    isWorkflowStatusLoading: readonly(isWorkflowStatusLoading),
    workflowStatus: readonly(workflowStatus),
    loadWorkflowStatus,
  };
}
