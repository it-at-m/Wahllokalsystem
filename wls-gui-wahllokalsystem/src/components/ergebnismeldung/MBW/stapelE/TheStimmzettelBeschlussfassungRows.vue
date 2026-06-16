<template>
  <div>
    <base-stimmzettel-beschlussfassung-row
      v-for="(bedenklicherStimmzettel, index) in bedenklicheStimmzettel"
      :key="index"
      :model-value="bedenklicherStimmzettel"
      :line-number="index + 1"
      @delete="
        (bedenklicherStimmzettelPayload) =>
          onDeleteIcon(index, bedenklicherStimmzettelPayload)
      "
    />
    <base-dialog
      :visible="deleteDialog"
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
      Zu löschende Zeile: Stimmzettel Nummer {{ dialogOrderIndex }}

      <div>
        <v-table striped="even">
          <tbody>
            <tr>
              <td class="context-category">Gültigkeit</td>
              <td class="text-left">{{ dialogValidity }}</td>
            </tr>
            <tr>
              <td class="context-category">Zusätze</td>
              <td class="text-left">{{ dialogSupplements }}</td>
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
const deleteDialog = ref(false);
const deleteIndex = ref<number | null>(null);
const dialogOrderIndex = ref(-1);
const dialogValidity = ref("");
const dialogSupplements = ref("");

function closeDeleteDialog() {
  deleteDialog.value = false;
}

function showDeleteDialog(index: number) {
  deleteIndex.value = index;
  deleteDialog.value = true;
}

function onDeleteIcon(
  index: number,
  bedenklicherStimmzettelPayload: BedenklicherStimmzettel
) {
  const { validity, supplements } = bedenklicherStimmzettelPayload;
  if (validity) {
    dialogOrderIndex.value = index + 1;
    if (validity) {
      dialogValidity.value = validityEnumToDisplayString(validity) ?? "-";
    } else {
      dialogValidity.value = "-";
    }

    if (supplements.length > 0) {
      dialogSupplements.value =
        supplements
          .map((supplement) => supplementEnumToDisplayString(supplement))
          .join(", ") ?? "-";
    } else {
      dialogSupplements.value = "-";
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
