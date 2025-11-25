<template>
  <div>
    <v-card-title> Ungültige Stimmen </v-card-title>
    <v-card-text>
      <v-table>
        <thead>
          <tr>
            <th />
            <th />
            <th class="font-weight-bold text-right">Insgesamt</th>
          </tr>
        </thead>
        <tbody>
          <tr class="bg-grey-lighten-3">
            <td class="font-weight-bold">C</td>
            <td class="font-weight-bold">Ungültige Stimmen</td>
            <td class="font-weight-bold text-right">{{ ungueltigeStimmen }}</td>
          </tr>
        </tbody>
      </v-table>
    </v-card-text>
  </div>
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
