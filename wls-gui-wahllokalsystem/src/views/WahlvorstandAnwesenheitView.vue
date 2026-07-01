<template>
  <v-card>
    <v-card-title class="d-flex align-center justify-space-between">
      <span>Anwesenheit prüfen</span>
      <div class="d-flex flex-column align-start">
        <the-wahlvorstand-latest-load-div
          :datetime="lastLoading"
          class="text-subtitle-2 mb-1"
        />
        <the-wahlvorstand-last-send-div
          :datetime="lastSending"
          class="text-subtitle-2"
        />
      </div>
    </v-card-title>
    <v-card-text>
      <the-wahlvorstand-mitglieder-table />
      <the-wahlvorstand-anwesenheit-requirement-card
        v-show="!isWahlvorstandAusreichendAnwesend"
      />
    </v-card-text>
    <v-card-actions>
      <base-wls-button-save
        :loading="isSaving"
        :disabled="!isWahlvorstandAusreichendAnwesend"
        :save-text="SAVE_CONTINUE"
        @click="onSaveWahlvorstandCLicked"
      />
      <base-button-refresh
        :loading="isLoading"
        @click="forceLoadWahlvorstand()"
      />
      <the-nachbesetzung-drucken-button />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import BaseButtonRefresh from "@/components/common/buttons/BaseButtonRefresh.vue";
import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import TheNachbesetzungDruckenButton from "@/components/wahlvorstand/TheNachbesetzungDruckenButton.vue";
import TheWahlvorstandAnwesenheitRequirementCard from "@/components/wahlvorstand/TheWahlvorstandAnwesenheitRequirementCard.vue";
import TheWahlvorstandLastSendDiv from "@/components/wahlvorstand/TheWahlvorstandLastSendDiv.vue";
import TheWahlvorstandLatestLoadDiv from "@/components/wahlvorstand/TheWahlvorstandLatestLoadDiv.vue";
import TheWahlvorstandMitgliederTable from "@/components/wahlvorstand/TheWahlvorstandMitgliederTable.vue";
import { useNavigationService } from "@/composables/navigation/navigationService.ts";
import { SAVE_CONTINUE } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore";

const { forceLoadWahlvorstand, sendWahlvorstand } = useWahlvorstandStore();
const { getNextRoute } = useNavigationService();

const {
  isWahlvorstandAusreichendAnwesend,
  lastLoading,
  lastSending,
  isLoading,
  isSaving,
} = storeToRefs(useWahlvorstandStore());

async function onSaveWahlvorstandCLicked() {
  await sendWahlvorstand();
  await router.push(getNextRoute());
}
</script>
