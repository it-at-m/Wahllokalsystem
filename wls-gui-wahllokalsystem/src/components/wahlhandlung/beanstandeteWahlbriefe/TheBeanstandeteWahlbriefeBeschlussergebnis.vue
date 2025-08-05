<template>
  <v-card>
    <v-card-text class="pb-0">
      <v-table>
        <tbody>
          <tr>
            <td class="font-weight-bold">Zugelassen</td>
            <td
              v-for="(value, index) in summeGueltigerWahlbriefe"
              :key="index"
              class="font-weight-bold"
            >
              {{ value }}
            </td>
          </tr>
          <tr>
            <td class="font-weight-bold">Nicht zugelassen</td>
            <td
              v-for="(value, index) in summeUngueltigerWahlbriefe"
              :key="index"
              class="font-weight-bold"
            >
              {{ value }}
            </td>
          </tr>
          <tr class="bg-grey-lighten-3">
            <td class="font-weight-bold">Zurückweisungsgrund</td>
            <td
              v-for="(_, index) in summeGueltigerWahlbriefe"
              :key="index"
            />
          </tr>
          <tr
            v-for="(beanstandung, index) in summenZurueckweisungsgruende"
            :key="index"
          >
            <td style="text-indent: 20px">
              {{ zurueckweisungsgrundEnumToDisplayString(beanstandung.grund) }}
            </td>
            <td
              v-for="(value, idx) in beanstandung.summen"
              :key="idx"
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
import { VCard, VCardText, VTable } from "vuetify/components";

import { useBeanstandeteWahlbriefeMapper } from "@/composables/briefwahl/beanstandeteWahlbriefeMapper.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const {
  summeGueltigerWahlbriefe,
  summeUngueltigerWahlbriefe,
  summenZurueckweisungsgruende,
} = storeToRefs(useWahlenStore());
const { zurueckweisungsgrundEnumToDisplayString } =
  useBeanstandeteWahlbriefeMapper();
</script>
