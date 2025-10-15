<template>
  <v-container v-if="stimmabgabevermerke">
    <v-table class="stimmabgabevermerke-table">
      <thead>
        <tr>
          <th class="sav-first-column border-b-0" />
          <!-- @vue-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008 -->
          <th
            v-for="stimmabgabevermerk in stimmabgabevermerke"
            :key="stimmabgabevermerk.wahldaten[0].wahlID"
            class="pl-0 font-weight-bold dynamic-column"
          >
            <!-- @vue-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008 -->
            {{
              wahlenActions.getWahlNameOrBlankStringById(
                stimmabgabevermerk.wahldaten[0].wahlID
              )
            }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <!-- placeholder column for spacing -->
          <td />
          <!-- @vue-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008 -->
          <td
            v-for="stimmabgabevermerk in stimmabgabevermerke"
            :key="stimmabgabevermerk.wahldaten[0].wahlID"
            class="pl-0"
          >
            <!-- @vue-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008 -->
            <base-number-input
              max-width="15rem"
              :rules="[required, minNumber(0)]"
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
  </v-container>
</template>
<script setup lang="ts">
import type { Wahldaten } from "@/types/stimmabgabevermerke/Wahldaten.ts";

import { storeToRefs } from "pinia";

import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";

const { required, minNumber } = useRules();
const { stimmabgabevermerke } = storeToRefs(useStimmabgabevermerkeStore());
const { wahlenActions } = useWahlenStore();

function getMapValue(
  key: EingenommenerWahlscheinStimmzettelartEnum,
  wahldaten: Wahldaten
) {
  return wahldaten.eingenommeneWahlscheine.get(key);
}

function setMapValue(
  key: EingenommenerWahlscheinStimmzettelartEnum,
  wahldaten: Wahldaten,
  value: number | null | undefined
) {
  if (value !== null && value !== undefined) {
    wahldaten.eingenommeneWahlscheine.set(key, value);
  } else {
    wahldaten.eingenommeneWahlscheine.delete(key);
  }
}
</script>
