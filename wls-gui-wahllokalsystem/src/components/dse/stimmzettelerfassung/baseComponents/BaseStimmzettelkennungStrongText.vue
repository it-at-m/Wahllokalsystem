<template>
  <div class="mt-3 text-h1 font-weight-bold text-center">
    {{ teamName }} {{ stimmzettelkennung }}
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
});
</script>
