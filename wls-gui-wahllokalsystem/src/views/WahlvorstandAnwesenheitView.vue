<template>
  <v-card>
    <v-card-title class="d-flex align-center justify-space-between">
      <span>Anwesenheit prüfen</span>
      <div class="d-flex flex-column align-start">
        <the-wahlvorstand-latest-load-div
          :datetime="wahlvorstandStore.lastLoading"
          class="text-subtitle-2 mb-1"
        />
        <the-wahlvorstand-last-send-div
          :datetime="wahlvorstandStore.lastSending"
          class="text-subtitle-2"
        />
      </div>
    </v-card-title>
    <v-card-text>
      <the-wahlvorstand-mitglieder-table />
      <the-wahlvorstand-anwesenheit-requirement-card
        v-show="!wahlvorstandStore.isWahlvorstandAusreichendAnwesend"
      />
    </v-card-text>
    <v-card-actions>
      <base-button-refresh @click="wahlvorstandStore.loadWahlvorstand()" />
      <base-button-save
        :disabled="!wahlvorstandStore.isWahlvorstandAusreichendAnwesend"
        active
        @click="wahlvorstandStore.sendWahlvorstand()"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { VCard, VCardActions, VCardText, VCardTitle } from "vuetify/components";

import BaseButtonRefresh from "@/components/common/buttons/BaseButtonRefresh.vue";
import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import TheWahlvorstandAnwesenheitRequirementCard from "@/components/wahlvorstand/TheWahlvorstandAnwesenheitRequirementCard.vue";
import TheWahlvorstandLastSendDiv from "@/components/wahlvorstand/TheWahlvorstandLastSendDiv.vue";
import TheWahlvorstandLatestLoadDiv from "@/components/wahlvorstand/TheWahlvorstandLatestLoadDiv.vue";
import TheWahlvorstandMitgliederTable from "@/components/wahlvorstand/TheWahlvorstandMitgliederTable.vue";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore";

const wahlvorstandStore = useWahlvorstandStore();
</script>
