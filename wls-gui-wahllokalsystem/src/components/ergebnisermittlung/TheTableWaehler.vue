<template>
  <v-table>
    <tbody>
      <tr
        v-for="(row, index) in rows"
        :key="index"
      >
        <td
          v-for="(value, idx) in row"
          :key="idx"
        >
          {{ value }}
        </td>
      </tr>
      <tr class="bg-grey-lighten-3">
        <td
          v-for="(value, index) in resultRow"
          :key="index"
          class="font-weight-bold"
        >
          {{ value }}
        </td>
      </tr>
    </tbody>
  </v-table>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, onMounted, ref } from "vue";

import { useErgebnisermittlungService } from "@/composables/ergebnisermittlung/ergebnisermittlungService.ts";
import { useStimmabgabevermerkeService } from "@/composables/stimmabgabevermerke/stimmabgabevermerkeService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { isUWB, isBWB } = storeToRefs(useUserStore());
const { waehlerverzeichnisActions, wahlenActions } = useWahlenStore();
const { getStimmabgabevermerke } = useStimmabgabevermerkeService();
const { getStimmzettelumschlaege } = useErgebnisermittlungService();

const props = defineProps<{
  wahlbezirkId: string;
  wahlId: string;
}>();

const b1 = ref(0);
const b2 = ref(0);
const b = ref(0);

const rows = computed(() =>
  isUWB.value
    ? [
        [
          "B1",
          "Wähler mit Stimmabgabevermerken im Wählerverzeichnis",
          b1.value,
        ],
        ["B2", "Wähler mit Wahlschein", b2.value],
      ]
    : []
);
const resultRow = computed(() =>
  isUWB.value
    ? ["B1+B2", "Wähler insgesamt", b1.value + b2.value]
    : ["B", "Wähler insgesamt", b.value]
);

onMounted(async () => {
  if (isUWB.value) {
    const waehlerverzeichnisNummer =
      waehlerverzeichnisActions.getWaehlerverzeichnisNummerOrUndefinedById(
        props.wahlId
      );
    if (waehlerverzeichnisNummer) {
      const loadedStimmabgabevermerke = await getStimmabgabevermerke(
        props.wahlbezirkId,
        waehlerverzeichnisNummer
      );
      // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
      b1.value = loadedStimmabgabevermerke.wahldaten[0].vermerke
        .flatMap((vermerk) => vermerk.stimmzettel)
        .reduce((summe, stimmzettel) => summe + (stimmzettel.anzahl || 0), 0);
      b2.value = Array.from(
        // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
        loadedStimmabgabevermerke.wahldaten[0].eingenommeneWahlscheine.values()
      ).reduce((sum, value) => sum + value, 0);
    }
  }
  if (isBWB.value) {
    const wahl = wahlenActions.getWahlOrUndefinedById(props.wahlId);
    if (wahl) {
      const loadedStimmzettelumschlaege = await getStimmzettelumschlaege(
        wahl,
        props.wahlbezirkId,
        "",
        false
      );
      b.value = loadedStimmzettelumschlaege?.anzahlWaehler || 0;
    }
  }
});
</script>
