<template>
  <v-table
    v-if="stimmabgabevermerke != null"
    data-test="uwb-stimmabgabevermerke-eingenommene-wahlscheine-table"
  >
    <thead>
      <tr>
        <th
          v-for="stimmabgabevermerk in stimmabgabevermerke"
          :key="stimmabgabevermerk.waehlerverzeichnisNummer"
          class="pl-0 font-weight-bold"
        >
          {{
            getWahlNameOrBlankStringById(stimmabgabevermerk.wahldaten[0].wahlID)
          }}
        </th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td
          v-for="stimmabgabevermerk in stimmabgabevermerke"
          :key="stimmabgabevermerk.wahldaten[0].wahlID"
          class="pl-0"
        >
          <v-number-input
            max-width="15rem"
            class="mt-5 pl-0"
            :model-value="
              getMapValue(
                EingenommenerWahlscheinStimmzettelartEnum.Klein,
                stimmabgabevermerk.wahldaten[0]
              )
            "
            @update:model-value="
              setMapValue(
                EingenommenerWahlscheinStimmzettelartEnum.Klein,
                stimmabgabevermerk.wahldaten[0],
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
