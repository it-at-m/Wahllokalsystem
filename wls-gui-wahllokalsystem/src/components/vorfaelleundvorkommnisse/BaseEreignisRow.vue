<template>
  <v-row>
    <v-col class="text-center mt-5 flex-grow-0">{{ lineNumber }}</v-col>
    <v-col
      cols="3"
      style="min-width: 380px; max-width: 380px"
    >
      <v-row>
        <v-col cols="7">
          <base-date-input
            v-model="dateOnly"
            :rules="[
              required,
              dateNotInFuture,
              dateGreaterOrEqual(currentUserWahltag),
            ]"
          />
        </v-col>
        <v-col cols="5"><base-time-input v-model="timeOnly" /></v-col>
      </v-row>
    </v-col>
    <v-col>
      <v-textarea
        v-model="ereignisModel.beschreibung"
        :rules="[minLength(4), maxLength(maxLengthForEreignisBeschreibung)]"
        rows="1"
        label="Beschreibung"
        auto-grow
        autofocus
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
import type { EreignisPayload } from "@/types/vorfaelleundvorkommnisse/EreignisPayload.ts";
import type { PropType } from "vue";

import { storeToRefs } from "pinia";
import { computed, watch } from "vue";

import BaseDateInput from "@/components/common/inputs/BaseDateInput.vue";
import BaseTimeInput from "@/components/common/inputs/BaseTimeInput.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useDateTimeSyncer } from "@/composables/common/dateTimeSyncer.ts";
import { useRules } from "@/composables/common/rules.ts";
import { MAX_LENGTH_FOR_TEXT_INPUT } from "@/constants.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { required, maxLength, minLength, dateNotInFuture, dateGreaterOrEqual } =
  useRules();
const { currentUserWahltag } = storeToRefs(useUserStore());
const { toHhMm, toGermanDate } = useDateTimeFormatter();

const maxLengthForEreignisBeschreibung = MAX_LENGTH_FOR_TEXT_INPUT;

const { indexOfModel } = defineProps({
  indexOfModel: {
    type: Number,
    required: true,
  },
});

const ereignisModel = defineModel({
  type: Object as PropType<Ereignis>,
  required: true,
});

const lineNumber = computed(() => indexOfModel + 1); //cause indexes start by 0

const { updateUhrzeitByIndex } = useEreignisStore();

const ereignisUhrzeit = computed(() => ereignisModel.value.uhrzeit);

const { dateOnly, timeOnly, dateAndTimeCombined } =
  useDateTimeSyncer(ereignisUhrzeit);

watch(dateAndTimeCombined, (newValue) => {
  if (ereignisModel.value.uhrzeit?.getTime() !== newValue?.getTime()) {
    updateUhrzeitByIndex(newValue ?? undefined, indexOfModel);
  }
});

const emit = defineEmits<{
  delete: [erreignisPayload: EreignisPayload];
}>();

function onDeleteIconClicked() {
  const ereignisPayload = {
    dateStr: toGermanDate(dateOnly.value),
    timeStr: toHhMm(timeOnly.value),
    beschreibung: ereignisModel.value.beschreibung,
  };
  emit("delete", ereignisPayload);
}
</script>
