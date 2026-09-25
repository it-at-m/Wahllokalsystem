<template>
  <base-dialog
    :visible="isDialogVisible"
    dialogtitle="Druck der Beschlussentscheidungen"
    confirmtext="Schließen"
    icon="$information"
    @confirm="onConfirmClicked"
  >
    <div>
      Bitte fügen Sie den Druck der Beschlussentscheidungen als Anlage zur
      Niederschrift hinzu.
    </div>
  </base-dialog>
</template>

<script setup lang="ts">
import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";

const isDialogVisible = defineModel("modelValue", {
  type: Boolean,
  required: true,
});

function onConfirmClicked(): void {
  isDialogVisible.value = false;
}
</script>
