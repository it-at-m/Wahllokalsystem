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
import { VContainer } from "vuetify/components";

import { checkHealth } from "@/api/health-client";
import { getUser } from "@/api/user-client.ts";
import { useBroadcastCronjobService } from "@/composables/broadcast/broadcastCronjobService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";
import { HealthState } from "@/types/HealthState";
import { User, UserLocalDevelopment } from "@/types/User.ts";

const wahlvorstandStore = useWahlvorstandStore();
const { startBroadcastMessageInterval } = useBroadcastCronjobService();
const status = ref("DOWN");
const wahlenStore = useWahlenStore();
const userStore = useUserStore();

onMounted(() => {
  loadUser();
  checkHealth().then((content: HealthState) => (status.value = content.status));
});

function loadUser(): void {
  getUser()
    .then((user: User) => userStore.setUser(user))
    .catch(() => {
      // No user info received, so fallback
      if (import.meta.env.DEV) {
        userStore.setUser(UserLocalDevelopment());
      } else {
        userStore.setUser(null);
      }
    })
    .then(() => {
      wahlvorstandStore.loadWahlvorstand();
      startBroadcastMessageInterval();
      wahlenStore.loadWahlen();
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
