<template>
  <div>
    <v-chip v-if="ergebnis > 0">{{ ergebnis }}</v-chip>
    <v-chip
      v-if="invalidVotes > 0"
      color="error"
      >{{ invalidVotes }}</v-chip
    >
    <v-chip
      v-if="validVotes > 0"
      color="success"
      >{{ validVotes }}</v-chip
    >
  </div>
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { PropType } from "vue";

import { computed } from "vue";

const props = defineProps({
  ergebnis: {
    type: Object as PropType<Ergebnis>,
    required: true,
  },
  maxValidVotes: {
    type: Number,
    required: false,
    default: 3,
  },
});

const ergebnis = computed(() => props.ergebnis?.ergebnis ?? 0);

const invalidVotes = computed(() =>
  ergebnis.value > props.maxValidVotes
    ? ergebnis.value - props.maxValidVotes
    : 0
);
const validVotes = computed(() =>
  ergebnis.value > props.maxValidVotes ? props.maxValidVotes : ergebnis.value
);
</script>

<style scoped></style>
