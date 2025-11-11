<template>
  <v-table>
    <tbody>
      <tr>
        <td />
        <td />
        <td class="font-weight-bold">Insgesamt</td>
      </tr>
      <tr class="bg-grey-lighten-3">
        <td class="font-weight-bold">C</td>
        <td class="font-weight-bold">Ungültige Stimmen</td>
        <td class="font-weight-bold">{{ c }}</td>
      </tr>
    </tbody>
  </v-table>
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

const c = ref(0);

onMounted(async () => {
  const result = await getErgebnisse(
    props.wahlbezirkId,
    props.wahlId,
    StapelArtEnum.MbwD,
    false
  );
  c.value = result?.ergebnisse[0]?.ergebnis || 0;
});
</script>
