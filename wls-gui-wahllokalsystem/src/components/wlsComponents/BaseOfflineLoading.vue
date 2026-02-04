<template>
  <v-container>
    <h1 v-if="isLoading">Offline-Daten werden heruntergeladen...</h1>
    <h1 v-else>Offline-Daten wurden heruntergeladen.</h1>
    <p
      v-if="currentlyRunningTask"
      class="my-4"
    >
      {{ currentlyRunningTask.name }}
    </p>
    <p
      v-else
      class="my-4"
    >
      Herunterladen der Daten abgeschlossen ({{ numberOfTasksFinished }} /
      {{ numberOfTasksToRun }})
    </p>
    <v-progress-linear
      :striped="isLoading"
      :max="numberOfTasksToRun"
      :model-value="numberOfTasksFinished"
    />
    <base-progress-linear
      titel="Erfolgreich heruntergeladen"
      data-test="base-progress-success"
      :is-loading="isLoading"
      :current="numberOfTasksSucceeded"
      :total="numberOfTasksToRun"
      :tasks="successfullyTasks"
      color="success"
    />
    <base-progress-linear
      titel="Fehlgeschlagen"
      data-test="base-progress-failed"
      :is-loading="isLoading"
      :current="numberOfTasksFailed"
      :total="numberOfTasksToRun"
      :tasks="failedTasks"
      color="warning"
    />
    <v-row
      justify="center"
      class="mt-4"
    >
      <base-text-button
        data-test="weiter-button"
        class="ma-4"
        prepend-icon="$continue"
        :disabled="isLoading"
        active
        @click="onContinueClicked"
        >Weiter</base-text-button
      >
      <base-button-refresh
        data-test="refresh-button"
        class="ma-4"
        :disabled="isLoading || numberOfTasksFailed === 0"
        @click="onRefreshClicked"
        >Fehlgeschlagene wiederholen</base-button-refresh
      >
    </v-row>
  </v-container>
</template>
<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, watch } from "vue";
import { useRouter } from "vue-router";

import BaseButtonRefresh from "@/components/common/buttons/BaseButtonRefresh.vue";
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseProgressLinear from "@/components/common/progressLinear/BaseProgressLinear.vue";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import { ROUTE_WAHLVORSTAND } from "@/constants.ts";
import { useInitTaskManagerStore } from "@/stores/initTaskManagerStore.ts";

const router = useRouter();

const { getNextRoute } = useNavigationUtils();

const {
  numberOfTasksToRun,
  numberOfTasksSucceeded,
  numberOfTasksFailed,
  numberOfTasksFinished,
  currentlyRunningTask,
  successfullyTasks,
  failedTasks,
  hasAllTasksRunSuccessfully,
} = storeToRefs(useInitTaskManagerStore());

const { rerunFailedTasks } = useInitTaskManagerStore();

const isLoading = computed(() => {
  return (
    successfullyTasks.value.length + failedTasks.value.length !=
    numberOfTasksToRun.value
  );
});

watch(hasAllTasksRunSuccessfully, () => {
  if (hasAllTasksRunSuccessfully.value) {
    router.push(ROUTE_WAHLVORSTAND);
  }
});

async function onContinueClicked() {
  await router.push(getNextRoute());
}

async function onRefreshClicked() {
  await rerunFailedTasks();
}
</script>
