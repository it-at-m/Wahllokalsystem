<template>
  <base-text-button prepend-icon="$updateTime">
    <slot name="default">Aktualisieren</slot>
  </base-text-button>
</template>
<script setup lang="ts">
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
</script>
