<template>
  <v-container>
    <v-row class="text-center">
      <v-col class="mb-4">
        <h1 class="text-h3 font-weight-bold mb-3">
          Willkommen beim Wahllokalsystem
        </h1>
        <v-icon
          icon="$home"
          size="large"
        ></v-icon>
        <p>
          Das API-Gateway ist:
          <span :class="status">{{ status }}</span>
        </p>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { VCol, VContainer, VIcon, VRow } from "vuetify/components";

import { checkHealth } from "@/api/health-client";
import { useSnackbarStore } from "@/stores/snackbar";
import HealthState from "@/types/HealthState";

const snackbarStore = useSnackbarStore();
const status = ref("DOWN");

onMounted(() => {
  checkHealth()
    .then((content: HealthState) => (status.value = content.status))
    .catch((error) => {
      snackbarStore.showMessage(error);
    });
});
</script>

<style scoped>
.UP {
  color: limegreen;
}

.DOWN {
  color: lightcoral;
}
</style>
