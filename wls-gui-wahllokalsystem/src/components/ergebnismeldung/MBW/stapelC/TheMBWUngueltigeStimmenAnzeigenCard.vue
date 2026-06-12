<template>
  <v-card>
    <v-card-title> Ungültige Stimmzettel </v-card-title>
    <v-card-text>
      <v-table>
        <thead>
          <tr>
            <th class="index-column" />
            <th />
            <th class="font-weight-bold text-right">Zweifelsfrei ungültig</th>
            <th class="font-weight-bold text-right">Laut Beschluss ungültig</th>
            <th class="font-weight-bold text-right">Insgesamt</th>
          </tr>
        </thead>
        <tfoot>
          <tr>
            <td class="font-weight-bold index-column">C</td>
            <td class="font-weight-bold">Ungültige Stimmzettel</td>
            <td class="font-weight-bold text-right">{{ ungueltigeStimmen }}</td>
            <td class="font-weight-bold text-right">
              {{ ungueltigeStimmzettelNachBeschluss }}
            </td>
            <td class="font-weight-bold text-right">
              {{ sumUngueltigeStimmzettel }}
            </td>
          </tr>
        </tfoot>
      </v-table>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { computed, onActivated, ref } from "vue";

import { useErgebnisService } from "@/composables/ergebnismeldung/common/ergebnisService.ts";
import { useBedenklicheStimmzettelService } from "@/composables/ergebnismeldung/MBW/bedenklicheStimmzettelService.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { ValidityEnum } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/ValidityEnum.ts";

const { getErgebnisse } = useErgebnisService();
const { getBedenklicheStimmzettel } = useBedenklicheStimmzettelService();

const props = defineProps<{
  wahlbezirkId: string;
  wahlId: string;
}>();

const ungueltigeStimmen = ref(0);
const ungueltigeStimmzettelNachBeschluss = ref(0);
const sumUngueltigeStimmzettel = computed(
  () => ungueltigeStimmen.value + ungueltigeStimmzettelNachBeschluss.value
);

onActivated(async () => {
  const result = await getErgebnisse(
    props.wahlbezirkId,
    props.wahlId,
    StapelArtEnum.MbwDUngueltig,
    false
  );
  ungueltigeStimmen.value = result?.ergebnisse[0]?.ergebnis || 0;

  const bedenklicheStimmzettel = await getBedenklicheStimmzettel(
    props.wahlId,
    props.wahlbezirkId,
    false
  );
  ungueltigeStimmzettelNachBeschluss.value = bedenklicheStimmzettel
    ? bedenklicheStimmzettel.filter(
        (stimmzettel) => stimmzettel.validity === ValidityEnum.VALID
      ).length
    : 0;
});
</script>
