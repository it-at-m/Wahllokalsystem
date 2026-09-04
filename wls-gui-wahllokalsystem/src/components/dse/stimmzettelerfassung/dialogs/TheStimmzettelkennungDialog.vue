<template>
  <base-dialog
    :visible="props.visible"
    dialogtitle="Übertragung der Stimmzettelkennung"
    icon="$information"
    confirmtext="Bestätigen"
    canceltext="Abbrechen"
    @confirm="onConfirmClicked"
    @cancel="$emit('cancel')"
  >
    <div>
      Bitte übertragen Sie die Stimmzettelkennung auf den Papier-Stimmzettel.
    </div>
    <base-stimmzettelkennung-strong-text
      :stimmzettelkennung="nextStimmzettelkennung"
      :team-name="teamName"
    />
  </base-dialog>
</template>

<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";

import { computed } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import BaseStimmzettelkennungStrongText from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelkennungStrongText.vue";
import { useStimmzettelkennungDialogUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelkennungDialogUtils.ts";

const { getNextStimmzettelNumber } = useStimmzettelkennungDialogUtils();

const props = defineProps<{
  visible: boolean;
  teamName: string;
  existingStimmzettel: Stimmzettel[];
}>();

const emit = defineEmits<{
  cancel: [];
  confirm: [confirmedStimmzettelKennung: number];
}>();

const nextStimmzettelkennung = computed(() =>
  getNextStimmzettelNumber(props.existingStimmzettel)
);

async function onConfirmClicked() {
  emit("confirm", nextStimmzettelkennung.value);
}
</script>
