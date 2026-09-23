<template>
  <div
    :class="[
      'mt-1 font-weight-bold text-center',
      compact ? 'text-h4' : 'text-h1',
    ]"
  >
    {{ teamName }}{{ stimmzettelkennung }}
  </div>
</template>

<script setup lang="ts">
defineProps({
  teamName: {
    type: String,
    required: true,
  },
  stimmzettelkennung: {
    type: Number,
    required: true,
  },
  compact: {
    type: Boolean,
    required: false,
    default: false,
  },
});
</script>
