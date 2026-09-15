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
        {{ stimmzettel.teamID }}{{ stimmzettel.stimmzettelkennung }}
      </v-tabs>
      <v-tabs-window v-model="tab">
        <v-tabs-window-item value="one">
          <the-beschluss-fassen-tab />
        </v-tabs-window-item>
        <v-tabs-window-item value="two"/>
      </v-tabs-window>
      <v-card-actions>
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

const props = defineProps<{
  stimmzettel: Stimmzettel;
}>();

const emit = defineEmits<{
  cancel: [];
  confirmClose: [stimmzettel: Stimmzettel];
  confirmNext: [stimmzettel: Stimmzettel];
}>();

const tab = ref("one");

function onCancelClicked() {
  emit("cancel");
}

function onSaveClicked() {}
</script>
