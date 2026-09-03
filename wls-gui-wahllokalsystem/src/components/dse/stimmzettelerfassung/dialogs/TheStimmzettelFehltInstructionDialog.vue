<template>
  <base-dialog
    :visible="isVisible"
    dialogtitle="Bitte Kennung anbringen"
    confirmtext="Bestätigen"
    icon="$information"
  >
    Bitte notieren Sie die Stimmzettelkennung auf dem Umschlag oder auf dem
    Hilfsblatt.
  </base-dialog>
</template>

<script setup lang="ts">
import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";

defineModel("isVisible", { type: Boolean, required: true });
</script>
