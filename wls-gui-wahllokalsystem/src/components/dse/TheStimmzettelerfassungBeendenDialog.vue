<template>
  <base-dialog
    :visible="isDialogVisible"
    dialogtitle="Beenden der Stimmzettelerfassung"
    :is-confirm-loading="isSaving || isOfflineDataSyncing"
    confirmtext="Synchronisieren und Bestätigen"
    canceltext="Abbrechen"
    icon="$warning"
    @cancel="onCancelClicked"
    @confirm="onConfirmClicked"
  >
    <div>
      Bitte bestätigen Sie, dass die Stimmzettelerfassung in Ihrem Team
      abgeschlossen ist und keine weiteren Papier-Stimmzettel mehr erfasst oder
      korrigiert werden sollen.
    </div>
    <v-card
      v-if="isSyncWidgetVisible"
      class="mt-2"
    >
      <v-card-title>Offline-Synchronisierung</v-card-title>
      <v-card-text>
        <the-offline-data-sync-widget />
      </v-card-text>
    </v-card>
  </base-dialog>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import TheOfflineDataSyncWidget from "@/components/common/widgets/TheOfflineDataSyncWidget.vue";
import { useStimmzettelerfassungBeendenDialogUtils } from "@/composables/dse/StimmzettelerfassungBeendenDialogUtils.ts";
import { useDataSyncStore } from "@/stores/dataSyncStore.ts";

const dataSyncStore = useDataSyncStore();
const { isOfflineDataSyncing } = storeToRefs(dataSyncStore);

const isDialogVisible = defineModel("modelValue", {
  type: Boolean,
  required: true,
});

const props = defineProps<{
  wahlId: string;
  wahlbezirkId: string;
  teamId: string;
}>();

const {
  isSyncWidgetVisible,
  isSaving,
  syncronizeDataAndPostTeamErfassungDone,
} = useStimmzettelerfassungBeendenDialogUtils(
  props.wahlId,
  props.wahlbezirkId,
  closeDialog
);

function onCancelClicked(): void {
  closeDialog();
}

async function onConfirmClicked() {
  await syncronizeDataAndPostTeamErfassungDone();
}

function closeDialog() {
  isDialogVisible.value = false;
  isSyncWidgetVisible.value = false;
}
</script>
