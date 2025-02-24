<template>
  <div class="text-center">
    <v-dialog
      v-model="dialog"
      max-width="400"
      height="150"
      persistent
    >
      <template #activator="{ props: dialogActivator }">
        <v-tooltip text="sync offline data">
          <template #activator="{ props: tooltipActivator }">
            <v-btn
              v-bind="mergeProps(dialogActivator, tooltipActivator)"
              icon="$reload"
              class="px-0"
              size="x-small"
              color="primary"
              @click="synchronizeOfflineData"
            >
            </v-btn>
          </template>
        </v-tooltip>
      </template>

      <v-card
        title="Synchronizing"
        :text="statusText"
      ></v-card>
    </v-dialog>
  </div>
</template>
<script setup lang="ts">
import type IdbObject from "@/types/wlsTypes/IdbObject";

import axios from "axios";
import localforage from "localforage";
import { mergeProps, ref } from "vue";
import { VBtn, VCard, VDialog, VTooltip } from "vuetify/components";

import { basicPostConfig } from "@/api/axios-utils";
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { useInterval } from "@/composables/useInterval";

const dialog = ref(false);
const statusText = ref("");

// todo: synchronizing activated via click. uncomment to activate periodically
/*
useInterval(() => {
  synchronizeOfflineData();
}, 10000); // updates every 10 seconds
*/

async function synchronizeOfflineData() {
  dialog.value = true;
  statusText.value = "Gathering dirty data in IDB...";

  const dataToSync: IdbObject[] = [];
  // gather dirty data
  return localforage
    .iterate((value: IdbObject) => {
      if (value.dirty) {
        dataToSync.push(value);
      }
    })
    .then(async () => {
      if (dataToSync.length > 0) {
        statusText.value = "... syncing ... ";
        for (const element of dataToSync) {
          await axios
            .request(basicPostConfig(element.url, undefined, element.data))
            .then(() => {
              statusText.value = "data has been synchronized successfully";
            })
            .catch(() => {
              statusText.value = "offline. try again in a few secs";
            });
        }
      } else {
        statusText.value = "no dirty data found";
      }
      await new Promise((resolve) => setTimeout(resolve, 2000)); // wait to show message
      dialog.value = false;
    })
    .catch(async () => {
      await new Promise((resolve) => setTimeout(resolve, 2000)); // wait to show message
      dialog.value = false;
    });
}
</script>
