<template>
  <v-icon
    :icon="icon"
    :color="color"
  />
</template>
<script setup lang="ts">
import type { PersistedStimmzettelGueltigkeitEnum } from "@/types/dse/persistedStimmzettel/PersistedStimmzettelGueltigkeitEnum.ts";
import type { PropType } from "vue";

import { computed } from "vue";

const props = defineProps({
  gueltigkeit: {
    type: String as PropType<PersistedStimmzettelGueltigkeitEnum>,
    required: true,
  },
});

const typeMapping: Record<PersistedStimmzettelGueltigkeitEnum, string> = {
  VALID: "$stimmzettelGueltig",
  INVALID: "$stimmzettelUngueltig",
  BESCHLUSS_AUSSTEHEND: "$stimmzettelBeschluss",
  BWB_PSEUDO_STIMMZETTEL_LEERER_UMSCHLAG: "$stimmzettelUngueltig",
  LEER: "$stimmzettelUngueltig",
};
const colorMapping: Record<PersistedStimmzettelGueltigkeitEnum, string> = {
  VALID: "success",
  INVALID: "error",
  BESCHLUSS_AUSSTEHEND: "info",
  BWB_PSEUDO_STIMMZETTEL_LEERER_UMSCHLAG: "error",
  LEER: "error",
};

const icon = computed(() => typeMapping[props.gueltigkeit]);
const color = computed(() => colorMapping[props.gueltigkeit]);
</script>
