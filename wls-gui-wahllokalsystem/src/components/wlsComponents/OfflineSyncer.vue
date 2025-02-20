<template>
  <div class="text-center pa-4">
    <v-dialog
      v-model="dialog"
      max-width="400"
      height="150"
      persistent
    >
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
import { ref } from "vue";
import { VCard, VDialog } from "vuetify/components";

import { useInterval } from "@/composables/useInterval";

const dialog = ref(false);
const statusText = ref("");

useInterval(() => {
  synchronizeOfflineData();
}, 10000); // updates every 10 seconds

async function synchronizeOfflineData() {
  dialog.value = true;
  statusText.value = "Gathering dirty data in IDB...";

  let dataToSync: IdbObject[] = [];
  // gather dirty data
  return localforage
    .iterate((value: { data: Object; url: string; dirty: boolean }) => {
      if (value.dirty) {
        dataToSync.push(value);
      }
    })
    .then(async () => {
      if (dataToSync.length > 0) {
        statusText.value = "... syncing ... ";
        for (let element = 0; element < dataToSync.length; element++) {
          const obj = dataToSync[element];
          await axios
            .request({
              url: obj.url,
              method: "POST",
              data: obj.data,
            })
            .then(() => {
              statusText.value = "data has been synchronized successfully";
            })
            .catch((e) => {
              console.log("failed to resend data. " + e);
              statusText.value = "offline. trying again in a few secs";
            });
        }
      } else {
        statusText.value = "no dirty data found";
      }
      await new Promise((resolve) => setTimeout(resolve, 2000)); // wait to show message
      dialog.value = false;
    })
    .catch(async (e) => {
      console.log(e);
      await new Promise((resolve) => setTimeout(resolve, 2000)); // wait to show message
      dialog.value = false;
    });
}
</script>
