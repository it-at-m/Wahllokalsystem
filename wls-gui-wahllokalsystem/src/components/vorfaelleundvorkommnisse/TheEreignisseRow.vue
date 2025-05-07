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
        <base-time-input v-model="ereignis.uhrzeit" />
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
        />
      </v-col>
      <v-col
        cols="1"
        class="text-center mt-5"
      >
        <v-icon
          data-test="delete-ereignis-icon"
          icon="$delete"
          title="Löschen"
          @click="onDeleteIconClicked(index)"
        />
      </v-col>
    </v-row>
    <yes-no-dialog
      v-model="deleteDialog"
      dialogtitle="Ereignis löschen"
      dialogtext="Möchten Sie dieses Ereignis wirklich löschen?"
      @no="onYesNoDialogNoClicked"
      @yes="onYesNoDialogYesClicked"
    />
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { onMounted, ref } from "vue";
import { VCol, VIcon, VRow, VTextarea } from "vuetify/components";

import BaseTimeInput from "@/components/common/inputs/BaseTimeInput.vue";
import YesNoDialog from "@/components/common/YesNoDialog.vue";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { MAX_LENGTH, MIN_LENGTH } from "@/util/rules.ts";

const ereignisStore = useEreignisStore();
const { wahlbezirkEreignisse } = storeToRefs(ereignisStore);
const deleteDialog = ref(false);
const deleteIndex = ref<number | null>(null);

onMounted(() => {
  ereignisStore.loadEreignisse();
});

function closeYesNoDialog() {
  deleteDialog.value = false;
}

function showYesNoDialogForItem(index: number) {
  deleteIndex.value = index;
  deleteDialog.value = true;
}

function onDeleteIconClicked(index: number) {
  showYesNoDialogForItem(index);
}

function onYesNoDialogNoClicked() {
  closeYesNoDialog();
}

function onYesNoDialogYesClicked() {
  if (deleteIndex.value !== null) {
    ereignisStore.deleteEreignisByIndex(deleteIndex.value);
    deleteIndex.value = null;
  }
  closeYesNoDialog();
}
</script>
