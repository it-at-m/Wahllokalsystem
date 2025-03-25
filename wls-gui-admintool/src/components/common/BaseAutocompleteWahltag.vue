<template>
  <v-autocomplete
    clearable
    chips
    label="Wahltage auswählen"
    :items="props.items"
    :multiple="props.multiple"
    variant="outlined"
    v-model="internalSelected"
    @update:model-value="handleSelectionChange"
  ></v-autocomplete>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { VAutocomplete } from "vuetify/components";

const props = defineProps({
  /**
   * Wahltage to select
   */
  items: {
    type: Array<string>,
    required: true,
  },
  multiple: {
    type: Boolean,
    default: true,
    required: false,
  },
  selected: {
    type: Array<string>,
    default: [],
    required: false,
  },
});

const internalSelected = ref<string[]>([]);
const emit = defineEmits(["update:selected"]);

function handleSelectionChange(newValue: string[]) {
  internalSelected.value = newValue;
  emit("update:selected", newValue);
}

watch(
  () => props.selected,
  (newVal) => {
    // convert single string inputs to array
    const valueArray = Array.isArray(newVal) ? newVal : [newVal];
    internalSelected.value = [...valueArray];
  },
  { immediate: true }
);
</script>
