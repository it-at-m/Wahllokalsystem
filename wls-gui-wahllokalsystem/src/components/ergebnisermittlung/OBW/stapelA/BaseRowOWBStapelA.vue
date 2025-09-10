<template>
  <tr>
    <td>D{{ wahlvorschlag.ordnungszahl }}</td>
    <td>{{ wahlvorschlagName }}</td>
    <td>
      <v-number-input
        v-model="modelValue.ergebnis"
        :rules="[required]"
      />
    </td>
  </tr>
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { PropType } from "vue";

import { computed } from "vue";

import { useRules } from "@/composables/common/rules.ts";
import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";

const { required } = useRules();
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
</script>
