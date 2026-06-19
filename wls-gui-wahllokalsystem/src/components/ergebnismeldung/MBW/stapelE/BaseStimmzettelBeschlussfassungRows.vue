<template>
  <div>
    <div
      v-for="(bedenklicherStimmzettel, index) in bedenklicheStimmzettel"
      :key="index"
    >
      <base-stimmzettel-beschlussfassung-row
        :key="index + 'row'"
        :model-value="bedenklicherStimmzettel"
        :line-number="index + 1"
        @delete="
          (bedenklicherStimmzettelPayload) =>
            onDeleteIcon(index, bedenklicherStimmzettelPayload)
        "
      />
      <v-divider
        v-if="index < bedenklicheStimmzettel.length - 1"
        :key="'divider' + index"
        class="ma-3"
      />
    </div>

    <base-dialog
      :visible="isDeleteDialogVisible"
      dialogtitle="Bedenklichen Stimmzettel löschen"
      confirmtext="Ja"
      canceltext="Nein"
      icon="$information"
      @confirm="onConfirmDelete"
      @cancel="onCancelDelete"
    >
      <div class="mb-3">
        Sie wollen einen bedenklichen Stimmzettel löschen, für den Sie bereits
        Werte erfasst haben. Wenn Sie das Löschen der Zeile fortsetzen, werden
        folgende Werte gelöscht
      </div>
      Zu löschende Zeile: Stimmzettel Nummer {{ deleteDialogTextOrderIndex }}

      <div>
        <v-table striped="even">
          <tbody>
            <tr>
              <td class="context-category">Gültigkeit</td>
              <td class="text-left">{{ deleteDialogTextValidity }}</td>
            </tr>
            <tr>
              <td class="context-category">Zusätze</td>
              <td class="text-left">{{ deleteDialogTextSupplements }}</td>
            </tr>
          </tbody>
        </v-table>
      </div>
    </base-dialog>
  </div>
</template>

<script setup lang="ts">
import type { BedenklicherStimmzettel } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/BedenklicherStimmzettel.ts";

import { ref } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import BaseStimmzettelBeschlussfassungRow from "@/components/ergebnismeldung/MBW/stapelE/BaseStimmzettelBeschlussfassungRow.vue";
import { useBedenklicherStimmzettelMapper } from "@/composables/ergebnismeldung/MBW/bedenklicherStimmzettelMapper.ts";

const bedenklicheStimmzettel = defineModel<BedenklicherStimmzettel[]>(
  "bedenklicheStimmzettel",
  {
    required: true,
  }
);
const { validityEnumToDisplayString, supplementEnumToDisplayString } =
  useBedenklicherStimmzettelMapper();
const isDeleteDialogVisible = ref(false);
const deleteIndex = ref<number | null>(null);
const deleteDialogTextOrderIndex = ref(-1);
const deleteDialogTextValidity = ref("");
const deleteDialogTextSupplements = ref("");

function closeDeleteDialog() {
  isDeleteDialogVisible.value = false;
}

function showDeleteDialog(index: number) {
  deleteIndex.value = index;
  isDeleteDialogVisible.value = true;
}

function onDeleteIcon(
  index: number,
  bedenklicherStimmzettelPayload: BedenklicherStimmzettel
) {
  const { validity, supplements } = bedenklicherStimmzettelPayload;
  if (validity) {
    deleteDialogTextOrderIndex.value = index + 1;
    deleteDialogTextValidity.value = validityEnumToDisplayString(validity);

    if (supplements.length > 0) {
      deleteDialogTextSupplements.value = supplements
        .map((supplement) => supplementEnumToDisplayString(supplement))
        .join(", ");
    } else {
      deleteDialogTextSupplements.value = "-";
    }

    showDeleteDialog(index);
  } else {
    deleteIndex.value = index;
    onConfirmDelete();
  }
}

function onCancelDelete() {
  closeDeleteDialog();
}

function onConfirmDelete() {
  if (deleteIndex.value !== null) {
    bedenklicheStimmzettel.value.splice(deleteIndex.value, 1);
    deleteIndex.value = null;
  }
  closeDeleteDialog();
}
</script>
<style>
.context-category {
  text-align: left;
  width: 300px;
  font-weight: bold;
}
</style>
