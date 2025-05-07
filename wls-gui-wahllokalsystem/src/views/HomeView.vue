<template>
  <v-container max-width="800px">
    <h1 v-if="progressBarActive">Offline-Daten werden heruntergeladen...</h1>
    <h1 v-else>Offline-Daten wurden heruntergeladen.</h1>
    <p v-if="currentlyRunningTask">{{ currentlyRunningTask.name }}</p>
    <p v-else>
      Herunterladen der Daten abgeschlossen ({{
        numberOfSuccessfullTasks + numberOfFailedTasks
      }}
      / {{ numberOfTasksToRun }})
    </p>
    <v-progress-linear
      color="primary"
      :striped="progressBarActive"
      :max="numberOfTasksToRun"
      :model-value="numberOfSuccessfullTasks + numberOfFailedTasks"
    />
    <p>
      Erfolgreich heruntergeladen ({{ numberOfSuccessfullTasks }} /
      {{ numberOfTasksToRun }})
    </p>
    <v-progress-linear
      color="primary"
      :striped="progressBarActive"
      :max="numberOfTasksToRun"
      :model-value="numberOfSuccessfullTasks"
    />
    <p>Fehlgeschlagen ({{ numberOfFailedTasks }} / {{ numberOfTasksToRun }})</p>
    <v-progress-linear
      color="primary"
      :striped="progressBarActive"
      :max="numberOfTasksToRun"
      :model-value="numberOfFailedTasks"
    />
  </v-container>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { VContainer, VProgressLinear } from "vuetify/components";

import { checkHealth } from "@/api/health-client";
import { getUser } from "@/api/user-client.ts";
import { useBroadcastCronjobService } from "@/composables/broadcast/broadcastCronjobService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";
import { HealthState } from "@/types/HealthState";
import { User, UserLocalDevelopment } from "@/types/User.ts";
import { useTaskManager } from "@/util/taskManager.ts";

const wahlvorstandStore = useWahlvorstandStore();
const { startBroadcastMessageInterval } = useBroadcastCronjobService();
const status = ref("DOWN");
const userStore = useUserStore();
const {
  initTasks,
  numberOfSuccessfullTasks,
  numberOfFailedTasks,
  numberOfTasksToRun,
  currentlyRunningTask,
} = useTaskManager();

onMounted(() => {
  loadUser();
  checkHealth().then((content: HealthState) => (status.value = content.status));
});

function loadUser(): void {
  getUser()
    .then((user: User) => userStore.setUser(user))
    .catch(() => {
      if (import.meta.env.DEV) {
        userStore.setUser(UserLocalDevelopment());
      } else {
        userStore.setUser(null);
      }
    })
    .then(() => {
      wahlvorstandStore.loadWahlvorstand();
      startBroadcastMessageInterval();
      initTasks();
    });
}

const progressBarActive = computed(() => {
  return (
    numberOfSuccessfullTasks.value + numberOfFailedTasks.value !=
    numberOfTasksToRun.value
  );
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
