<template>
  <tr>
    <td>{{ index }}</td>
    <td>
      <v-checkbox-btn
        :model-value="isSelected"
        @update:model-value="onUngueltigCheckboxChanged"
      />
    </td>
    <td>
      <v-autocomplete
        :ref="REF_AUTOCOMPLETE_WAHLVORSCHLAG"
        v-model="modelValue.wahlvorschlagID"
        :items="wahlvorschlaege"
        :item-title="getWahlvorschlagTitle"
        item-value="identifikator"
        :disabled="isWahlvorschlagSelectionDisabled"
        label="Wahlvorschlag"
      />
    </td>
  </tr>
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/common/Ergebnis.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { PropType } from "vue";
import type { VAutocomplete } from "vuetify/components";

import { computed, useTemplateRef, watchEffect } from "vue";

import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";

const STAPELART_FOR_INVALID = StapelArtEnum.ObwCUngueltig;
const REF_AUTOCOMPLETE_WAHLVORSCHLAG = "wahlvorschlagSelection";

const { getWahlvorschlagTitle } = useWahlvorschlagUtils();

const modelValue = defineModel({
  type: Object as PropType<Ergebnis>,
  required: true,
});

const props = defineProps({
  index: {
    type: Number,
    required: true,
  },
  stapelArt: {
    type: String as PropType<StapelArtEnum>,
    required: true,
  },
  wahlvorschlaege: {
    type: Array as PropType<Wahlvorschlag[]>,
    required: true,
  },
});

const wahlvorschlagSelection = useTemplateRef<typeof VAutocomplete>(
  REF_AUTOCOMPLETE_WAHLVORSCHLAG
);

const emit = defineEmits<{
  selectionChanged: [newValue: boolean];
}>();

//Notwendig damit das Label nach dem Löschen wieder mittig steht, wie nach einem Clear
watchEffect(() => {
  if (modelValue.value.wahlvorschlagID === null) {
    wahlvorschlagSelection.value?.reset();
  }
});

const isSelected = computed(() => props.stapelArt === STAPELART_FOR_INVALID);
const isWahlvorschlagSelectionDisabled = computed(
  () => props.stapelArt === STAPELART_FOR_INVALID
);

function onUngueltigCheckboxChanged(newValue: boolean) {
  emit("selectionChanged", newValue);
}
</script>
