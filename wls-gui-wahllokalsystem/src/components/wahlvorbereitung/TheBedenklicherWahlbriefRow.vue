<template>
  <v-row
    class="my-2"
    align="center"
  >
    <v-col class="font-weight-bold"> Wahlschein </v-col>
    <v-col
      v-for="wahl in wahlen"
      class="font-weight-bold"
    >
      Stimmzettelumschlag für {{ wahl.name }}
    </v-col>
    <v-col cols="1">
      <v-row
        align="center"
        justify="space-between"
        class="px-2"
      >
        <v-btn
          icon="$delete"
          size="x-small"
        />
        <v-btn
          icon="$save"
          size="x-small"
        />
      </v-row>
    </v-col>
  </v-row>
</template>
<script setup lang="ts">
import { storeToRefs } from "pinia";
import { onMounted } from "vue";
import { VBtn, VCol, VRow } from "vuetify/components";

import { useBriefwahlService } from "@/composables/briefwahl/briefwahlService.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { wahlen } = storeToRefs(useWahlenStore());
const { getBeanstandeteWahlbriefe } = useBriefwahlService();

onMounted(() => {
  const briefe = getBeanstandeteWahlbriefe();
  console.log(briefe);
});
</script>
