<template>
  <div>
    <v-row>
      <v-col class="text-center">
        <h2>This view shows how printing will work</h2>
      </v-col>
    </v-row>
    <v-row class="pa-2">
      <v-col>
        <v-table
          ref="tableRef"
          density="compact"
        >
          <thead>
            <tr>
              <th class="text-left">Name</th>
              <th class="text-left">Calories</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="item in desserts"
              :key="item.name"
            >
              <td>{{ item.name }}</td>
              <td>{{ item.calories }}</td>
            </tr>
          </tbody>
        </v-table>
      </v-col>
      <v-col class="mt-5">
        <v-text-field
          id="print"
          v-model="cake"
          label="Dein Lieblingskuchen"
          placeholder="Himbeertorte"
          width="500"
        ></v-text-field>
        <base-number-input
          v-model="cakeNumber"
          label="Maximale Kuchenstücke die du verdrücken kannst"
          width="500"
        ></base-number-input>
        <v-autocomplete
          v-model="toppings"
          label="Deine Lieblingstoppings"
          width="500"
          :items="['Streusel', 'Schokoglasur', 'Früchte']"
          multiple
        ></v-autocomplete>
        <v-range-slider
          v-model="hungerIndex"
          label="Dein Hunger jetzt gerade"
          thumb-label
          thumb-size="14"
          width="500"
        >
        </v-range-slider>
      </v-col>
    </v-row>

    <v-row
      justify="center"
      class="mt-10"
    >
      <v-btn
        class="ma-2"
        color="primary"
        @click="printDocument"
      >
        Print
      </v-btn>
    </v-row>
  </div>
</template>

<script setup lang="ts">
import { ref, useTemplateRef } from "vue";
import {
  VAutocomplete,
  VBtn,
  VCol,
  VRangeSlider,
  VRow,
  VTable,
  VTextField,
} from "vuetify/components";

import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { Data } from "@/types/Data.ts";
import { htmlFromData } from "@/util/getHtml.ts";

const cake = ref("");
const cakeNumber = ref(0);
const toppings = ref(null);
const hungerIndex = ref([]);
const tableRef = useTemplateRef("tableRef");

const desserts = ref([
  { name: "Frozen Yogurt", calories: 159 },
  { name: "Ice cream sandwich", calories: 237 },
  { name: "Eclair", calories: 262 },
  { name: "Cupcake", calories: 305 },
  { name: "Gingerbread", calories: 356 },
  { name: "Jelly bean", calories: 375 },
  { name: "Lollipop", calories: 392 },
  { name: "Honeycomb", calories: 408 },
  { name: "Donut", calories: 452 },
  { name: "KitKat", calories: 518 },
]);

function printDocument() {
  const data = new Data(
    "Heute gibt es leckeren Nachtisch!",
    cake.value,
    cakeNumber.value,
    toppings.value,
    hungerIndex.value
  );

  const printWindow = window.open(
    "",
    "",
    "left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0"
  );

  if (printWindow) {
    printWindow.document.body.innerHTML = htmlFromData(data, desserts);
    printWindow.print();
    printWindow.close();
  }
}
</script>
