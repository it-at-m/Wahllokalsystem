<template>
  <tr>
    <td>D{{ wahlvorschlag.ordnungszahl }}</td>
    <td>{{ wahlvorschlagName }}</td>
    <td>
      <base-number-input
        v-model="modelValue.ergebnis"
        :rules="[required, minNumber(0), maxNumber(9999)]"
      />
    </td>
    <td>{{ ergebnisC }}</td>
    <td>{{ sumCAndA }}</td>
  </tr>
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/common/Ergebnis.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { PropType } from "vue";

import { computed } from "vue";

import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";

const { required, minNumber, maxNumber } = useRules();
const { getFirstKandidatNameOrEmptyString } = useWahlvorschlagUtils();

const modelValue = defineModel({
  type: Object as PropType<Ergebnis>,
  required: true,
});

const props = defineProps({
  wahlvorschlag: {
    type: Object as PropType<Wahlvorschlag>,
    required: true,
  },
  ergebnisStapelC: {
    type: Number as PropType<number | null>,
    required: false,
    default: null,
  },
});

const firstKandidatName = computed(() =>
  props.wahlvorschlag
    ? getFirstKandidatNameOrEmptyString(props.wahlvorschlag)
    : ""
);

const wahlvorschlagName = computed(() =>
  props.wahlvorschlag
    ? `${[props.wahlvorschlag.kurzname, firstKandidatName.value].join(", ")}`
    : ""
);

const ergebnisC = computed(() => props.ergebnisStapelC ?? 0);

const sumCAndA = computed(() => {
  return ergebnisC.value + (modelValue.value.ergebnis ?? 0);
});
</script>
