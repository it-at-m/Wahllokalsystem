<template>
  <v-table
    v-if="stimmabgabevermerke != null"
    data-test="uwb-stimmabgabevermerke-eingenommene-wahlscheine-table"
  >
    <thead>
      <tr>
        <th
          v-for="wahldaten in stimmabgabevermerke.wahldaten"
          :key="wahldaten.wahlID"
          class="pl-0 font-weight-bold"
        >
          {{ getWahlNameOrBlankStringById(wahldaten.wahlID) }}
        </th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td
          v-for="wahldaten in stimmabgabevermerke.wahldaten"
          :key="wahldaten.wahlID"
          class="pl-0"
        >
          <v-number-input
            max-width="15rem"
            class="mt-5 pl-0"
            :model-value="
              getMapValue(
                EingenommenerWahlscheinStimmzettelartEnum.Klein,
                wahldaten
              )
            "
            @update:model-value="
              setMapValue(
                EingenommenerWahlscheinStimmzettelartEnum.Klein,
                wahldaten,
                $event
              )
            "
          />
        </td>
      </tr>
    </tbody>
  </v-table>
</template>
<script setup lang="ts">
import type { Wahldaten } from "@/types/stimmabgabevermerke/Wahldaten.ts";

import { storeToRefs } from "pinia";
import { VNumberInput, VTable } from "vuetify/components";

import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";

const { stimmabgabevermerke } = storeToRefs(useStimmabgabevermerkeStore());
const { getWahlNameOrBlankStringById } = useWahlenStore();

function getMapValue(
  key: EingenommenerWahlscheinStimmzettelartEnum,
  wahldaten: Wahldaten
) {
  return wahldaten.eingenommeneWahlscheine.get(key);
}

function setMapValue(
  key: EingenommenerWahlscheinStimmzettelartEnum,
  wahldaten: Wahldaten,
  value: number
) {
  wahldaten.eingenommeneWahlscheine.set(key, value);
}
</script>


