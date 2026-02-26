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
        :disabled="!isSendenActive"
        :loading="isSending"
        @click="$emit('save')"
      />
      <base-button-save
        :save-text="`${title} korrigieren`"
        prepend-icon="$edit"
        :disabled="!isKorrigierenActive"
        @click="$emit('edit')"
      />
      <base-button-save
        :save-text="`${title} drucken und weiter`"
        prepend-icon="$printer"
        :disabled="!isDruckenActive"
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
  isKorrigierenActive: boolean | null | undefined;
  isDruckenActive: boolean | null | undefined;
  isDruckenLoading: boolean;
  isSendenActive: boolean;
}>();

defineEmits<{
  save: [];
  edit: [];
  print: [];
}>();
</script>
