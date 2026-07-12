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
          v-model="kennzeichen"
          label="Kennzeichen"
          variant="filled"
          data-test="wahl-kennzeichen"
        />
        <!-- Pfeiltasten-Eingabe wird unterbunden (ADR002): in der Capture-Phase
        abgefangen, bevor VNumberInput den Wert verändert. -->
        <div @keydown.capture="onNumberFieldKeydown">
          <v-number-input
            v-model="waehlerverzeichnisNummer"
            control-variant="hidden"
            label="Nummer des Wählerverzeichnisses"
            :rules="nichtNegativeZahlRules"
            data-test="wahl-waehlerverzeichnisnummer"
          />
          <v-number-input
            v-model="reihenfolge"
            control-variant="hidden"
            label="Wahlreihenfolge"
            :rules="nichtNegativeZahlRules"
            data-test="wahl-reihenfolge"
          />
        </div>
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
import type {
  FarbeDTO,
  WahlDTO,
} from "@/api/wls-clients/generated-basisdaten-api";

import { computed, reactive, ref } from "vue";
import {
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
  VColorPicker,
  VDialog,
  VNumberInput,
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
// VNumberInput liefert beim Leeren null, WahlDTO erlaubt aber nur number –
// daher eigene, nullbare Refs für die editierbaren Zahlenfelder.
const waehlerverzeichnisNummer = ref<number | null>(null);
const reihenfolge = ref<number | null>(null);
const kennzeichen = ref("");
const farbeHex = ref("#000000");

const isFormValid = computed(
  () =>
    istNichtNegativeZahl(waehlerverzeichnisNummer.value) &&
    istNichtNegativeZahl(reihenfolge.value)
);

const nichtNegativeZahlRules = [
  (value: unknown) =>
    istNichtNegativeZahl(value) ||
    "Bitte eine Zahl größer oder gleich 0 eingeben",
];

function showDialog(wahl: WahlDTO) {
  Object.assign(workingCopy, wahl);
  waehlerverzeichnisNummer.value = wahl.waehlerverzeichnisNummer;
  reihenfolge.value = wahl.reihenfolge;
  farbeHex.value = farbeToHex(wahl.farbe);
  kennzeichen.value = wahl.kennzeichen;
  visible.value = true;
}

function onNumberFieldKeydown(event: KeyboardEvent) {
  if (event.key === "ArrowUp" || event.key === "ArrowDown") {
    event.preventDefault();
    event.stopPropagation();
  }
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
    kennzeichen: kennzeichen.value,
    waehlerverzeichnisNummer: Number(waehlerverzeichnisNummer.value),
    reihenfolge: Number(reihenfolge.value),
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
    kennzeichen: "",
    name: "",
    farbe: { r: 0, g: 0, b: 0 },
    reihenfolge: 0,
    waehlerverzeichnisNummer: 0,
    wahltag: "",
    wahlart: "BTW",
  };
}
</script>
