<template>
  <v-dialog
    v-model="isDialogVisibleModel"
    persistent
    fullscreen
  >
    <v-card>
      <v-tabs
        v-model="tab"
        bg-color="grey-lighten-3"
        slider-color="primary"
        color="primary"
        class="rounded-t border-b"
      >
        <v-tab value="one">
          <v-icon icon="$stimmzettelBeschluss" />
          Beschluss fassen
        </v-tab>
        <v-tab value="two"> Stimmzettel anzeigen und bearbeiten </v-tab>
        <v-spacer />
        <div
          v-if="stimmzettel"
          class="stimmzettelkennung-container pr-4 text-no-wrap font-weight-bold"
        >
          {{ stimmzettel.teamID }}{{ stimmzettel.stimmzettelkennung }}
        </div>
      </v-tabs>
      <v-tabs-window v-model="tab">
        <v-tabs-window-item value="one">
          <the-beschluss-fassen-tab :stimmzettel="stimmzettel" />
        </v-tabs-window-item>
        <v-tabs-window-item value="two" />
      </v-tabs-window>
      <v-card-actions v-if="tab === 'one'">
        <base-text-button @click="onCancelClicked">Abbrechen</base-text-button>
        <base-wls-button-save
          save-text="Beschluss speichern"
          @click="onSaveClicked"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";

import { ref } from "vue";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import TheBeschlussFassenTab from "@/components/dse/beschlussfassung/TheBeschlussFassenTab.vue";

const isDialogVisibleModel = defineModel("modelValue", {
  type: Boolean,
  required: false,
});

defineProps<{
  stimmzettel: Stimmzettel | undefined;
}>();

const emit = defineEmits<{
  cancel: [];
  save: [];
}>();

const tab = ref("one");

function onCancelClicked() {
  emit("cancel");
}

function onSaveClicked() {
  emit("save");
}
</script>

<style scoped>
.stimmzettelkennung-container {
  align-self: stretch;
  display: flex;
  align-items: center;
  font-size: clamp(0.95rem, 6vh, 1.5rem);
}
</style>
