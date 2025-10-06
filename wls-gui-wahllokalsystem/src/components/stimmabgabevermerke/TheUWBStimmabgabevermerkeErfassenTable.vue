<template>
  <v-container v-if="stimmabgabevermerke">
    <div class="d-flex">
      <base-number-input
        v-model="rowSize"
        :rules="[required, minNumber(1), maxNumber(maxRowSize)]"
        max-width="15rem"
        label="Anzahl der Blätter"
      />
      <v-btn
        class="ml-4 mt-3"
        active
        :disabled="disableChangeRowSizeButton"
        @click="changeRowCountOrOpenDialog"
      >
        Übernehmen
      </v-btn>
    </div>
    <v-divider
      :thickness="2"
      class="border-opacity-25"
    />
    <v-table class="stimmabgabevermerke-table">
      <thead>
        <tr>
          <th class="sav-first-column border-b-0" />
          <th
            v-for="stimmabgabevermerk in stimmabgabevermerke"
            :key="stimmabgabevermerk.wahldaten[0].wahlID"
            class="font-weight-bold dynamic-column border-b-0"
          >
            {{
              wahlenActions.getWahlNameOrBlankStringById(
                stimmabgabevermerk.wahldaten[0].wahlID
              )
            }}
            <div>Personen mit Stimmabgabevermerk</div>
          </th>
        </tr>
        <tr class="font-weight-bold">
          <th class="border-b-0">Blatt</th>
          <th
            v-for="stimmabgabevermerk in stimmabgabevermerke"
            :key="stimmabgabevermerk.wahldaten[0].wahlID"
            class="border-b-0"
          >
            Anzahl Stimmabgabevermerke
          </th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>Nr. 1</td>
          <td
            v-for="stimmabgabevermerk in stimmabgabevermerke"
            :key="stimmabgabevermerk.wahldaten[0].wahlID"
          >
            <v-text-field
              disabled
              label="Beurkundung"
            />
          </td>
        </tr>
        <tr
          v-for="number in lowestNumberOfRowsOverAllWahldaten"
          :key="number"
        >
          <td>Nr. {{ number + 1 }}</td>
          <td
            v-for="stimmabgabevermerk in stimmabgabevermerke"
            :key="stimmabgabevermerk.wahldaten[0].wahlID"
          >
            <template
              v-for="stimmzettel in stimmabgabevermerk.wahldaten[0].vermerke[
                number - 1
              ].stimmzettel"
            >
              <base-number-input
                v-if="
                  stimmzettel != null &&
                  stimmzettel.stimmzettelart ==
                    StimmzettelStimmzettelartEnum.Klein
                "
                :key="stimmzettel.stimmzettelart"
                v-model="stimmzettel.anzahl"
                max-width="15rem"
                :rules="[required, minNumber(0), maxNumber(50)]"
              />
            </template>
          </td>
        </tr>
      </tbody>
      <tfoot>
        <tr class="font-weight-bold">
          <td>Gesamt</td>
          <td
            v-for="(
              totalCount, index
            ) in stimmabgabevermerkeTableTotalEachWahldaten"
            :key="`column-${index}-` + totalCount"
          >
            {{ totalCount }}
          </td>
        </tr>
      </tfoot>
    </v-table>
    <base-dialog
      :visible="isDeleteDialogVisible"
      dialogtitle="Reduzierung der Blätteranzahl des Wählerverzeichnisses"
      confirmtext="Trotzdem Löschen"
      canceltext="Abbrechen"
      icon="$information"
      @cancel="isDeleteDialogVisible = false"
      @confirm="onDialogConfirmDeletingRows"
      ><div>
        Sie wollen Blätter löschen, für die Sie Stimmabgabevermerke eingetragen
        haben. Wenn Sie diese löschen, werden dadurch auch die Werte für die
        Stimmabgabevermerke gelöscht.
      </div></base-dialog
    >
  </v-container>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, onMounted, ref } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";

const { minNumber, maxNumber, required } = useRules();

const {
  stimmabgabevermerke,
  stimmabgabevermerkeTableTotalEachWahldaten,
  lowestNumberOfRowsOverAllWahldaten,
} = storeToRefs(useStimmabgabevermerkeStore());
const { isAnyRowThatShouldBeDeletedFilled, changeRowCount } =
  useStimmabgabevermerkeStore();
const { wahlenActions } = useWahlenStore();

onMounted(() => {
  rowSize.value = lowestNumberOfRowsOverAllWahldaten.value + 1;
});

const isDeleteDialogVisible = ref(false);
const rowSize = ref<number | null>(null);
const maxRowSize = 999;

const disableChangeRowSizeButton = computed(() => {
  return (
    rowSize.value == null ||
    rowSize.value <= 0 ||
    rowSize.value > maxRowSize ||
    rowSize.value == lowestNumberOfRowsOverAllWahldaten.value + 1
  );
});

function changeRowCountOrOpenDialog() {
  if (
    lowestNumberOfRowsOverAllWahldaten.value != null &&
    rowSize.value != null
  ) {
    if (
      rowSize.value - 1 < lowestNumberOfRowsOverAllWahldaten.value &&
      isAnyRowThatShouldBeDeletedFilled(rowSize.value)
    ) {
      isDeleteDialogVisible.value = true;
    } else {
      changeRowCount(rowSize.value);
    }
  }
}

function onDialogConfirmDeletingRows() {
  if (rowSize.value != null) {
    changeRowCount(rowSize.value);
    isDeleteDialogVisible.value = false;
  }
}
</script>
