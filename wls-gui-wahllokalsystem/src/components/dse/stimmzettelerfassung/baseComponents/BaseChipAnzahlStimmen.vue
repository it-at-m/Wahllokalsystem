<template>
  <v-chip
    v-if="visible"
    size="small"
    :color="color"
    variant="tonal"
    :style="hidden ? 'visibility: hidden' : ''"
    :aria-hidden="hidden"
  >
    {{ stimmen }}
  </v-chip>
</template>
<script setup lang="ts">
interface Props {
  stimmen: number;
  color: string;
  visible?: boolean;
  hidden?: boolean;
}

withDefaults(defineProps<Props>(), {
  visible: true,
  hidden: false,
});
</script>
