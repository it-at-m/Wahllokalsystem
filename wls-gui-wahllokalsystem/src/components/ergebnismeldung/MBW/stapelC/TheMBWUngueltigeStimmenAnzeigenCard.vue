<template>
  <base-card-ungueltige-stimmen-anzeigen
    :ungueltige-stimmen="ungueltigeStimmen"
    :ungueltige-stimmzettel-nach-beschluss="ungueltigeStimmzettelNachBeschluss"
  />
</template>

<script setup lang="ts">
import { onActivated, ref } from "vue";

import BaseCardUngueltigeStimmenAnzeigen from "@/components/ergebnismeldung/common/BaseCardUngueltigeStimmenAnzeigen.vue";
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
        (stimmzettel) => stimmzettel.validity === ValidityEnum.INVALID
      ).length
    : 0;
});
</script>
