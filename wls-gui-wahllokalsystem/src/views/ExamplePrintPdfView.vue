<template>
  <div>
    <v-row class="pa-2">
      <v-col>
        <v-table
          density="compact"
          id="table"
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
          v-model="cake"
          label="Dein Lieblingskuchen"
          placeholder="Himbeertorte"
          id="print"
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
        @click="printDocumentDepr"
        color="primary"
      >
        Print with deprecated <br />
        document.write()
      </v-btn>
    </v-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
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

const cake = defineModel<String>("cake", { default: "srgrygedgyd" });
const cakeNumber = ref(0);
const toppings = ref([]);
const hungerIndex = ref([10, 35]);

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

class Data {
  title: String;
  cake: String;
  cakeNumber: number;
  toppings: Array<String>;
  hungerIndex: Array<number>;

  constructor(
    title: String,
    cake: String,
    cakeNumber: number,
    toppings: Array<String>,
    hungerIndex: Array<number>
  ) {
    this.title = title;
    this.cake = cake;
    this.cakeNumber = cakeNumber;
    this.toppings = toppings;
    this.hungerIndex = hungerIndex;
  }
}

function printDocumentDepr() {
  let data = new Data(
    "Heute gibt es Leckereien!",
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
    // document.write() is deprecated!
    printWindow.document.write(htmlFromData(data));

    printWindow.document.close();
    printWindow.focus();
    printWindow.print();
    printWindow.close();
  }
}

function htmlFromData(data: Data) {
  return `
            <!DOCTYPE html>
            <html lang="de">
            <head>
                <meta charset="utf-8"/>
                <style>
                    /****** Print Header ******/
                    @page {
                        size: auto;
                        margin-top: 0.0cm;
                        margin-left: 0;
                        margin-right: 0;
                    }

                    @media print {
                        div.footer {
                            position: fixed;
                            bottom: 0;
                            left: 1cm;
                            font-size: x-small;
                            z-index: 0;
                        }
                    }

                    /****** Default Value Tags ******/
                    body {
                        max-width: 21cm;
                        margin: 0;
                        font-size: 9pt;
                        writing-mode: lr-tb;
                        text-align: left;
                        font-family: Arial, serif;
                    }

                    html, body {
                        overflow-x: hidden;
                    }

                    /****** Table ******/
                    table {
                        border-collapse: collapse;
                    }

                    .table > tr > th,
                    .table > tr > td {
                        padding-top: 2px;
                        padding-bottom: 2px;
                    }

                    th,
                    td {
                        font-weight: normal;
                        padding: 0.12cm 0.2cm;
                        border: 1px solid #000000;
                    }

                    /** styles **/
                    .horizontal {
                        display: flex;
                    }

                    .vertical {
                        display: flex;
                        flex-direction: column;
                    }

                    .spaceBetween {
                        justify-content: space-between;
                    }

                    .widthLeft {
                        width: 10.95cm;
                    }

                    .widthRightTop {
                        width: 6.7cm;
                    }

                    .margin-top {
                        margin-top: 1.5cm;
                    }
                    .margin-leftright {
                        margin-left: 1cm;
                        margin-right: 1cm;
                    }
                </style>
                <title>Testdruck PDF</title>
            </head>
            <body>
                <h1 class="margin-top margin-leftright">
                ${data.title}
                </h1>
                <div class="horizontal spaceBetween margin-leftright">
                  <div class="vertical widthLeft">
                      <p>Für deinen perfekten Nachtisch hast du folgende Einstellungen getroffen:</p>
                      <br />
                      <p>Kuchen: ${data.cake}</p>
                      <p>Stücke: ${data.cakeNumber}</p>
                      <p>Toppings: ${data.toppings}</p>
                      <p>Hunger Index: ${data.hungerIndex[0]} - ${data.hungerIndex[1]}</p>
                      <p>${
                        data.hungerIndex[1] <= 20
                          ? "Du scheinst satt zu sein. Ist noch was vom Kuchen übrig?"
                          : data.hungerIndex[0] >= 50
                            ? "Du bist ziemlich hungrig, du solltest dir jetzt einen Kuchen backen! Und am besten gleich noch was für deine Kollegen mitbringen"
                            : ""
                      }</p>
                      <br />
                      <br />
                  </div>
                  <div class="vertical spaceBetween widthRightTop marginTopBottom_1">
                      <h2>Kalorienliste</h2>
                      <span>${document.getElementById("table").innerHTML}</span>
                  </div>
                </div>
                </div>
            </body>
            </html>
            `;
}
</script>
