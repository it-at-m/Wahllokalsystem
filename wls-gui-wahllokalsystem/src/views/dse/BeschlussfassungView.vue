<template>
  <v-card>
    <v-card-title>Beschlussfassung</v-card-title>
    <v-card-text>
      <base-progress-linear
        titel="bereits gefasste Beschlüsse"
        :is-loading="false"
        :current="23"
        :total="66"
        color="success"
      />
      <v-data-table
        :headers="headers"
        :items="items"
      >
        <template #item.bezirk="{ value }">
          <v-icon
            :icon="value == 'UWB' ? '$wahlbezirksartUWB' : '$wahlbezirksartBWB'"
          />
        </template>

        <template #item.zeitpunkt="{ value }"> {{ value }} Uhr </template>

        <template #item.beschluss="{ value }">
          <v-icon
            :icon="value ? '$success' : ''"
            :color="value ? 'success' : ''"
          />
        </template>

        <template #item.actions="{ item }">
          <div class="d-flex ga-2">
            <v-btn
              icon="$edit"
              size="x-small"
            />
          </div>
        </template>
      </v-data-table>
    </v-card-text>
    <v-card-actions>
      <base-text-button active>Nächsten Beschluss fassen</base-text-button>
      <base-text-button>Beschlussfassung unterbrechen</base-text-button>
      <v-spacer />
      <base-text-button :disabled="true">
        Beschlussfassung Beenden
      </base-text-button>
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { onActivated, onMounted, ref } from "vue";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseProgressLinear from "@/components/common/progressLinear/BaseProgressLinear.vue";

const {
  generateRandomNumber,
  getRandomItem,
  generateRandomBoolean,
  generateRandomNumberInRange,
} = useCommonTestDataFactory();

onActivated(() => {
  generateTableData(66);
});

const headers = [
  { title: "Kennung", key: "kennung" },
  { title: "Bezirk", key: "bezirk" },
  { title: "Team", key: "team" },
  { title: "Erfassungszeitpunkt", key: "zeitpunkt" },
  { title: "Beschlussgrund", key: "grund" },
  { title: "Beschluss gefasst", key: "beschluss" },
  { title: "Actions", key: "actions", sortable: false },
];

class BeschlussTabelleItem {
  id: number;
  kennung;
  bezirk;
  team;
  zeitpunkt;
  grund: string;
  beschluss;

  constructor(
    kennung: number,
    bezirk: string,
    team: string,
    zeitpunkt: string,
    grund: string,
    beschluss: boolean
  ) {
    this.id = 0;
    this.kennung = kennung;
    this.bezirk = bezirk;
    this.team = team;
    this.zeitpunkt = zeitpunkt;
    this.grund = grund;
    this.beschluss = beschluss;
  }
}

const items = ref<BeschlussTabelleItem[]>([]);

function generateTableData(amount: number) {
  for (let i = 0; i < amount; i++) {
    items.value.push({
      id: generateRandomNumber(5),
      kennung: generateRandomNumber(2),
      bezirk: getRandomItem(["UWB", "BWB"]),
      team: getRandomItem(["A", "B", "C"]),
      zeitpunkt:
        generateRandomNumberInRange(0, 23) +
        ":" +
        generateRandomNumberInRange(10, 59),
      grund: getRandomItem([
        "unzulässiger Zusatz/Vorbehalt",
        "Stimmzettel vollständig durchgestrichen",
        "Wählerwille nicht zweifelsfrei erkennbar",
        "handschriftlich ergänzte Person",
        "Kennzeichnung nicht eindeutig zuzuordnen",
      ]),
      beschluss: generateRandomBoolean(),
    });
  }
}
</script>
