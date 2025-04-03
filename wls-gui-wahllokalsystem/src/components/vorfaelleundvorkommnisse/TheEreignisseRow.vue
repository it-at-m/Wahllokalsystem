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
          :rules="[REQUIRED]"
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
          :rules="[MIN_LENGTH(4), MAX_LENGTH(500)]"
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
          @click="openDeleteDialog(index)"
        >
        </v-icon>
      </v-col>
    </v-row>
    <v-dialog
      v-model="deleteDialog"
      max-width="400"
    >
      <v-card>
        <v-card-title class="headline">Ereignis löschen</v-card-title>
        <v-card-text>
          Möchten Sie dieses Ereignis wirklich löschen?
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="grey"
            text
            @click="deleteDialog = false"
            >Abbrechen</v-btn
          >
          <v-btn
            color="red"
            text
            @click="confirmDelete"
            >Löschen</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";

import { storeToRefs } from "pinia";
import { onMounted, ref } from "vue";
import {
  VBtn,
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
  VCol,
  VDialog,
  VIcon,
  VRow,
  VSpacer,
  VTextarea,
  VTextField,
} from "vuetify/components";

import useFormatter from "@/composables/common/formatter.ts";
import { useEreignisStore } from "@/stores/vorfaelleundvorkommnisseStore.ts";
import { MAX_LENGTH, MIN_LENGTH, REQUIRED } from "@/util/rules.ts";

const { toHhMm } = useFormatter();
const ereignisStore = useEreignisStore();
const { wahlbezirkEreignisse } = storeToRefs(ereignisStore);
const deleteDialog = ref(false);
const deleteIndex = ref<number | null>(null);

const openDeleteDialog = (index: number) => {
  deleteIndex.value = index;
  deleteDialog.value = true;
};

const confirmDelete = () => {
  if (deleteIndex.value !== null) {
    wahlbezirkEreignisse.value.ereigniseintraege?.splice(deleteIndex.value, 1);
    deleteIndex.value = null; // Zurücksetzen
  }
  deleteDialog.value = false; // Dialog schließen
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
