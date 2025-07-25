<template>
  <v-table>
    <thead>
      <tr>
        <th class="font-weight-bold text-center">Wahlschein</th>
        <th
          v-for="wahl in wahlen"
          :key="wahl.wahlID"
          class="font-weight-bold text-center"
        >
          Stimmzettelumschlag für {{ wahl.name }}
        </th>
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
              :rules="[REQUIRED]"
              :data-test="`wahlscheingruende-input-${index - 1}`"
              @update:model-value="
                (value) => onZulassungsgrundWahlscheinChanged(value, index - 1)
              "
            />
          </v-row>
        </td>
        <td
          v-for="wahl in wahlen"
          :key="`${wahl.wahlID}-${index - 1}`"
        >
          <v-autocomplete
            :model-value="
              zurueckweisungsgrundEnumToDisplayString(
                wahl.beanstandeteWahlbriefe[index - 1]
              )
            "
            label="Beschlussergebnis"
            :items="gruendeStimmzettel"
            hide-details
            auto-select-first
            :rules="[REQUIRED]"
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
</template>

<script setup lang="ts">
import type { Wahl } from "@/types/wahl/Wahl.ts";

import { storeToRefs } from "pinia";
import { computed, onMounted, ref } from "vue";
import { VAutocomplete, VBtn, VRow, VTable } from "vuetify/components";

import TheBeanstandeteWahlbriefeRowStatusIcon from "@/components/common/icons/TheBeanstandeteWahlbriefeRowStatusIcon.vue";
import { useBeanstandeteWahlbriefeMapper } from "@/composables/briefwahl/beanstandeteWahlbriefeMapper.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";
import { REQUIRED } from "@/util/rules.ts";

const { wahlen } = storeToRefs(useWahlenStore());
const {
  zurueckweisungsgrundStringToEnumValue,
  zurueckweisungsgrundEnumToDisplayString,
} = useBeanstandeteWahlbriefeMapper();

const maxRows = computed(() => {
  return wahlen.value
    ? Math.max(
        ...wahlen.value.map((wahl) => wahl.beanstandeteWahlbriefe.length)
      )
    : 0;
});

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
    if (wahlen.value) {
      const hasAnyWahlAnyWahlscheinGrund = wahlen.value.some((wahl) => {
        const grund = wahl.beanstandeteWahlbriefe[row];
        wahlscheinZurueckweisungsgrund = grund;
        return (
          grund &&
          gruendeWahlscheine.includes(
            zurueckweisungsgrundEnumToDisplayString(grund)
          ) &&
          grund !== ZurueckweisungsgrundEnum.Zugelassen
        );
      });

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

  if (selectedValue !== ZurueckweisungsgrundEnum.Zugelassen && wahlen.value) {
    wahlen.value.forEach(
      (wahl) => (wahl.beanstandeteWahlbriefe[rowIndex] = selectedValue)
    );
  } else if (wahlen.value) {
    // unset values of stimmzettelumschlag columns if "ZUGELASSEN" is selected
    wahlen.value.forEach(
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

  if (wahlen.value) {
    // add new value to other stimmzettelumschlag columns
    wahlen.value.forEach((otherWahl) => {
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
  if (wahlen.value) {
    wahlen.value.forEach((wahl) =>
      wahl.beanstandeteWahlbriefe.splice(rowIndex, 1)
    );
    wahlscheinGruende.value.splice(rowIndex, 1);
  }
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
</style>
