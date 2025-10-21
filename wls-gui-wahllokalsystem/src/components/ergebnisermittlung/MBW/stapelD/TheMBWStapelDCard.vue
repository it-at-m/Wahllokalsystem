<template>
  <base-card-snipped-ergebnis
    :wahl-id="props.wahlId"
    :stapel-art="StapelArtEnum.MbwD"
    snipped-title="Ungültige Stimmzettel"
    stapelart="MBW_D"
  />
</template>

<script setup lang="ts">
import BaseCardSnippedErgebnis from "@/components/ergebnisermittlung/BaseCardSnippedErgebnis.vue";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const props = defineProps<{
  wahlId: string;
}>();
</script>
