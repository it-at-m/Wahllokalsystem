<template>
  <v-card data-test="konfigurationParameterView">
    <v-card-title class="d-flex align-center">
      Konfigurationsparameter
      <base-icon-button-refresh
        class="ml-2"
        :disabled="konfigurationenAreLoading"
        @click="onRefreshClicked"
      />
    </v-card-title>
    <v-card-text>
      <v-progress-linear
        v-if="konfigurationenAreLoading"
        indeterminate
        data-test="loading-indicator"
      />
      <v-row>
        <v-col
          v-for="configParameter in konfigurationen"
          :key="configParameter.name"
          cols="12"
        >
          <base-card-config-parameter-display
            :config-parameter="configParameter"
            @click-edit="onEditClicked"
          />
        </v-col>
      </v-row>
      <base-dialog-config-parameter-display
        v-if="selectedConfigParameter"
        ref="dialogRef"
        :key="selectedConfigParameter.name"
        :config-parameter="selectedConfigParameter"
        @commit-edit="onCommitEdit"
        @cancel-edit="onCancelEdit"
      />
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { InfomanagementConfigParameter } from "@/types/config/InfomanagementConfigParameter.ts";
import type { Ref } from "vue";

import { nextTick, onMounted, ref, useTemplateRef } from "vue";
import {
  VCard,
  VCardText,
  VCardTitle,
  VCol,
  VProgressLinear,
  VRow,
} from "vuetify/components";

import BaseIconButtonRefresh from "@/components/common/BaseIconButtonRefresh.vue";
import BaseCardConfigParameterDisplay from "@/components/config-parameter/BaseCardConfigParameterDisplay.vue";
import BaseDialogConfigParameterDisplay from "@/components/config-parameter/BaseDialogConfigParameterDisplay.vue";
import { useKonfigurationService } from "@/composables/konfiguration/konfigurationService.ts";

const { getKonfigurations, saveKonfiguration } = useKonfigurationService();

const konfigurationen: Ref<InfomanagementConfigParameter[]> = ref([]);
const konfigurationenAreLoading = ref(false);
const selectedConfigParameter: Ref<InfomanagementConfigParameter | undefined> =
  ref(undefined);
const dialogRef =
  useTemplateRef<InstanceType<typeof BaseDialogConfigParameterDisplay>>(
    "dialogRef"
  );

onMounted(() => {
  loadKonfigurationen();
});

function onRefreshClicked() {
  loadKonfigurationen();
}

async function loadKonfigurationen(): Promise<void> {
  konfigurationen.value = await getKonfigurations(konfigurationenAreLoading);
}

async function onEditClicked(name: string): Promise<void> {
  selectedConfigParameter.value = konfigurationen.value.find(
    (configParameter) => configParameter.name === name
  );

  if (selectedConfigParameter.value) {
    await nextTick();
    dialogRef.value?.showDialog();
  }
}

async function onCommitEdit(
  configParameter: InfomanagementConfigParameter
): Promise<void> {
  const wasSaved = await saveKonfiguration(configParameter);

  if (wasSaved) {
    await loadKonfigurationen();
  }

  selectedConfigParameter.value = undefined;
}

function onCancelEdit(): void {
  selectedConfigParameter.value = undefined;
}
</script>
