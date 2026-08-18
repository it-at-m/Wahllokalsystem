import type { StimmzettelerfassungStatus } from "@/types/dse/StimmzettelerfassungStatus.ts";

import { onActivated, readonly, ref } from "vue";

import { useDseWorkflowStatusService } from "@/composables/dse/dseWorkflowStatusService.ts";

const workflowStatusService = useDseWorkflowStatusService();

export function useDseWorkflowStatusUtils(
  wahlID: string,
  wahlbezirkID: string
) {
  const workflowStatus = ref<StimmzettelerfassungStatus | null>(null);
  const isWorkflowStatusLoading = ref(false);

  //Hooks
  onActivated(async () => {
    await Promise.allSettled([_loadWorkflowStatus()]);
  });

  //actions
  async function reloadWorkflowStatus() {
    await _loadWorkflowStatus();
  }

  async function _loadWorkflowStatus() {
    isWorkflowStatusLoading.value = true;
    try {
      const loaded = await workflowStatusService.loadDseWorkflowStatus(
        wahlID,
        wahlbezirkID,
        false
      );
      if (loaded) {
        workflowStatus.value = loaded;
      }
    } finally {
      isWorkflowStatusLoading.value = false;
    }
  }

  return {
    workflowStatus: readonly(workflowStatus),
    isWorkflowStatusLoading: readonly(isWorkflowStatusLoading),
    reloadWorkflowStatus,
  };
}
