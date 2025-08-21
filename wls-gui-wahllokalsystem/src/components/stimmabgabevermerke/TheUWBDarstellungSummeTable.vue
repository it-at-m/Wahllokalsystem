<template>
  <v-container>
    <v-table>
      <thead>
        <tr>
          <td
            v-for="key in Array.from(
              sumEingenommeneWahlscheineAndStimmabgabevermerkeForEachWahl.keys()
            )"
            :key="key"
          >
            <b>{{ getWahlNameOrBlankStringById(key) }}</b>
          </td>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td
            v-for="value in Array.from(
              sumEingenommeneWahlscheineAndStimmabgabevermerkeForEachWahl.values()
            )"
            :key="value"
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
const { getWahlNameOrBlankStringById } = useWahlenStore();
</script>
