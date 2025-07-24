<template>
  <v-card>
    <v-card-title> Beschlussergebnis </v-card-title>
    <v-card-text class="pb-0">
      <v-table>
        <tbody>
          <tr>
            <td>
              <b>Zugelassen</b>
            </td>
            <td
              v-for="(value, index) in sumGueltig"
              :key="index"
            >
              {{ value }}
            </td>
          </tr>
          <tr>
            <td><b>Nicht zugelassen</b></td>
            <td
              v-for="(value, index) in sumUngueltig"
              :key="index"
            >
              {{ value }}
            </td>
          </tr>
          <tr>
            <td><b>Zurückweisungsgrund</b></td>
            <td />
            <td />
          </tr>
          <tr
            v-for="(beanstandung, index) in ungueltigeeinzelsummen"
            :key="index"
          >
            <td>
              {{ zurueckweisungsgrundEnumToDisplayString(beanstandung.grund) }}
            </td>
            <td
              v-for="(value, idx) in beanstandung.ungueltig"
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
import { onMounted, ref } from "vue";
import { VCard, VCardText, VCardTitle, VTable } from "vuetify/components";

import { useBeanstandeteWahlbriefeMapper } from "@/composables/briefwahl/beanstandeteWahlbriefeMapper.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";

const { wahlen } = storeToRefs(useWahlenStore());
const { zurueckweisungsgrundEnumToDisplayString } =
  useBeanstandeteWahlbriefeMapper();

const sumGueltig = ref<number[]>([]);
const sumUngueltig = ref<number[]>([]);
const ungueltigeeinzelsummen = ref<ZurueckweisungRow[]>([]);

onMounted(() => {
  calculateSums();
});

interface ZurueckweisungRow {
  ungueltig: number[];
  grund: ZurueckweisungsgrundEnum;
}

function calculateSums() {
  if (wahlen.value) {
    const anzahlWahlen = wahlen.value.length;
    sumGueltig.value = new Array(anzahlWahlen).fill(0);
    sumUngueltig.value = new Array(anzahlWahlen).fill(0);

    const tempUngueltigeEinzelstimmen: ZurueckweisungRow[] = [];
    const gruendeUngueltig = Object.values(ZurueckweisungsgrundEnum).filter(
      (grund) => grund !== ZurueckweisungsgrundEnum.Zugelassen
    );
    gruendeUngueltig.forEach((grund, index) => {
      tempUngueltigeEinzelstimmen[index] = {
        ungueltig: new Array(anzahlWahlen).fill(0),
        grund: grund,
      };
    });

    wahlen.value.forEach((wahl) => {
      wahl.beanstandeteWahlbriefe.forEach((beanstandeteWahlbrief) => {
        if (wahlen.value) {
          if (beanstandeteWahlbrief === ZurueckweisungsgrundEnum.Zugelassen) {
            sumGueltig.value[wahlen.value.indexOf(wahl)] += 1;
          } else {
            sumUngueltig.value[wahlen.value.indexOf(wahl)] += 1;
            tempUngueltigeEinzelstimmen[
              gruendeUngueltig.findIndex(
                (value) => value === beanstandeteWahlbrief
              )
            ].ungueltig[wahlen.value.indexOf(wahl)] += 1;
          }
        }
      });
    });

    ungueltigeeinzelsummen.value = tempUngueltigeEinzelstimmen;
  }
}
</script>
