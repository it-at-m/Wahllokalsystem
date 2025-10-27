<template>
  <v-container>
    <v-table>
      <thead>
        <tr>
          <th class="font-weight-bold text-center">Wahlschein</th>
          <th
            v-for="wahl in wahlenState.wahlen"
            :key="wahl.wahlID"
            class="font-weight-bold text-center"
          >
            Stimmzettelumschlag für {{ wahl.name }}
          </th>
          <th />
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="index in maxRows"
          :key="index"
        >
          <td>
            <v-row
              align="center"
              class="my-2"
              style="min-width: 350px"
            >
              {{ index }}
              <v-autocomplete
                :model-value="
                  zurueckweisungsgrundEnumToDisplayString(
                    wahlscheinGruende[index - 1]
                  )
                "
                label="Beschlussergebnis"
                class="ml-5"
                :items="gruendeWahlscheine"
                hide-details
                auto-select-first
                :rules="[required]"
                :data-test="`wahlscheingruende-input-${index - 1}`"
                @update:model-value="
                  (value) =>
                    onZulassungsgrundWahlscheinChanged(value, index - 1)
                "
              />
            </v-row>
          </td>
          <td
            v-for="wahl in wahlenState.wahlen"
            :key="`${wahl.wahlID}-${index - 1}`"
          >
            <v-autocomplete
              :model-value="
                zurueckweisungsgrundEnumToDisplayString(
                  wahl.beanstandeteWahlbriefe[index - 1] ?? null
                )
              "
              label="Beschlussergebnis"
              :items="gruendeStimmzettel"
              hide-details
              auto-select-first
              :rules="[required]"
              :disabled="_isInputDisabled(index - 1)"
              :data-test="`stimmzettelgruende-input-${wahl.wahlID}-${index - 1}`"
              @update:model-value="
                (value) =>
                  onZulassungsgrundStimmzettelChanged(value, index - 1, wahl)
              "
            />
          </td>
          <td>
            <v-row
              align="center"
              justify="space-between"
              class="px-2"
              style="min-width: 115px"
            >
              <v-btn
                icon="$delete"
                variant="text"
                :data-test="`delete-btn-${index - 1}`"
                @click="onDeleteBeanstandeteWahlbriefeRowClicked(index - 1)"
              />
              <the-beanstandete-wahlbriefe-row-status-icon :index="index - 1" />
            </v-row>
          </td>
        </tr>
      </tbody>
    </v-table>
    <base-dialog
      :visible="rowIndexToDelete !== null"
      dialogtitle="Löschen eines Beschlusses"
      confirmtext="Trotzdem Löschen"
      canceltext="Abbrechen"
      icon="$information"
      @cancel="onCancelDialog"
      @confirm="onDialogConfirmDeletingRows"
      ><div>
        <div class="mb-4">
          Sie wollen einen Beschluss löschen, für den Sie bereits Werte erfasst
          haben. Wenn Sie das Löschen der Zeile fortsetzen, werden folgende
          Werte gelöscht:
        </div>
        <div>
          Zu löschende Zeile: Wahlschein Nummer {{ rowIndexToDelete + 1 }}
        </div>
        <div>
          <v-table striped="even">
            <tbody>
              <tr
                v-for="(context, index) in contextForDeletion"
                :key="index"
              >
                <td class="context-category">
                  {{ context.category }}
                </td>
                <td class="text-left">{{ context.beschluss }}</td>
              </tr>
            </tbody>
          </v-table>
        </div>
      </div></base-dialog
    >
  </v-container>
</template>

<script setup lang="ts">
import type { Wahl } from "@/types/wahl/Wahl.ts";

import { storeToRefs } from "pinia";
import { computed, onMounted, ref } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import TheBeanstandeteWahlbriefeRowStatusIcon from "@/components/wahlhandlung/beanstandeteWahlbriefe/TheBeanstandeteWahlbriefeRowStatusIcon.vue";
import { useBeanstandeteWahlbriefeMapper } from "@/composables/briefwahl/beanstandeteWahlbriefeMapper.ts";
import { useRules } from "@/composables/common/rules.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";

const { required } = useRules();

const { wahlenState } = storeToRefs(useWahlenStore());
const { beanstandeteWahlbriefeActions } = useWahlenStore();
const {
  zurueckweisungsgrundStringToEnumValue,
  zurueckweisungsgrundEnumToDisplayString,
} = useBeanstandeteWahlbriefeMapper();

const maxRows = computed(() => {
  return wahlenState.value.wahlen
    ? Math.max(
        ...wahlenState.value.wahlen.map(
          (wahl) => wahl.beanstandeteWahlbriefe.length
        )
      )
    : 0;
});

const contextForDeletion = computed(() => {
  const contextLines: { category: string; beschluss: string }[] = [];
  if (rowIndexToDelete.value !== null) {
    contextLines.push({
      category: "Wahlschein",
      beschluss: zurueckweisungsgrundEnumToDisplayString(
        wahlscheinGruende.value[rowIndexToDelete.value]
      ),
    });
    wahlenState.value.wahlen?.map((wahl) => {
      if (rowIndexToDelete.value !== null) {
        const beanstandeterWahlbrief =
          beanstandeteWahlbriefeActions.getBeanstandeterWahlbriefEntryByWahl(
            rowIndexToDelete.value,
            wahl.wahlID
          );
        if (beanstandeterWahlbrief != null) {
          contextLines.push({
            category: `Stimmzettelumschlag für ${wahl.name}`,
            beschluss: zurueckweisungsgrundEnumToDisplayString(
              beanstandeterWahlbrief
            ),
          });
        }
      }
    });
  }
  return contextLines;
});

