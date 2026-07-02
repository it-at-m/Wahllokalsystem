<template>
  <div>
    <v-tabs
      v-model="tab"
      bg-color="grey-lighten-3"
      slider-color="primary"
      color="primary"
      class="rounded-t border-b"
    >
      <v-tab
        value="one"
        :prepend-icon="
          beanstandeteWahlbriefeState.isBeanstandeteWahlbriefeTableValid
            ? `$valid`
            : `$edit`
        "
        data-test="wahlbriefe-zulassen-tab"
      >
        {{ titleTabDataInput }}
      </v-tab>
      <v-tab
        value="two"
        prepend-icon="$summary"
      >
        Beschlussergebnis
      </v-tab>
    </v-tabs>
    <v-tabs-window v-model="tab">
      <v-tabs-window-item value="one">
        <base-beanstandete-wahlbriefe-card
          :has-nachlieferungen="hasNachlieferungen"
          :nachtraeglich-ueberbracht-valid="nachtraeglichUeberbrachtValid"
          @save="emit('save')"
        />
      </v-tabs-window-item>
      <v-tabs-window-item value="two">
        <the-beanstandete-wahlbriefe-beschlussergebnis />
      </v-tabs-window-item>
    </v-tabs-window>
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { ref } from "vue";

import BaseBeanstandeteWahlbriefeCard from "@/components/wahlhandlung/beanstandeteWahlbriefe/BaseBeanstandeteWahlbriefeCard.vue";
import TheBeanstandeteWahlbriefeBeschlussergebnis from "@/components/wahlhandlung/beanstandeteWahlbriefe/TheBeanstandeteWahlbriefeBeschlussergebnis.vue";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { beanstandeteWahlbriefeState } = storeToRefs(useWahlenStore());
const tab = ref("one");

defineProps<{
  titleTabDataInput: string;
  hasNachlieferungen: boolean;
  nachtraeglichUeberbrachtValid?: boolean | null;
}>();

const emit = defineEmits<{
  save: [];
}>();
</script>
