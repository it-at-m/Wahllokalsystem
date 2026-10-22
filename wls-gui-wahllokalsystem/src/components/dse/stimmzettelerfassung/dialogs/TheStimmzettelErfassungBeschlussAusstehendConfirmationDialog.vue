<template>
  <base-dialog
    :visible="visible"
    dialogtitle="Stimmzettel separat ablegen"
    confirmtext="Bestätigen"
    icon="$information"
  >
    <div>
      Bitte legen Sie den Stimmzettel für die Beschlussfassung separat ab.
    </div>
  </base-dialog>
</template>

<script setup lang="ts">
import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";

defineProps({
  visible: Boolean,
});
</script>
