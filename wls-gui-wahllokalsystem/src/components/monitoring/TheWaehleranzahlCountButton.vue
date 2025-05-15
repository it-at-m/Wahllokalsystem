<template>
  <v-btn
    class="bg-white mr-5"
    color="primary"
    prepend-icon="$addCircle"
    @click="onWaehleranzahlClicked"
  >
    {{ waehler }} Wähler*innen
  </v-btn>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { VBtn } from "vuetify/components";

import { useMonitoringStore } from "@/stores/monitoringStore.ts";

const { waehler } = storeToRefs(useMonitoringStore());
const { increaseWaehlerByOne } = useMonitoringStore();

function onWaehleranzahlClicked() {
  increaseWaehlerByOne();
}
</script>
