<template>
  <v-dialog
    :model-value="visible"
    width="500"
    data-test="dialog-wahl-bearbeiten"
    @update:model-value="onVisibilityChanged"
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
          :rules="nichtNegativeZahlRules"
          data-test="wahl-waehlerverzeichnisnummer"
        />
        <v-text-field
          v-model.number="workingCopy.reihenfolge"
          type="number"
          label="Wahlreihenfolge"
          :rules="nichtNegativeZahlRules"
          data-test="wahl-reihenfolge"
        />
        <div class="text-subtitle-2 mb-1">Wahlfarbe</div>
        <v-color-picker
          v-model="farbeHex"
          mode="rgb"
          :modes="['rgb', 'hex']"
          hide-inputs
          width="100%"
          data-test="wahl-farbe"
        />
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <base-button-cancel
          variant="text"
          @click="onCancelClicked"
        />
        <base-button-confirm
          color="primary"
          variant="elevated"
          :disabled="!isFormValid"
          @click="onSaveClicked"
        />
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
  VColorPicker,
  VDialog,
  VSpacer,
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
const farbeHex = ref("#000000");

const isFormValid = computed(
  () =>
    istNichtNegativeZahl(workingCopy.waehlerverzeichnisNummer) &&
    istNichtNegativeZahl(workingCopy.reihenfolge)
);

const nichtNegativeZahlRules = [
  (value: unknown) =>
    istNichtNegativeZahl(value) ||
    "Bitte eine Zahl größer oder gleich 0 eingeben",
];

function showDialog(wahl: WahlDTO) {
  Object.assign(workingCopy, wahl);
  farbeHex.value = farbeToHex(wahl.farbe);
  visible.value = true;
}

function hideDialog() {
  visible.value = false;
}

function onVisibilityChanged(value: boolean) {
  // Hält den lokalen Zustand mit Vuetify synchron, wenn der Dialog über ESC
  // oder Klick außerhalb geschlossen wird – sonst ließe er sich nicht erneut
  // öffnen.
  visible.value = value;
}

function onCancelClicked() {
  emit("cancel");
}

function onSaveClicked() {
  if (!isFormValid.value) {
    return;
  }
  const updatedWahl: WahlDTO = {
    ...workingCopy,
    waehlerverzeichnisNummer: Number(workingCopy.waehlerverzeichnisNummer),
    reihenfolge: Number(workingCopy.reihenfolge),
    farbe: hexToFarbe(farbeHex.value),
  };
  emit("save", updatedWahl);
}

function istNichtNegativeZahl(value: unknown): boolean {
  return typeof value === "number" && Number.isFinite(value) && value >= 0;
}

function farbeToHex(farbe?: FarbeDTO): string {
  const toHex = (value?: number) =>
    Math.min(255, Math.max(0, Math.round(Number(value) || 0)))
      .toString(16)
      .padStart(2, "0");
  return `#${toHex(farbe?.r)}${toHex(farbe?.g)}${toHex(farbe?.b)}`;
}

function hexToFarbe(hex: string): FarbeDTO {
  const normalized = hex.replace("#", "").slice(0, 6).padEnd(6, "0");
  return {
    r: parseInt(normalized.slice(0, 2), 16),
    g: parseInt(normalized.slice(2, 4), 16),
    b: parseInt(normalized.slice(4, 6), 16),
  };
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
