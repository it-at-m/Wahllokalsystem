<template>
  <v-card>
    <v-card-title class="font-weight-bold">{{ title }}</v-card-title>
    <v-card-subtitle class="font-weight-bold mb-10">{{
      subtitle
    }}</v-card-subtitle>
    <div class="d-flex flex-column ga-5 mx-4">
      <slot />
    </div>
    <v-card-actions>
      <base-button-save
        :save-text="`${title} senden`"
        prepend-icon="$cloudUpload"
        :loading="isSending"
        @click="$emit('save')"
      />
      <base-button-save
        :save-text="`${title} korrigieren`"
        prepend-icon="$edit"
        :disabled="!isKorrigierenValid"
        @click="$emit('edit')"
      />
      <base-button-save
        :save-text="`${title} drucken`"
        prepend-icon="$printer"
        :disabled="!isDruckenValid"
        :loading="isDruckenLoading"
        @click="$emit('print')"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";

defineProps<{
  title: string;
  subtitle: string;
  isSending: boolean;
  isKorrigierenValid: boolean | null | undefined;
  isDruckenValid: boolean | null | undefined;
  isDruckenLoading: boolean;
}>();

defineEmits<{
  save: [];
  edit: [];
  print: [];
}>();
</script>
