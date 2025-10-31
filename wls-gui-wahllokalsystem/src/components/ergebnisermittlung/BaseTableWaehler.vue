<template>
  <v-table>
    <tbody>
      <tr v-if="isUWB">
        <td>B1</td>
        <td>Wähler mit Stimmabgabevermerken im Wählerverzeichnis</td>
        <td>{{ b1 }}</td>
      </tr>
      <tr v-if="isUWB">
        <td>B2</td>
        <td>Wähler mit Wahlschein</td>
        <td>{{ b2 }}</td>
      </tr>
      <tr class="bg-grey-lighten-3">
        <td
          v-if="isUWB"
          class="font-weight-bold"
        >
          B1+B2
        </td>
        <td
          v-if="isBWB"
          class="font-weight-bold"
        >
          B
        </td>
        <td class="font-weight-bold">Wähler insgesamt</td>
        <td
          v-if="isUWB"
          class="font-weight-bold"
        >
          {{ b1 + b2 }}
        </td>
        <td
          v-if="isBWB"
          class="font-weight-bold"
        >
          {{ b }}
        </td>
      </tr>
    </tbody>
  </v-table>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { onMounted, ref } from "vue";

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
