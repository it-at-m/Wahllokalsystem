<template>
  <div data-test="list-wahlen">
    <template v-if="wahlen.length > 0">
      <v-table density="comfortable">
        <thead>
          <tr>
            <th>Name der Wahl</th>
            <th>Kennzeichen</th>
            <th>Nummer des Wählerverzeichnisses</th>
            <th>Wahlreihenfolge</th>
            <th>Wahlfarbe</th>
            <th class="text-right">Aktion</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="wahl in sortedWahlen"
            :key="wahl.wahlID"
            data-test="wahl-row"
          >
            <td data-test="wahl-name">{{ wahl.name }}</td>
            <td>{{ wahl.kennzeichen }}</td>
            <td>{{ wahl.waehlerverzeichnisNummer }}</td>
            <td>{{ wahl.reihenfolge }}</td>
            <td>
              <span
                class="wahl-farbe-swatch"
                :style="{ backgroundColor: toCssColor(wahl.farbe) }"
                data-test="wahl-farbe-swatch"
              />
            </td>
            <td class="text-right">
              <v-btn
                :loading="isSaving"
                :prepend-icon="mdiPencil"
                variant="outlined"
                color="primary"
                size="small"
                data-test="edit-wahl"
                @click="onEditWahlClicked(wahl)"
                >Bearbeiten</v-btn
              >
            </td>
          </tr>
        </tbody>
      </v-table>
    </template>
    <div
      v-else
      class="text-medium-emphasis py-4 text-center"
      data-test="wahlen-empty"
    >
      Keine Wahlen gefunden.
    </div>

    <base-dialog-wahl-bearbeiten
      ref="wahlBearbeitenDialog"
      @save="onDialogSave"
      @cancel="onDialogCancel"
    />
  </div>
</template>
<script setup lang="ts">
import type {
  FarbeDTO,
  WahlDTO,
} from "@/api/wls-clients/generated-basisdaten-api";

import { mdiPencil } from "@mdi/js";
import { computed, onMounted, ref, useTemplateRef, watch } from "vue";
import { VBtn, VTable } from "vuetify/components";

import BaseDialogWahlBearbeiten from "@/components/wahltag/BaseDialogWahlBearbeiten.vue";
import { useWahlenService } from "@/composables/wahlen/wahlenService.ts";

const { getWahlen, updateWahlen, isSaving } = useWahlenService();

const props = defineProps({
  wahltagId: {
    type: String,
    required: true,
  },
});

const templateRefWahlBearbeitenDialog = useTemplateRef<
  InstanceType<typeof BaseDialogWahlBearbeiten>
>("wahlBearbeitenDialog");

const wahlen = ref<WahlDTO[]>([]);

const sortedWahlen = computed(() =>
  [...wahlen.value].sort((a, b) => a.reihenfolge - b.reihenfolge)
);

onMounted(loadWahlen);
watch(() => props.wahltagId, loadWahlen);

async function loadWahlen() {
  try {
    wahlen.value = await getWahlen(props.wahltagId);
  } catch {
    wahlen.value = [];
  }
}

function onEditWahlClicked(wahl: WahlDTO) {
  templateRefWahlBearbeitenDialog.value?.showDialog(wahl);
}

async function onDialogSave(updatedWahl: WahlDTO) {
  // Reentrante Speichervorgänge verhindern: Ein zweiter Aufruf würde
  // mergedWahlen aus dem noch nicht aktualisierten wahlen.value bauen und damit
  // die gerade laufende Speicherung überschreiben.
  if (isSaving.value) {
    return;
  }
  const mergedWahlen = wahlen.value.map((wahl) =>
    wahl.wahlID === updatedWahl.wahlID ? updatedWahl : wahl
  );
  try {
    await updateWahlen(props.wahltagId, mergedWahlen);
  } catch {
    // Fehler wurde im Service bereits als Benachrichtigung gemeldet; Dialog
    // bleibt zum erneuten Speichern geöffnet.
    return;
  }
  wahlen.value = mergedWahlen;
  templateRefWahlBearbeitenDialog.value?.hideDialog();
}

function onDialogCancel() {
  templateRefWahlBearbeitenDialog.value?.hideDialog();
}

function toCssColor(farbe?: FarbeDTO): string {
  if (!farbe) {
    return "transparent";
  }
  return `rgb(${farbe.r}, ${farbe.g}, ${farbe.b})`;
}
</script>
<style scoped>
.wahl-farbe-swatch {
  display: inline-block;
  width: 24px;
  height: 24px;
  border-radius: 4px;
  border: 1px solid rgba(var(--v-theme-on-surface), 0.2);
  vertical-align: middle;
}
</style>
