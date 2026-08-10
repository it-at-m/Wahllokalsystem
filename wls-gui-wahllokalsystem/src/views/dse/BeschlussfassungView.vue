<template>
  <div>
    <v-card>
      <v-card-title>Beschlussfassung</v-card-title>
      <v-card-text>
        <base-progress-linear
          class="text-center"
          titel="bereits gefasste Beschlüsse"
          :is-loading="false"
          :current="23"
          :total="66"
          color="success"
        />
        <v-data-table
          class="hideSortBadges"
          :headers="headers"
          :items="items"
          no-data-text="Keine Stimmzettel für die Beschlussfassung vorgemerkt"
          :sort-by="[
            { key: 'team', order: 'asc' },
            { key: 'beschluss', order: 'asc' },
            { key: 'kennung', order: 'asc' },
          ]"
          :multi-sort="true"
          sort-asc-icon="$asc"
          sort-desc-icon="$desc"
          items-per-page-text="Stimmzettel pro Seite:"
          sticky
        >
          <template #item.zeitpunkt="{ value }"> {{ value }} Uhr </template>

          <template #item.beschluss="{ value }">
            <v-icon
              :icon="value ? '$success' : ''"
              :color="value ? 'success' : ''"
            />
          </template>

          <template #item.beschlussergebnis="{ value }">
            <v-icon
              :icon="
                value == 'VALID'
                  ? '$stimmzettelValid'
                  : value == 'INVALID'
                    ? '$stimmzettelInvalid'
                    : ''
              "
              :color="
                value == 'VALID' ? 'success' : value == 'INVALID' ? 'error' : ''
              "
            />
            {{
              value == "VALID" ? "gültig" : value == "INVALID" ? "ungültig" : ""
            }}
          </template>

          <template #item.actions="{ item }">
            <div class="d-flex ga-2">
              <v-btn
                icon="$edit"
                size="x-small"
                :color="item.beschluss ? '' : 'primary'"
                @click="edit(item)"
                @mouseenter="register($event)"
              />
            </div>
          </template>
        </v-data-table>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <base-text-button active> Beschlussfassung Beenden </base-text-button>
      </v-card-actions>
    </v-card>
    <the-beschluss-fassen-dialog
      v-model="beschlussDialogVisible"
      :selected-stimmzettel="selected"
    />
  </div>
</template>

<script setup lang="ts">
import type { BeschlussTabelleItem } from "@/types/dse/BeschlussTabelleItem.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { onActivated, ref } from "vue";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseProgressLinear from "@/components/common/progressLinear/BaseProgressLinear.vue";
import TheBeschlussFassenDialog from "@/components/experimental/TheBeschlussFassenDialog.vue";

const { generateRandomNumber, getRandomItem, generateRandomBoolean } =
  useCommonTestDataFactory();

onActivated(async () => {
  generateTableData(66);
});

const headers = [
  { title: "Team", key: "team" },
  { title: "Kennung", key: "kennung" },
  { title: "Beschlussgrund", key: "grund" },
  { title: "Beschluss gefasst", key: "beschluss" },
  { title: "Beschlussergebnis", key: "beschlussergebnis" },
  { title: "", key: "actions", sortable: false },
];

const items = ref<BeschlussTabelleItem[]>([]);

function generateTableData(amount: number) {
  for (let i = 0; i < amount; i++) {
    const beschlussGefasst = generateRandomBoolean();
    items.value.push({
      id: generateRandomNumber(5),
      team: getRandomItem(["A", "B", "C"]),
      kennung: generateRandomNumber(2),
      grund: getRandomItem([
        "unzulässiger Zusatz/Vorbehalt",
        "Stimmzettel vollständig durchgestrichen",
        "Wählerwille nicht zweifelsfrei erkennbar",
        "handschriftlich ergänzte Person",
        "Kennzeichnung nicht eindeutig zuzuordnen",
      ]),
      beschluss: beschlussGefasst,
      beschlussergebnis: beschlussGefasst
        ? getRandomItem(["VALID", "INVALID"])
        : "",
    });
  }
}

const beschlussDialogVisible = ref(false);
const activator = ref(null);
const selected = ref<BeschlussTabelleItem | null>(null);

// Register current, hovered row to activator
// Preferrably called before edit()
function register(event) {
  activator.value = event.currentTarget;
}

// Select & load data to be edited
function edit(item) {
  beschlussDialogVisible.value = true;
  selected.value = item;
}
</script>

<!--<style scoped>
.hideSortBadges :deep(.v-data-table-header__sort-badge) {
  display: none !important;
}
</style>-->
