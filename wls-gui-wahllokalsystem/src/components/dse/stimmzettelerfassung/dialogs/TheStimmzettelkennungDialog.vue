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
    <div class="mt-3 text-h1 font-weight-bold text-center">
      {{ teamName }} {{ nextStimmzettelkennung }}
    </div>
  </base-dialog>
</template>

<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";

import { computed } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
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
