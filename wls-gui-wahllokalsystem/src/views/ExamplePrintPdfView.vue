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
        class="ma-2"
        @click="printDocument"
        color="primary"
      >
        Print
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

const cake = ref("");
const cakeNumber = ref(0);
const toppings = ref(null);
const hungerIndex = ref([]);

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
  toppings: Array<String> | null;
  hungerIndex: Array<number>;

  constructor(
    title: String,
    cake: String,
    cakeNumber: number,
    toppings: Array<String> | null,
    hungerIndex: Array<number>
  ) {
    this.title = title;
    this.cake = cake;
    this.cakeNumber = cakeNumber;
    this.toppings = toppings;
    this.hungerIndex = hungerIndex;
  }
}

function printDocument() {
  let data = new Data(
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
    printWindow.document.body.innerHTML = htmlFromData(data);
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
              font-size: 12pt;
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
              width: 10cm;
              margin-right: 0.4cm;
          }

          .widthRightTop {
              width: 6.7cm;
          }

          .margin-top {
              margin-top: 1cm;
          }

          .margin-leftright {
              margin-left: 1cm;
              margin-right: 1cm;
          }

          .smallfont {
              font-size: 9pt;
          }
       </style>
        <title>Testdruck PDF</title>
      </head>
      <body>
        <h1 class="margin-top margin-leftright">${data.title ? data.title : "Alternativer Titel"}</h1>
        <div class="horizontal spaceBetween margin-leftright">
          <div class="vertical widthLeft">
            <p>Für deinen perfekten Nachtisch hast du folgende Einstellungen getroffen: mehr text um zu sehen was passiert wenn der platz nicht reicht</p>
            <br />
            <p>Kuchen: ${data.cake ? data.cake : "Standardkuchen"}</p>
            <p>Stücke: ${data.cakeNumber ? data.cakeNumber : "2 schaffst du bestimmt!"}</p>
            <p>Toppings: ${data.toppings ? data.toppings : "Weniger ist mehr."}</p>
            <p>Hunger Index: ${data.hungerIndex[0] ? data.hungerIndex[0] : "Hier ist was schief gelaufen"} - ${data.hungerIndex[1] ? data.hungerIndex[1] : "Das Messgerät spinnt."}</p>
            <p>${
              data.hungerIndex[1] <= 20
                ? "Du scheinst satt zu sein. Ist noch was vom Kuchen übrig?"
                : data.hungerIndex[0] >= 50
                  ? "Du bist ziemlich hungrig, du solltest dir jetzt einen Kuchen backen! Und am besten gleich noch was für deine Kollegen mitbringen"
                  : ""
            }
            </p>
            <br />
            <br />
          </div>
          <div class="vertical spaceBetween widthRightTop marginTopBottom_1">
            <h4>Kalorienliste</h4>
            <span>
              ${(() => {
                const tableElement = document.getElementById("table");
                return tableElement
                  ? tableElement.innerHTML
                  : "Keine Tabellendaten gefunden";
              })()}
            </span>
          </div>
        </div>
        <div class="margin-top margin-leftright">
          <h3>Aus offensichtlichen obligatorischen Gründen:</h3>
          <span class="smallfont">und um den seitenumbruch zu testen</span>
          <p>
              Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore
              et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.
              Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet,
              consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,
              sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea
              takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed
              diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et
              accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum
              dolor sit amet. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel
              illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent
              luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer
              adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat.
              Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea
              commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse.
              Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore
              et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.
              Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet,
              consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,
              sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea
              takimata sanctus est Lorem ipsum dolor sit amet. 
          </p>
        </div>
      </body>
      </html>`;
}
</script>
