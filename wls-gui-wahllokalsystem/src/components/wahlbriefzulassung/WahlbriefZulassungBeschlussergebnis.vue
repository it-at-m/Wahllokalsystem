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
          </tr>
          <tr
            v-for="(beanstandung, index) in ungueltigeeinzelsummen"
            :key="index"
          >
            <td>{{ zuordnungGruende[beanstandung.grund] }}</td>
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
import type { BeanstandeteWahlbriefeDTO } from "@/api/wls-clients/generated-briefwahl-api";

import { onMounted, ref } from "vue";
import { VCard, VCardText, VCardTitle, VTable } from "vuetify/components";

import { BeanstandeteWahlbriefeDTOBeanstandeteWahlbriefeEnum } from "@/api/wls-clients/generated-briefwahl-api";

const test = {
  wahlbezirkID:
    "1eineGehashteIdDieImKLartextAndersAussiehtAlsWahlbezirk.wahlnummerbw1",
  waehlerverzeichnisNummer: 1,
  beanstandeteWahlbriefe: {
    "OBW-WAHL-ID": [
      "KEIN_ORIGINAL_SCHEIN",
      "UMSCHLAG_NICHT_AMTLICH",
      "LOSE_STIMMZETTEL",
      "SCHEINE_UNGLEICH_UMSCHLAEGE",
      "NICHT_WAHLBERECHTIGT",
      "ZUGELASSEN",
      "NICHT_WAHLBERECHTIGT",
    ],
    "SRW-WAHL-ID": [
      "KEIN_ORIGINAL_SCHEIN",
      "UMSCHLAG_NICHT_AMTLICH",
      "LOSE_STIMMZETTEL",
      "NICHT_WAHLBERECHTIGT",
      "LOSE_STIMMZETTEL",
      "NICHT_WAHLBERECHTIGT",
      "ZUGELASSEN",
    ],
    "BAW-WAHL-ID": [
      "KEIN_ORIGINAL_SCHEIN",
      "UMSCHLAG_NICHT_AMTLICH",
      "LOSE_STIMMZETTEL",
      "SCHEINE_UNGLEICH_UMSCHLAEGE",
      "LOSE_STIMMZETTEL",
      "ZUGELASSEN",
      "ZUGELASSEN",
    ],
  },
} as BeanstandeteWahlbriefeDTO;

const zuordnungGruende: Record<string, string> = {
  [BeanstandeteWahlbriefeDTOBeanstandeteWahlbriefeEnum.Zugelassen]:
    "Zugelassen",
  [BeanstandeteWahlbriefeDTOBeanstandeteWahlbriefeEnum.ScheinUngueltig]:
    "Wahlschein ungültig laut Liste",
  [BeanstandeteWahlbriefeDTOBeanstandeteWahlbriefeEnum.KeinOriginalSchein]:
    "Kein Original-Wahlschein",
  [BeanstandeteWahlbriefeDTOBeanstandeteWahlbriefeEnum.UnterschriftFehlt]:
    "Unterschrift auf Wahlschein fehlt",
  [BeanstandeteWahlbriefeDTOBeanstandeteWahlbriefeEnum.UmschlagFehlt]:
    "Stimmzettelumschlag fehlt",
  [BeanstandeteWahlbriefeDTOBeanstandeteWahlbriefeEnum.LoseStimmzettel]:
    "Lose Stimmzettel",
  [BeanstandeteWahlbriefeDTOBeanstandeteWahlbriefeEnum.WahlbriefUndUmschlagOffen]:
    "Wahlbrief und Stimmzettelumschlag offen",
  [BeanstandeteWahlbriefeDTOBeanstandeteWahlbriefeEnum.ScheineUngleichUmschlaege]:
    "Wahlscheine ungleich Stimmzettelumschläge",
  [BeanstandeteWahlbriefeDTOBeanstandeteWahlbriefeEnum.UmschlagNichtAmtlich]:
    "Nicht-amtlicher Stimmzettelumschlag",
  [BeanstandeteWahlbriefeDTOBeanstandeteWahlbriefeEnum.UmschlagGefaehrdetWahlgeheimnis]:
    "Stimmzettelumschlag gefährdet Wahlgeheimnis",
  [BeanstandeteWahlbriefeDTOBeanstandeteWahlbriefeEnum.GegenstandImUmschlag]:
    "Gegenstand im Stimmzettelumschlag",
  [BeanstandeteWahlbriefeDTOBeanstandeteWahlbriefeEnum.NichtWahlberechtigt]:
    "Für diese Wahl nicht wahlberechtigt",
};

const sumGueltig = ref<number[]>([]);
const sumUngueltig = ref<number[]>([]);
const ungueltigeeinzelsummen = ref<ZurueckweisungRow[]>([]);

onMounted(() => {
  calculateSums();
});

interface ZurueckweisungRow {
  ungueltig: number[];
  grund: string;
}

function calculateSums() {
  const wahlen = Object.keys(test.beanstandeteWahlbriefe);
  const anzahlWahlen = wahlen.length;
  sumGueltig.value = new Array(anzahlWahlen).fill(0);
  sumUngueltig.value = new Array(anzahlWahlen).fill(0);

  const tempUngueltigeEinzelstimmen: ZurueckweisungRow[] = [];
  const gruendeUngueltig = Object.values(
    BeanstandeteWahlbriefeDTOBeanstandeteWahlbriefeEnum
  ).filter(
    (grund) =>
      grund !== BeanstandeteWahlbriefeDTOBeanstandeteWahlbriefeEnum.Zugelassen
  );
  gruendeUngueltig.forEach((grund, index) => {
    tempUngueltigeEinzelstimmen[index] = {
      ungueltig: new Array(anzahlWahlen).fill(0),
      grund: grund,
    };
  });

  Object.entries(test.beanstandeteWahlbriefe).forEach(
    ([wahl, beanstandungen]) => {
      for (const beanstandung of beanstandungen) {
        if (
          beanstandung ===
          BeanstandeteWahlbriefeDTOBeanstandeteWahlbriefeEnum.Zugelassen
        ) {
          sumGueltig.value[wahlen.indexOf(wahl)] += 1;
        } else {
          sumUngueltig.value[wahlen.indexOf(wahl)] += 1;
          tempUngueltigeEinzelstimmen[
            gruendeUngueltig.findIndex((value) => value === beanstandung)
          ].ungueltig[wahlen.indexOf(wahl)] += 1;
        }
      }
    }
  );

  ungueltigeeinzelsummen.value = tempUngueltigeEinzelstimmen;
}
</script>
