<template>
  <base-dialog
    :visible="isConfirmDialogVisible"
    dialogtitle="Reduzierung der Anzahl bedenklicher Stimmzettel"
    confirmtext="Hinweis schließen"
    icon="$information"
    @confirm="onConfirmDialogConfirmClicked"
  >
    Sie haben die Anzahl der bedenklichen Stimmzettel verändert. Zeilen können
    nur gelöscht werden, wenn für einen Stimmzettel noch kein Beschluss im
    System gespeichert worden ist. Bitte überprüfen Sie Ihre Eingaben.
  </base-dialog>
</template>

<script setup lang="ts">
import { ref } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";

const isConfirmDialogVisible = ref(false);

defineExpose({
  showDialog,
});

function onConfirmDialogConfirmClicked() {
  isConfirmDialogVisible.value = false;
}

function showDialog() {
  isConfirmDialogVisible.value = true;
}
</script>
