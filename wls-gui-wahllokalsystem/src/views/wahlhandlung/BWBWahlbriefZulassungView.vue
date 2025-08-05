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
        prepend-icon="$edit"
      >
        Wahlbriefe zulassen oder zurückweisen
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
        <the-beanstandete-wahlbriefe-erfassen-card />
      </v-tabs-window-item>
      <v-tabs-window-item value="two">
        <the-beanstandete-wahlbriefe-beschlussergebnis />
      </v-tabs-window-item>
    </v-tabs-window>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { VTab, VTabs, VTabsWindow, VTabsWindowItem } from "vuetify/components";

import TheBeanstandeteWahlbriefeErfassenCard from "@/components/wahlhandlung/beanstandeteWahlbriefe/TheBeanstandeteWahlbriefeErfassenCard.vue";
import TheBeanstandeteWahlbriefeBeschlussergebnis from "@/components/wahlvorbereitung/beanstandeteWahlbriefe/TheBeanstandeteWahlbriefeBeschlussergebnis.vue";

const tab = ref(null);
</script>
