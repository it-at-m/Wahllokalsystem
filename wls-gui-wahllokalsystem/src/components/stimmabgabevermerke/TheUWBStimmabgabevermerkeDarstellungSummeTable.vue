<template>
  <v-container>
    <v-table class="stimmabgabevermerke-table">
      <thead>
        <tr>
          <th class="sav-first-column border-b-0" />
          <th
            v-for="key in Array.from(
              sumEingenommeneWahlscheineAndStimmabgabevermerkeForEachWahl.keys()
            )"
            :key="key"
            class="pl-0 font-weight-bold dynamic-column"
          >
            {{ wahlenActions.getWahlNameOrBlankStringById(key) }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <!-- placeholder column for spacing -->
          <td />
          <td
            v-for="(value, index) in Array.from(
              sumEingenommeneWahlscheineAndStimmabgabevermerkeForEachWahl.values()
            )"
            :key="`Row-${index}-` + value"
          >
            {{ value }}
          </td>
        </tr>
      </tbody>
    </v-table>
  </v-container>
</template>
<script setup lang="ts">
import { storeToRefs } from "pinia";

import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { sumEingenommeneWahlscheineAndStimmabgabevermerkeForEachWahl } =
  storeToRefs(useStimmabgabevermerkeStore());
const { wahlenActions } = useWahlenStore();
</script>