const rowIndexToDelete = ref<number | null>(null);
const wahlscheinGruende = ref(Array(maxRows.value).fill(""));

const gruendeWahlscheine = [
  "Zugelassen",
  "Wahlschein ungültig laut Liste",
  "Kein Original-Wahlschein",
  "Unterschrift auf Wahlschein fehlt",
];
const gruendeStimmzettel = [
  "Zugelassen",
  "Stimmzettelumschlag fehlt",
  "Lose Stimmzettel",
  "Wahlbrief und Stimmzettelumschlag offen",
  "Wahlscheine ungleich Stimmzettelumschläge",
  "Nicht-amtlicher Stimmzettelumschlag",
  "Stimmzettelumschlag gefährdet Wahlgeheimnis",
  "Gegenstand im Stimmzettelumschlag",
  "Für diese Wahl nicht wahlberechtigt",
];

onMounted(() => {
  for (const row of Array.from({ length: maxRows.value }, (_, i) => i)) {
    let wahlscheinZurueckweisungsgrund;
    if (wahlenState.value.wahlen) {
      const hasAnyWahlAnyWahlscheinGrund = wahlenState.value.wahlen.some(
        (wahl) => {
          const grund = wahl.beanstandeteWahlbriefe[row];
          wahlscheinZurueckweisungsgrund = grund;
          return (
            grund &&
            gruendeWahlscheine.includes(
              zurueckweisungsgrundEnumToDisplayString(grund)
            ) &&
            grund !== ZurueckweisungsgrundEnum.Zugelassen
          );
        }
      );

      if (hasAnyWahlAnyWahlscheinGrund) {
        wahlscheinGruende.value[row] = wahlscheinZurueckweisungsgrund;
      } else {
        wahlscheinGruende.value[row] = ZurueckweisungsgrundEnum.Zugelassen;
      }
    }
  }
});

function onZulassungsgrundWahlscheinChanged(
  newValue: string,
  rowIndex: number
) {
  const selectedValue = zurueckweisungsgrundStringToEnumValue(newValue);
  wahlscheinGruende.value[rowIndex] = selectedValue;

  if (
    selectedValue !== ZurueckweisungsgrundEnum.Zugelassen &&
    wahlenState.value.wahlen
  ) {
    wahlenState.value.wahlen.forEach(
      (wahl) => (wahl.beanstandeteWahlbriefe[rowIndex] = selectedValue)
    );
  } else if (wahlenState.value.wahlen) {
    // unset values of stimmzettelumschlag columns if "ZUGELASSEN" is selected
    wahlenState.value.wahlen.forEach(
      (wahl) => (wahl.beanstandeteWahlbriefe[rowIndex] = null)
    );
  }
}

function onZulassungsgrundStimmzettelChanged(
  newValue: string,
  rowIndex: number,
  wahl: Wahl
) {
  const selectedValue = zurueckweisungsgrundStringToEnumValue(newValue);
  wahl.beanstandeteWahlbriefe[rowIndex] = selectedValue;

  if (wahlenState.value.wahlen) {
    // add new value to other stimmzettelumschlag columns
    wahlenState.value.wahlen.forEach((otherWahl) => {
      // avoid updating the same wahl again
      if (otherWahl.wahlID !== wahl.wahlID) {
        if (
          selectedValue !== ZurueckweisungsgrundEnum.NichtWahlberechtigt &&
          otherWahl.beanstandeteWahlbriefe[rowIndex] !==
            ZurueckweisungsgrundEnum.NichtWahlberechtigt
        ) {
          otherWahl.beanstandeteWahlbriefe[rowIndex] = selectedValue;
        }
      }
    });
  }
}

function onDeleteBeanstandeteWahlbriefeRowClicked(rowIndex: number) {
  if (
    !beanstandeteWahlbriefeActions.isBeanstandeterWahlbriefEntryEmpty(
      rowIndex
    ) ||
    wahlscheinGruende.value[rowIndex] !== undefined
  ) {
    rowIndexToDelete.value = rowIndex;
  } else {
    _deleteBeanstandeterWahlbrief(rowIndex);
  }
}

function onDialogConfirmDeletingRows() {
  if (rowIndexToDelete.value !== null) {
    _deleteBeanstandeterWahlbrief(rowIndexToDelete.value);
    rowIndexToDelete.value = null;
  }
}

function onCancelDialog() {
  rowIndexToDelete.value = null;
}

function _deleteBeanstandeterWahlbrief(rowIndex: number) {
  beanstandeteWahlbriefeActions.deleteBeanstandeterWahlbriefEntry(rowIndex);
  wahlscheinGruende.value.splice(rowIndex, 1);
}

function _isInputDisabled(rowIndex: number) {
  const grund = wahlscheinGruende.value[rowIndex];
  return grund == undefined || grund !== ZurueckweisungsgrundEnum.Zugelassen;
}
</script>

<style scoped>
td {
  text-align: center;
}

.context-category {
  text-align: left;
  width: 300px;
  font-weight: bold;
}
</style>
