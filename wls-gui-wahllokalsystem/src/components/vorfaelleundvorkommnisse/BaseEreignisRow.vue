<template>
  <v-row>
    <v-col class="text-center mt-5 flex-grow-0">{{ lineNumber }}</v-col>
    <v-col
      cols="3"
      class="flex-grow-0 flex-shrink-0"
      style="min-width: 380px"
    >
      <v-row>
        <v-col cols="7"><base-date-input v-model="dateOnly" /></v-col>
        <v-col cols="5"><base-time-input v-model="timeOnly" /></v-col>
      </v-row>
    </v-col>
    <v-col>
      <v-textarea
        v-model="model.beschreibung"
        :rules="[MIN_LENGTH(4), MAX_LENGTH(maxLengthForEreignisBeschreibung)]"
        rows="1"
        label="Beschreibung"
        auto-grow
        clearable
        autofokus
        persistent-counter
        :counter="maxLengthForEreignisBeschreibung"
      />
    </v-col>
    <v-col class="text-center mt-5 flex-grow-0">
      <v-icon
        data-test="delete-ereignis-icon"
        icon="$delete"
        title="Löschen"
        @click="onDeleteIconClicked"
      />
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";
import type { PropType } from "vue";

import { computed, watch } from "vue";
import { VCol, VIcon, VRow, VTextarea } from "vuetify/components";

import BaseDateInput from "@/components/common/inputs/BaseDateInput.vue";
import BaseTimeInput from "@/components/common/inputs/BaseTimeInput.vue";
import { useDateAndTime } from "@/composables/common/dateAndTime.ts";
import { MAX_LENGTH, MIN_LENGTH } from "@/util/rules.ts";

const maxLengthForEreignisBeschreibung = 500;

defineProps({
  lineNumber: {
    type: Number,
    required: true,
  },
});

const model = defineModel({
  type: Object as PropType<Ereignis>,
  required: true,
});

const ereignisUhrzeit = computed(() => model.value.uhrzeit);

const { dateOnly, timeOnly, dateAndTimeCombined } =
  useDateAndTime(ereignisUhrzeit);

watch(dateAndTimeCombined, (newValue) => {
  console.debug(`watch of dateAndTimeCombined - newValue: ${newValue}`);
  if (model.value.uhrzeit?.getTime() !== newValue?.getTime()) {
    model.value.uhrzeit = newValue ?? undefined;
  }
});

const emit = defineEmits<{ delete: [] }>();

function onDeleteIconClicked() {
  emit("delete");
}
</script>

<style scoped></style>
