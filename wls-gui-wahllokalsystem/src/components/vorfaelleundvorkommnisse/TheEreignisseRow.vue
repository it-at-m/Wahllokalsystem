<template>
  <div>
    <v-row
      v-for="(ereignis, index) in wahlbezirkEreignisse.ereigniseintraege"
      :key="index"
    >
      <v-col
        cols="1"
        class="text-center mt-5"
        >{{ index + 1 }}</v-col
      >
      <v-col cols="2">
        <v-text-field
          :model-value="toHhMm(ereignis.uhrzeit)"
          :rules="[REQUIRED]"
          label="Uhrzeit"
          type="time"
          clearable
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
          label="Beschreibung"
          auto-grow
          clearable
          autofokus
        ></v-textarea>
      </v-col>
      <v-col
        cols="1"
        class="text-center mt-5"
      >
        <v-icon
          data-test="delete-ereignis-icon"
          icon="$delete"
          title="Löschen"
          @click="openDeleteDialog(index)"
        >
        </v-icon>
      </v-col>
    </v-row>
    <yes-no-dialog
      v-model="deleteDialog"
      dialogtitle="Ereignis löschen"
      dialogtext="Möchten Sie dieses Ereignis wirklich löschen?"
      @no="deleteDialog = false"
      @yes="confirmDelete"
    ></yes-no-dialog>
  </div>
</template>

<script setup lang="ts">
import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";

import { storeToRefs } from "pinia";
import { onMounted, ref } from "vue";
import { VCol, VIcon, VRow, VTextarea, VTextField } from "vuetify/components";

import YesNoDialog from "@/components/common/YesNoDialog.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { MAX_LENGTH, MIN_LENGTH, REQUIRED } from "@/util/rules.ts";

const { toHhMm } = useDateTimeFormatter();
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
  ereignisStore.loadEreignisse();
});

function onEreignisUhrzeitChanged(
  ereignis: Ereignis,
  uhrzeit: string,
  index: number
) {
  const updateUhrzeit = (time: Date | undefined) => {
    if (wahlbezirkEreignisse.value.ereigniseintraege) {
      wahlbezirkEreignisse.value.ereigniseintraege[index].uhrzeit = time;
    }
  };

  if (uhrzeit) {
    const [hours, minutes] = uhrzeit.split(":").map(Number);
    const currentUhrzeit = ereignis.uhrzeit
      ? new Date(ereignis.uhrzeit)
      : new Date();
    currentUhrzeit.setHours(hours, minutes);
    updateUhrzeit(currentUhrzeit);
  } else {
    updateUhrzeit(undefined);
  }
}
</script>
