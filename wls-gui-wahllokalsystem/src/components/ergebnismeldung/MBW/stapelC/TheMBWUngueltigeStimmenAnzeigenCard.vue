<template>
  <v-card>
    <v-card-title> Ungültige Stimmzettel </v-card-title>
    <v-card-text>
      <v-table>
        <thead>
          <tr>
            <th class="index-column" />
            <th />
            <th class="font-weight-bold text-right">Insgesamt</th>
          </tr>
        </thead>
        <tfoot>
          <tr>
            <td class="font-weight-bold index-column">C</td>
            <td class="font-weight-bold">Ungültige Stimmzettel</td>
            <td class="font-weight-bold text-right">{{ ungueltigeStimmen }}</td>
          </tr>
        </tfoot>
      </v-table>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { onActivated, ref } from "vue";

import { useErgebnisService } from "@/composables/ergebnismeldung/common/ergebnisService.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";

const { getErgebnisse } = useErgebnisService();

const props = defineProps<{
  wahlbezirkId: string;
  wahlId: string;
}>();

const ungueltigeStimmen = ref(0);

onActivated(async () => {
  const result = await getErgebnisse(
    props.wahlbezirkId,
    props.wahlId,
    StapelArtEnum.MbwDUngueltig,
    false
  );
  ungueltigeStimmen.value = result?.ergebnisse[0]?.ergebnis || 0;
});
</script>
