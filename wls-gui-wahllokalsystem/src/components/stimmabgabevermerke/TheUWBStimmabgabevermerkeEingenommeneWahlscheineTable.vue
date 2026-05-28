<template>
  <v-container v-if="stimmabgabevermerke">
    <v-table class="stimmabgabevermerke-table">
      <thead>
        <tr>
          <th class="sav-first-column border-b-0" />

          <th
            v-for="stimmabgabevermerk in stimmabgabevermerke"
            :key="stimmabgabevermerk.wahlID"
            class="pl-0 font-weight-bold dynamic-column"
          >
            {{
              wahlenActions.getWahlNameOrBlankStringById(
                stimmabgabevermerk.wahlID
              )
            }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <!-- placeholder column for spacing -->
          <td />

          <td
            v-for="stimmabgabevermerk in stimmabgabevermerke"
            :key="stimmabgabevermerk.wahlID"
            class="pl-0"
          >
            <base-number-input
              max-width="15rem"
              :rules="[required]"
              :model-value="
                getMapValue(
                  EingenommenerWahlscheinStimmzettelartEnum.Klein,
                  stimmabgabevermerk
                )
              "
              @update:model-value="
                setMapValue(
                  EingenommenerWahlscheinStimmzettelartEnum.Klein,
                  stimmabgabevermerk,
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
import type { Stimmabgabevermerke } from "@/types/stimmabgabevermerke/Stimmabgabevermerke.ts";

import { storeToRefs } from "pinia";

import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";

const { required } = useRules();
const { stimmabgabevermerke } = storeToRefs(useStimmabgabevermerkeStore());
const { wahlenActions } = useWahlenStore();

function getMapValue(
  key: EingenommenerWahlscheinStimmzettelartEnum,
  stimmabgabevermerke: Stimmabgabevermerke
) {
  return stimmabgabevermerke.eingenommeneWahlscheine.get(key);
}

function setMapValue(
  key: EingenommenerWahlscheinStimmzettelartEnum,
  stimmabgabevermerke: Stimmabgabevermerke,
  value: number | null | undefined
) {
  if (value !== null && value !== undefined) {
    stimmabgabevermerke.eingenommeneWahlscheine.set(key, value);
  } else {
    stimmabgabevermerke.eingenommeneWahlscheine.delete(key);
  }
}
</script>
