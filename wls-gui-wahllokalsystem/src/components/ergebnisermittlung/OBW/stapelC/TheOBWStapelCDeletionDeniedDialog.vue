<template>
  <base-dialog
    :visible="isConfirmDialogVisible"
    dialogtitle="Reduzierung der Anzahl bedenklicher Stimmzettel"
    confirmtext="Hinweis schließen"
    icon="$information"
    @confirm="onConfirmDialogConfirmClicked"
  >
    <div class="mb-4">
      Sie haben die Anzahl der bedenklichen Stimmzettel verändert. Zeilen können
      nur gelöscht werden, wenn für einen Stimmzettel noch kein Beschluss
      erfasst wurde.
    </div>
    <div>
      {{ contextThatPreventDeletion }}
    </div>
  </base-dialog>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";

const isConfirmDialogVisible = ref(false);
const stimmzettelThatPreventDeletion = ref<number[]>([]);

defineExpose({
  showDialog,
});

const contextThatPreventDeletion = computed(() => {
  return (
    stimmzettelThatPreventDeletion.value.length +
    (stimmzettelThatPreventDeletion.value.length === 1
      ? " Element verhindert das Löschen: Für den Stimmzettel "
      : " Elemente verhindern das Löschen: Für die Stimmzettel ") +
    stimmzettelThatPreventDeletion.value.join(", ") +
    (stimmzettelThatPreventDeletion.value.length === 1
      ? " wurde bereits ein Beschluss erfasst."
      : " wurden bereits Beschlüsse erfasst.")
  );
});

function onConfirmDialogConfirmClicked() {
  isConfirmDialogVisible.value = false;
}

function showDialog(stimmzettel: number[]) {
  stimmzettelThatPreventDeletion.value = stimmzettel;
  isConfirmDialogVisible.value = true;
}
</script>
