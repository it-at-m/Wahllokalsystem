<template>
  <v-container>
    <v-col class="text-center">
      <h2>This view shows how communication with backend-services will work</h2>
    </v-col>
    <v-responsive class="mx-auto">
      <v-col class="text-center">
        <h4>Get or Post a Broadcast message:</h4>
        <br />
        <v-text-field
          v-model="messageInput"
          class="ml-auto mr-auto"
          width="350"
          clearable
          label="ID"
        ></v-text-field>
        <v-btn @click="postMessage(['wbz-1', 'wbz-2'])"
          >post message with fetch utils
        </v-btn>
        <p v-if="errors.post">{{ errors.post }}</p>
        <br />
        <br />
        <v-btn @click="getMessage('wbz-1')"
          >get message with fetch utils
        </v-btn>
        <pre v-if="message"> {{ message }} </pre>
        <p v-if="errors.get">{{ errors.get }}</p>
        <p v-if="errors.read">{{ errors.read }}</p>
      </v-col>
    </v-responsive>
  </v-container>
</template>

<script setup lang="ts">
import type { BroadcastMessageToRead } from "@/api/wls-clients/broadcast-service/BroadcastMessageToRead";

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
} from "@/api/wls-clients/broadcast-service/broadcast-client";

const messageInput = ref("Broadcast Message");
const message = ref("");
const errors = ref({ get: "", post: "", read: "" });
let messageId = "";

async function getMessage(wahlbezirkID: string) {
  errors.value.get = "";
  message.value = "";
  try {
    const response = await getBroadcastMessage(wahlbezirkID);
    const content: BroadcastMessageToRead = await response.json();
    message.value = content.nachricht;
    messageId = content.oid;

    await broadcastMessageRead(messageId).catch(() => {
      errors.value.read =
        "Es ist ein Fehler beim Lesen der Nachricht aufgetreten";
    });
  } catch (e) {
    errors.value.get = (e as Error).message;
  }
}

function postMessage(wahlbezirkIDs: string[]) {
  errors.value.post = "";
  postBroadcastMessage(wahlbezirkIDs, messageInput.value).catch((e) => {
    errors.value.post = e.message;
  });
  messageInput.value = "";
}
</script>
