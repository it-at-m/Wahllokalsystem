<template>
  <div>
    <v-row
      v-for="(ereignis, index) in wahlbezirkEreignisse.ereigniseintraege"
      :key="index"
    >
      <v-col cols="1">{{ index + 1 }}</v-col>
      <v-col cols="2">
        <v-text-field
          :model-value="toHhMm(ereignis.uhrzeit)"
          label="Uhrzeit"
          type="time"
          hide-details
          @update:model-value="
            (value) => onEreignisUhrzeitChanged(ereignis, value, index)
          "
        ></v-text-field>
      </v-col>
      <v-col>
        <v-textarea
          v-model="ereignis.beschreibung"
          rows="1"
          auto-grow
          label="Beschreibung"
          hide-details
          @update:model-value="(value) => (ereignis.beschreibung = value)"
        ></v-textarea>
      </v-col>
      <v-col cols="1">
        <v-icon
          icon="$delete"
          title="Löschen"
          @click="removeEreignis(index)"
        >
        </v-icon>
      </v-col>
    </v-row>
  </div>
</template>

<script setup lang="ts">
import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";

import { storeToRefs } from "pinia";
import { onMounted } from "vue";
import { VCol, VIcon, VRow, VTextarea, VTextField } from "vuetify/components";

import useFormatter from "@/composables/common/formatter.ts";
import { useEreignisStore } from "@/stores/vorfaelleundvorkommnisseStore.ts";

const { toHhMm } = useFormatter();
const ereignisStore = useEreignisStore();
const { wahlbezirkEreignisse } = storeToRefs(ereignisStore);

const removeEreignis = (index: number) => {
  wahlbezirkEreignisse.value.ereigniseintraege?.splice(index, 1);
};

onMounted(() => {
  loadEreignisse();
});

/**
 * Loads Ereignisse from the backend and sets it in the store.
 */
function loadEreignisse(): void {
  ereignisStore.loadEreignisse();
}

function onEreignisUhrzeitChanged(
  ereignis: Ereignis,
  uhrzeit: String,
  index: number
) {
  const timeInHoursAndMinutes = uhrzeit
    .split(":")
    .map((s) => Number.parseInt(s));
  const currentUhrzeit = ereignis.uhrzeit
    ? new Date(ereignis.uhrzeit)
    : undefined;
  currentUhrzeit?.setHours(timeInHoursAndMinutes[0], timeInHoursAndMinutes[1]);

  if (wahlbezirkEreignisse.value.ereigniseintraege) {
    wahlbezirkEreignisse.value.ereigniseintraege[index].uhrzeit =
      currentUhrzeit;
  }
}
</script>
