<template>
  <v-container>
    <v-col class="text-center">
      <h2>This view shows how communication with backend-services will work</h2>
    </v-col>
    <v-responsive class="mx-auto">
      <v-col class="text-center">
        <v-text-field
          class="ml-auto mr-auto"
          width="350"
          v-model="messageInput"
          clearable
          label="ID"
        ></v-text-field>
        <v-btn @click="postMessage(['wbz-1', 'wbz-2'])"
          >post message with fetch utils
        </v-btn>
        <p v-if="errorPostFetch">{{ errorPostFetch }}</p>
        <br />
        <br />
        <v-btn @click="getMessage('wbz-1')">get message with fetch utils</v-btn>
        <pre v-if="messageFetch"> {{ messageFetch }} </pre>
        <p v-if="errorGetFetch">{{ errorGetFetch }}</p>
      </v-col>
    </v-responsive>
  </v-container>
</template>

<script setup lang="ts">
import type BroadcastMessageToRead from "@/types/BroadcastMessageToRead";

import { ref } from "vue";
import {
  VBtn,
  VCol,
  VContainer,
  VResponsive,
  VTextField,
} from "vuetify/components";

import {
  broadcastMessageRead,
  getBroadcastMessage,
  postBroadcastMessage,
} from "@/api/broadcast-client";
import { STATUS_INDICATORS } from "@/constants";
import { useSnackbarStore } from "@/stores/snackbar";

const snackbarStore = useSnackbarStore();
const messageInput = ref("Broadcast Message");
const messageFetch = ref("Click Button to Load Message");
const errorGetFetch = ref("");
const errorPostFetch = ref("");
let messageFetchId = "";

function getMessage(wahlbezirkID: string) {
  errorGetFetch.value = "";
  messageFetch.value = "";
  getBroadcastMessage(wahlbezirkID)
    .then((content: BroadcastMessageToRead) => {
      messageFetch.value = content.nachricht;
      messageFetchId = content.oid;
      broadcastMessageRead(messageFetchId);
    })
    .catch((e) => {
      errorGetFetch.value = e.message;
      snackbarStore.showMessage({ message: e, level: STATUS_INDICATORS.ERROR });
    });
}

function postMessage(wahlbezirkIDs: string[]) {
  errorPostFetch.value = "";
  postBroadcastMessage(wahlbezirkIDs, messageInput.value).catch((e) => {
    errorPostFetch.value = e.message;
    snackbarStore.showMessage(e);
  });
  messageInput.value = "";
}
</script>
