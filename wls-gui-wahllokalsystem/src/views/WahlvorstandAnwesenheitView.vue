<template>
  <div>
    <div class="text-h1">Wahlvorstand</div>
    <the-wahlvorstand-latest-load-div
      :datetime="wahlvorstandStore.lastLoading"
    />
    <the-wahlvorstand-last-send-div :datetime="wahlvorstandStore.lastSending" />
    <the-wahlvorstand-mitglieder-table />
    <the-wahlvorstand-anwesenheit-requirement-card
      v-show="!wahlvorstandStore.isWahlvorstandAusreichendAnwesend"
    />
    <base-button-refresh @click="wahlvorstandStore.loadWahlvorstand()" />
    <base-button-save
      :disabled="!wahlvorstandStore.isWahlvorstandAusreichendAnwesend"
      @click="wahlvorstandStore.sendWahlvorstand()"
    />
  </div>
</template>

<script setup lang="ts">
import BaseButtonRefresh from "@/components/base/buttons/BaseButtonRefresh.vue";
import BaseButtonSave from "@/components/base/buttons/BaseButtonSave.vue";
import TheWahlvorstandAnwesenheitRequirementCard from "@/components/wahlvorstand/TheWahlvorstandAnwesenheitRequirementCard.vue";
import TheWahlvorstandLastSendDiv from "@/components/wahlvorstand/TheWahlvorstandLastSendDiv.vue";
import TheWahlvorstandLatestLoadDiv from "@/components/wahlvorstand/TheWahlvorstandLatestLoadDiv.vue";
import TheWahlvorstandMitgliederTable from "@/components/wahlvorstand/TheWahlvorstandMitgliederTable.vue";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore";

const wahlvorstandStore = useWahlvorstandStore();
</script>
