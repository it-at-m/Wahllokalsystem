<template>
  <v-container>
    <v-form v-model="isChangeRowCountFormValid">
      <div class="d-flex">
        <base-number-input
          v-model="newRowCount"
          :rules="validationRules"
          max-width="15rem"
          :label="inputFieldLabel"
        />
        <v-btn
          active
          :disabled="isApplyRowCountDisabled"
          class="ml-4 mt-3"
          @click="onChangeRowCountClicked"
        >
          {{ applyBtnLabel }}
        </v-btn>
      </div>
    </v-form>
  </v-container>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";

import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";

const { required } = useRules();

const newRowCount = defineModel<number | null>({ required: true });

const props = defineProps({
  currentRowCount: {
    type: Number,
    required: true,
  },
  rules: {
    type: Array<(value: number) => string | boolean>,
    required: false,
    default: [],
  },
  applyBtnLabel: {
    type: String,
    required: false,
    default: "Übernehmen",
  },
  inputFieldLabel: {
    type: String,
    required: false,
    default: "Anzahl",
  },
});

const emit = defineEmits<{
  rowCountChanged: [newValue: number | null];
}>();

const isChangeRowCountFormValid = ref<boolean | null>(null);

const isApplyRowCountDisabled = computed(
  () =>
    newRowCount.value === null ||
    isChangeRowCountFormValid.value !== true ||
    newRowCount.value === props.currentRowCount
);

const validationRules = computed(() => {
  return props.rules ? [required, ...props.rules] : [required];
});

function onChangeRowCountClicked() {
  emit("rowCountChanged", newRowCount.value);
}
</script>
