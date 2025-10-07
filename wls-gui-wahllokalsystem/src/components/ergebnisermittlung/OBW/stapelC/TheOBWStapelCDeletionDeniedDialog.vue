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
      {{ stimmzettelThatPreventDeletion.length }}
      {{
        stimmzettelThatPreventDeletion.length === 1
          ? "Element verhindert"
          : "Elemente verhindern"
      }}
      das Löschen: Für
      {{ stimmzettelThatPreventDeletion.length === 1 ? "den" : "die" }}
      Stimmzettel {{ stimmzettelThatPreventDeletion.join(", ") }}
      {{
        stimmzettelThatPreventDeletion.length === 1
          ? "wurde bereits ein Beschluss"
          : "wurden bereits Beschlüsse"
      }}
      erfasst.
    </div>
  </base-dialog>
</template>

<script setup lang="ts">
import { ref } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";

const isConfirmDialogVisible = ref(false);
let stimmzettelThatPreventDeletion: number[] = [];

defineExpose({
  showDialog,
});

function onConfirmDialogConfirmClicked() {
  isConfirmDialogVisible.value = false;
}

function showDialog(stimmzettel: number[]) {
  stimmzettelThatPreventDeletion = stimmzettel;
  isConfirmDialogVisible.value = true;
}
</script>
