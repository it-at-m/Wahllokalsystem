<template>
  <v-container>
    <v-table>
      <thead>
        <tr>
          <th
            v-for="key in Array.from(
              sumEingenommeneWahlscheineAndStimmabgabevermerkeForEachWahl.keys()
            )"
            :key="key"
          >
            <b>{{ wahlenActions.getWahlNameOrBlankStringById(key) }}</b>
          </th>
        </tr>
      </thead>
      <tbody>
        <tr>
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
