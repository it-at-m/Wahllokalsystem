<template>
  <div>
    <v-number-input
      v-model="modelVotes"
      control-variant="stacked"
      density="compact"
      hide-details
      :min="0"
    ></v-number-input>
    <v-checkbox
      v-if="!useSwitchComponent"
      v-model="modelIsDiscarded"
      hide-details
      inset
      :true-value="false"
      true-icon="$kandidatNonRemoved"
      :false-value="true"
      false-icon="$kandidatRemoved"
    />
    <v-switch
      v-if="useSwitchComponent"
      v-model="modelIsDiscarded"
      hide-details
      false-icon="$kandidatNonRemoved"
      true-icon="$kandidatRemoved"
    />
  </div>
</template>

<script setup lang="ts">
const modelVotes = defineModel({
  type: Number,
});

const modelIsDiscarded = defineModel("isDiscarded", {
  type: Boolean,
});

const props = defineProps({
  useSwitchComponent: {
    type: Boolean,
    required: false,
    default: false,
  },
});

const emit = defineEmits<{
  add: [count: number];
  subtract: [count: number];
  discard: [];
  revokeDiscard: [];
}>();
</script>
