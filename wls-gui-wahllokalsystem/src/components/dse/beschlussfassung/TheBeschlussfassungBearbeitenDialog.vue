<template>
  <v-dialog
    v-model="isDialogVisibleModel"
    persistent
    fullscreen
  >
    <v-card>
      <v-tabs v-model="tab">
        <v-tab value="one">
          <v-icon
            icon="$stimmzettelBeschluss"
            size="x-large"
            class="mr-2"
          />
          Beschluss fassen
        </v-tab>
        <v-tab value="two"> Stimmzettel anzeigen und bearbeiten </v-tab>
        <v-spacer />
        <base-stimmzettelkennung-strong-text
          v-if="stimmzettel"
          :stimmzettelkennung="stimmzettel.stimmzettelkennung"
          :team-name="stimmzettel.teamID"
          compact
          class="mr-2"
        />
      </v-tabs>
      <v-tabs-window v-model="tab">
        <v-tabs-window-item value="one">
          <the-beschluss-fassen-tab v-model:stimmzettel="stimmzettel" />
        </v-tabs-window-item>
        <v-tabs-window-item value="two" />
      </v-tabs-window>
      <v-spacer />
      <v-card-actions>
        <base-text-button @click="onCancelClicked">Abbrechen</base-text-button>
        <base-wls-button-save
          v-if="tab === 'one'"
          save-text="Beschluss speichern"
          @click="onSaveClicked"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import { ref, watch } from "vue";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import TheBeschlussFassenTab from "@/components/dse/beschlussfassung/TheBeschlussFassenTab.vue";
import BaseStimmzettelkennungStrongText from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelkennungStrongText.vue";

const isDialogVisibleModel = defineModel("modelValue", {
  type: Boolean,
  required: false,
});

const stimmzettel = defineModel<PersistedStimmzettel | undefined>(
  "stimmzettel"
);

const emit = defineEmits<{
  cancel: [];
  save: [];
}>();

const tab = ref("one");

watch(isDialogVisibleModel, (isVisible) => {
  if (isVisible) {
    tab.value = "one";
  }
});

function onCancelClicked() {
  emit("cancel");
}

function onSaveClicked() {
  emit("save");
}
</script>
