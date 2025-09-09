<template>
  <v-row>
    <v-col>D{{ wahlvorschlag.ordnungszahl }}</v-col>
    <v-col>{{ wahlvorschlagName }}</v-col>
    <v-col
      ><v-number-input
        v-model="modelValue.ergebnis"
        :rules="[required]"
    /></v-col>
  </v-row>
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { PropType } from "vue";

import { computed } from "vue";

import { useRules } from "@/composables/common/rules.ts";

const { required } = useRules();

const modelValue = defineModel({
  type: Object as PropType<Ergebnis>,
  required: true,
});

const props = defineProps({
  wahlvorschlag: {
    type: Object as PropType<Wahlvorschlag>,
    required: true,
  },
});

const firstKandidatName = computed(() => {
  if (props.wahlvorschlag?.kandidaten) {
    const kandidatWithLowedListenPosition = [
      ...props.wahlvorschlag.kandidaten,
    ].reduce((min, current) =>
      current.listenposition < min.listenposition ? current : min
    );
    return kandidatWithLowedListenPosition.name;
  } else {
    return "";
  }
});

const wahlvorschlagName = computed(() =>
  props.wahlvorschlag
    ? `${[props.wahlvorschlag.kurzname, firstKandidatName.value].join(", ")}`
    : ""
);
</script>
