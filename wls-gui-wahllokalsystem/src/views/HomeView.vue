<template>
  <v-container max-width="800px">
    <base-offline-loading />
  </v-container>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { VContainer } from "vuetify/components";

import { checkHealth } from "@/api/health-client";
import { getUser } from "@/api/user-client.ts";
import BaseOfflineLoading from "@/components/wlsComponents/BaseOfflineLoading.vue";
import { useBroadcastCronjobService } from "@/composables/broadcast/broadcastCronjobService.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";
import { HealthState } from "@/types/HealthState";
import { User, UserLocalDevelopment } from "@/types/User.ts";

const wahlvorstandStore = useWahlvorstandStore();
const { startBroadcastMessageInterval } = useBroadcastCronjobService();
const status = ref("DOWN");
const userStore = useUserStore();
const { initTasks, hasInitializationOfTasksCompletelyRun } =
  useTaskManagerStore();
const { loadEreignisse } = useEreignisStore();

onMounted(() => {
  if (!hasInitializationOfTasksCompletelyRun) {
    loadUser();
  }
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
      loadEreignisse();
    });
}
</script>

<style scoped>
.UP {
  color: limegreen;
}

.DOWN {
  color: lightcoral;
}
</style>
