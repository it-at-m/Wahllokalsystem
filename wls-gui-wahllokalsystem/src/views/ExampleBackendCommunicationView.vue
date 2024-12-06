<template>
  <v-container>
    <v-col class="text-center">
      <h2>This view shows how communication with backend-services will work</h2>
    </v-col>
    <v-responsive class="mx-auto">
      <v-col class="text-center">
        <h4>API Calls with fetch():</h4>
        <br />
        <v-text-field
          class="ml-auto mr-auto"
          width="350"
          v-model="messageInputFetch"
          clearable
          label="ID"
        ></v-text-field>
        <v-btn @click="postMessageFetch(['wbz-1', 'wbz-2'])"
          >post message with fetch utils
        </v-btn>
        <p v-if="errorPostFetch">{{ errorPostFetch }}</p>
        <br />
        <br />
        <v-btn @click="getMessageFetch('wbz-1')"
          >get message with fetch utils
        </v-btn>
        <pre v-if="messageFetch"> {{ messageFetch }} </pre>
        <p v-if="errorGetFetch">{{ errorGetFetch }}</p>
        <p v-if="errorReadFetch">{{ errorReadFetch }}</p>
      </v-col>
    </v-responsive>
    <v-responsive class="mx-auto">
      <v-col class="text-center">
        <h4>API Calls with axios():</h4>
        <br />
        <v-text-field
          class="ml-auto mr-auto"
          width="350"
          v-model="messageInputAxios"
          clearable
          label="ID"
        ></v-text-field>
        <v-btn @click="postMessageAxios(['wbz-1', 'wbz-2'])"
          >post message with fetch utils
        </v-btn>
        <p v-if="errorPostAxios">{{ errorPostAxios }}</p>
        <br />
        <br />
        <v-btn @click="getMessageAxios('wbz-1')"
          >get message with fetch utils
        </v-btn>
        <pre v-if="messageAxios"> {{ messageAxios }} </pre>
        <p v-if="errorGetAxios">{{ errorGetAxios }}</p>
        <p v-if="errorReadAxios">{{ errorReadAxios }}</p>
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
const messageInputFetch = ref("Broadcast Message");
const messageFetch = ref("Click Button to Load Message");
const errorGetFetch = ref("");
const errorPostFetch = ref("");
const errorReadFetch = ref("");
let messageIdFetch = "";

const messageInputAxios = ref("Broadcast Message");
const messageAxios = ref("Click Button to Load Message");
const errorGetAxios = ref("");
const errorPostAxios = ref("");
const errorReadAxios = ref("");
let messageIdAxios = "";

function getMessageFetch(wahlbezirkID: string) {
  errorGetFetch.value = "";
  messageFetch.value = "";
  getBroadcastMessage(wahlbezirkID)
    .then((response) => {
      return response.json();
    })
    .then((content: BroadcastMessageToRead) => {
      messageFetch.value = content.nachricht;
      messageIdFetch = content.oid;
      broadcastMessageRead(messageIdFetch).catch((e) => {
        errorReadFetch.value =
          "Es ist ein Fehler beim Lesen der Nachricht aufgetreten";
      });
    })
    .catch((e) => {
      errorGetFetch.value = e.message;
      snackbarStore.showMessage({ message: e, level: STATUS_INDICATORS.ERROR });
    });
}

function postMessageFetch(wahlbezirkIDs: string[]) {
  errorPostFetch.value = "";
  postBroadcastMessage(wahlbezirkIDs, messageInputFetch.value).catch((e) => {
    errorPostFetch.value = e.message;
    snackbarStore.showMessage({ message: e, level: STATUS_INDICATORS.ERROR });
  });
  messageInputFetch.value = "";
}
</script>
