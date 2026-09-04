<template>
  <v-icon
    :icon="hideBeschlussIcon && value === 'BESCHLUSS_AUSSTEHEND' ? '' : icon"
    :color="hideBeschlussIcon && value === 'BESCHLUSS_AUSSTEHEND' ? '' : color"
  />
</template>
<script setup lang="ts">
import type { StimmzettelGueltigkeitEnum } from "@/types/dse/persistedStimmzettel/StimmzettelGueltigkeitEnum.ts";
import type { PropType } from "vue";

import { computed } from "vue";

const props = defineProps({
  value: {
    type: String as PropType<StimmzettelGueltigkeitEnum>,
    required: true,
  },
  hideBeschlussIcon: { type: Boolean },
});

const typeMapping: Record<StimmzettelGueltigkeitEnum, string> = {
  VALID: "$stimmzettelGueltig",
  INVALID: "$stimmzettelUngueltig",
  BESCHLUSS_AUSSTEHEND: "$stimmzettelBeschluss",
  BWB_PSEUDO_STIMMZETTEL_LEERER_UMSCHLAG: "$stimmzettelUngueltig",
  LEER: "$stimmzettelUngueltig",
};
const colorMapping: Record<StimmzettelGueltigkeitEnum, string> = {
  VALID: "success",
  INVALID: "error",
  BESCHLUSS_AUSSTEHEND: "info",
  BWB_PSEUDO_STIMMZETTEL_LEERER_UMSCHLAG: "error",
  LEER: "error",
};

const icon = computed(() => typeMapping[props.value]);
const color = computed(() => colorMapping[props.value]);
</script>
