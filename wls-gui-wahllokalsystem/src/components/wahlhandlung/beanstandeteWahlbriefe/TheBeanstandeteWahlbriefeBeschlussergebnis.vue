<template>
  <v-card>
    <v-card-text class="pb-0">
      <v-table>
        <thead>
          <tr>
            <th />
            <th
              v-for="wahl in wahlenState.wahlen"
              :key="wahl.wahlID"
              class="font-weight-bold text-center"
            >
              {{ wahl.name }}
            </th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td class="font-weight-bold">Zugelassen</td>
            <td
              v-for="(
                value, index
              ) in beanstandeteWahlbriefeGetter.summeGueltigerWahlbriefe"
              :key="index"
              class="font-weight-bold text-center"
            >
              {{ value }}
            </td>
          </tr>
          <tr>
            <td class="font-weight-bold">Nicht zugelassen</td>
            <td
              v-for="(
                value, index
              ) in beanstandeteWahlbriefeGetter.summeUngueltigerWahlbriefe"
              :key="index"
              class="font-weight-bold text-center"
            >
              {{ value }}
            </td>
          </tr>
          <tr class="bg-grey-lighten-3">
            <td class="font-weight-bold">Zurückweisungsgrund</td>
            <td
              v-for="(
                _, index
              ) in beanstandeteWahlbriefeGetter.summeGueltigerWahlbriefe"
              :key="index"
            />
          </tr>
          <tr
            v-for="(
              beanstandung, index
            ) in beanstandeteWahlbriefeGetter.summenZurueckweisungsgruende"
            :key="index"
          >
            <td style="padding-left: 30px">
              {{ zurueckweisungsgrundEnumToDisplayString(beanstandung.grund) }}
            </td>
            <td
              v-for="(value, idx) in beanstandung.summen"
              :key="idx"
              class="text-center"
            >
              {{ value }}
            </td>
          </tr>
        </tbody>
      </v-table>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import { useBeanstandeteWahlbriefeMapper } from "@/composables/briefwahl/beanstandeteWahlbriefeMapper.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { wahlenState, beanstandeteWahlbriefeGetter } =
  storeToRefs(useWahlenStore());
const { zurueckweisungsgrundEnumToDisplayString } =
  useBeanstandeteWahlbriefeMapper();
</script>
