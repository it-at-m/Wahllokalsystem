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
        Wahlbriefezulassungen aktualisieren
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
        <the-beanstandete-wahlbriefe-aktualisieren-card
          :nachtraeglich-ueberbracht-valid="props.nachtraeglichUeberbrachtValid"
          @save="$emit('save')"
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

import TheBeanstandeteWahlbriefeBeschlussergebnis from "@/components/wahlhandlung/beanstandeteWahlbriefe/TheBeanstandeteWahlbriefeBeschlussergebnis.vue";
import TheBeanstandeteWahlbriefeAktualisierenCard from "@/components/wahlhandlung/nachlieferungen/TheBeanstandeteWahlbriefeAktualisierenCard.vue";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { beanstandeteWahlbriefeState } = storeToRefs(useWahlenStore());
const tab = ref(null);

const props = defineProps<{
  nachtraeglichUeberbrachtValid: boolean | null;
}>();

defineEmits<{
  save: [];
}>();
</script>
