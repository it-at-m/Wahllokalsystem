<template>
  <v-stepper
    v-if="wahltagEvents.length > 0"
    v-model="activeStepComputed"
    editable
  >
    <v-stepper-header>
      <template
        v-for="(event, index) in wahltagEvents"
        :key="event.wahltagID"
      >
        <v-stepper-item
          :value="index"
          :complete="lastSetActiveStep > index"
          :title="toStepTitle(event)"
          :subtitle="event.beschreibung"
        />

        <v-divider v-if="index < wahltagEvents.length - 1" />
      </template>
    </v-stepper-header>
    <v-stepper-window>
      <v-stepper-window-item
        v-for="(event, index) in wahltagEvents"
        :key="event.wahltagID"
        :value="index"
      >
        <base-step-wahltag-init
          :wahltag-event="event"
          :wahltermin-daten-exists="konfigurierteWahltage.get(event.wahltagID)"
        />
      </v-stepper-window-item>
    </v-stepper-window>
    <v-stepper-actions
      v-if="hasMultipleEvents"
      @click:next="onNextClicked"
      @click:prev="onPrevClicked"
    />
  </v-stepper>
</template>
<script setup lang="ts">
import type { WahltagEvent } from "@/types/wahltag/WahltagEvent.ts";

import { computed, ref } from "vue";
import {
  VDivider,
  VStepper,
  VStepperActions,
  VStepperHeader,
  VStepperItem,
  VStepperWindow,
  VStepperWindowItem,
} from "vuetify/components";

import BaseStepWahltagInit from "@/components/wahltag/BaseStepWahltagInit.vue";

const props = defineProps({
  wahltagEvents: {
    type: Array<WahltagEvent>,
    required: true,
  },
  konfigurierteWahltage: {
    type: Map<string, boolean | undefined>,
    required: false,
    default: () => new Map<string, boolean | undefined>(),
  },
});

const lastSetActiveStep = ref(0);
const activeStepComputed = computed({
  get() {
    return lastSetActiveStep.value >= props.wahltagEvents.length
      ? 0
      : lastSetActiveStep.value;
  },
  set(newValue: number) {
    lastSetActiveStep.value = newValue;
  },
});
const hasMultipleEvents = computed(() => (props.wahltagEvents.length ?? 0) > 1);

function onNextClicked() {
  activeStepComputed.value = activeStepComputed.value + 1;
}

function onPrevClicked() {
  activeStepComputed.value = activeStepComputed.value - 1;
}

function toStepTitle(wahltagEvent: WahltagEvent) {
  return `Wahlnummer: ${wahltagEvent.nummer}`;
}
</script>
