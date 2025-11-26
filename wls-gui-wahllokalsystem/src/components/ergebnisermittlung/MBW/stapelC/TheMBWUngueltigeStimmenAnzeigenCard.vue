<template>
  <v-card>
    <v-card-title> Ungültige Stimmen </v-card-title>
    <v-card-text>
      <v-table>
        <tbody class="bottom-border-black">
          <tr>
            <td class="index-column" />
            <td />
            <td class="font-weight-bold text-right">Insgesamt</td>
          </tr>
        </tbody>
        <tfoot>
          <tr>
            <td class="font-weight-bold">C</td>
            <td class="font-weight-bold">Ungültige Stimmen</td>
            <td class="font-weight-bold text-right">{{ ungueltigeStimmen }}</td>
          </tr>
        </tfoot>
      </v-table>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";

import { useErgebnisService } from "@/composables/ergebnismeldung/ergebnisService.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { getErgebnisse } = useErgebnisService();

const props = defineProps<{
  wahlbezirkId: string;
  wahlId: string;
}>();

const ungueltigeStimmen = ref(0);

onMounted(async () => {
  const result = await getErgebnisse(
    props.wahlbezirkId,
    props.wahlId,
    StapelArtEnum.MbwDUngueltig,
    false
  );
  ungueltigeStimmen.value = result?.ergebnisse[0]?.ergebnis || 0;
});
</script>
