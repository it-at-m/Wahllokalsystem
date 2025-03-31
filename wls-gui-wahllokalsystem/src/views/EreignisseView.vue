<template>
  <div>
    <div class="text-h1">Dokumentation eingetretener Ereignisse</div>

    <the-ereignisse-table />

    <base-button-save @click="ereignisStore.sendEreignisse()" />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import TheEreignisseTable from "@/components/vorfaelleundvorkommnisse/TheEreignisseTable.vue";
import { useEreignisStore } from "@/stores/vorfaelleundvorkommnisseStore.ts";

const ereignisStore = useEreignisStore();

onMounted(() => {
  loadEreignisse();
});

/**
 * Loads Ereignisse from the backend and sets it in the store.
 */
function loadEreignisse(): void {
  ereignisStore.loadEreignisse();
}
</script>
