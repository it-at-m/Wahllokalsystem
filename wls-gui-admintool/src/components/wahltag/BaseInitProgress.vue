<template>
  <div>
    <base-row-init-progress
      v-if="wahlvorschlaege"
      title="Wahlvorschlaege"
      :progress="wahlvorschlaege"
    />
    <base-row-init-progress
      v-if="referendumvorschlaege"
      title="Referendumvorschläge"
      :progress="referendumvorschlaege"
    />
    <base-row-init-progress
      v-if="awerte"
      title="AWerte"
      :progress="awerte"
    />
  </div>
</template>
<script setup lang="ts">
import type { Progress } from "@/types/common/Progress.ts";
import type { PropType } from "vue";

import BaseRowInitProgress from "@/components/wahltag/BaseRowInitProgress.vue";

defineProps({
  wahlvorschlaege: {
    type: Object as PropType<Progress>,
    required: false,
  },
  referendumvorschlaege: {
    type: Object as PropType<Progress>,
    required: false,
  },
  awerte: {
    type: Object as PropType<Progress>,
    required: false,
  },
});
</script>
