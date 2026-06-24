<template>
  <v-dialog
    :model-value="visible"
    width="500"
    data-test="dialog-wahl-bearbeiten"
  >
    <v-card>
      <v-card-title>Wahl bearbeiten</v-card-title>
      <v-card-text>
        <v-text-field
          :model-value="workingCopy.name"
          label="Name der Wahl"
          readonly
          variant="filled"
          data-test="wahl-name"
        />
        <v-text-field
          v-model.number="workingCopy.waehlerverzeichnisNummer"
          type="number"
          label="Nummer des Wählerverzeichnis"
          data-test="wahl-waehlerverzeichnisnummer"
        />
        <v-text-field
          v-model.number="workingCopy.reihenfolge"
          type="number"
          label="Wahlreihenfolge"
          data-test="wahl-reihenfolge"
        />
        <div class="text-subtitle-2 mb-1">Wahlfarbe</div>
        <div class="d-flex align-center ga-2">
          <div
            class="wahl-farbe-preview"
            :style="{ backgroundColor: farbeCssColor }"
            data-test="wahl-farbe-preview"
          />
          <v-text-field
            v-model.number="farbe.r"
            type="number"
            min="0"
            max="255"
            label="R"
            density="compact"
            data-test="wahl-farbe-r"
          />
          <v-text-field
            v-model.number="farbe.g"
            type="number"
            min="0"
            max="255"
            label="G"
            density="compact"
            data-test="wahl-farbe-g"
          />
          <v-text-field
            v-model.number="farbe.b"
            type="number"
            min="0"
            max="255"
            label="B"
            density="compact"
            data-test="wahl-farbe-b"
          />
        </div>
      </v-card-text>
      <v-card-actions>
        <base-button-cancel @click="onCancelClicked" />
        <base-button-confirm @click="onSaveClicked" />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script setup lang="ts">
import type { FarbeDTO, WahlDTO } from "@/api/wls-clients/generated-admin-api";

import { computed, reactive, ref } from "vue";
import {
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
  VDialog,
  VTextField,
} from "vuetify/components";

import BaseButtonCancel from "@/components/common/BaseButtonCancel.vue";
import BaseButtonConfirm from "@/components/common/BaseButtonConfirm.vue";

const emit = defineEmits<{
  save: [wahl: WahlDTO];
  cancel: [];
}>();

defineExpose({ showDialog, hideDialog });

const visible = ref(false);
const workingCopy = reactive<WahlDTO>(createEmptyWahl());
const farbe = reactive<FarbeDTO>({ r: 0, g: 0, b: 0 });

const farbeCssColor = computed(
  () => `rgb(${Number(farbe.r)}, ${Number(farbe.g)}, ${Number(farbe.b)})`
);

function showDialog(wahl: WahlDTO) {
  Object.assign(workingCopy, wahl);
  Object.assign(farbe, {
    r: wahl.farbe?.r ?? 0,
    g: wahl.farbe?.g ?? 0,
    b: wahl.farbe?.b ?? 0,
  });
  visible.value = true;
}

function hideDialog() {
  visible.value = false;
}

function onCancelClicked() {
  emit("cancel");
}

function onSaveClicked() {
  const updatedWahl: WahlDTO = {
    ...workingCopy,
    waehlerverzeichnisNummer: Number(workingCopy.waehlerverzeichnisNummer),
    reihenfolge: Number(workingCopy.reihenfolge),
    farbe: {
      r: Number(farbe.r),
      g: Number(farbe.g),
      b: Number(farbe.b),
    },
  };
  emit("save", updatedWahl);
}

function createEmptyWahl(): WahlDTO {
  return {
    wahlID: "",
    name: "",
    reihenfolge: 0,
    waehlerverzeichnisNummer: 0,
    wahltag: "",
    wahlart: "BTW",
  };
}
</script>
<style scoped>
.wahl-farbe-preview {
  width: 32px;
  height: 32px;
  border-radius: 4px;
  border: 1px solid rgba(var(--v-theme-on-surface), 0.2);
  flex: 0 0 auto;
}
</style>
