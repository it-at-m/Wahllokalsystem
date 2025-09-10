<template>
  <v-row class="align-center">
    <v-col
      cols="3"
      class="flex-grow-1"
      >{{ title }} ({{ progress.finished }} / {{ progress.total }})</v-col
    >
    <v-col class="d-flex"
      ><v-progress-linear
        color="primary"
        :striped="progress.active"
        :max="progress.total"
        :model-value="progress.finished"
      />
    </v-col>
  </v-row>
</template>
<script setup lang="ts">
import type { Progress } from "@/types/common/Progress.ts";
import type { PropType } from "vue";

import { VCol, VProgressLinear, VRow } from "vuetify/components";

defineProps({
  progress: {
    type: Object as PropType<Progress>,
    required: true,
  },
  title: {
    type: String,
    required: true,
  },
});
</script>
