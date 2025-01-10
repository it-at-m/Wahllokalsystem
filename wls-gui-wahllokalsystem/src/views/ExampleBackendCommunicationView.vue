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
          data-test="messageInput"
          v-model="messageInput"
          class="ml-auto mr-auto"
          width="350"
          clearable
          label="ID"
        ></v-text-field>
        <v-btn
          data-test="postMessageBtn"
          @click="postBroadcastMessage(messageInput, ['wbz-1', 'wbz-2'])"
          >post message with fetch utils
        </v-btn>
        <br />
        <br />
        <v-btn
          data-test="getMessageBtn"
          @click="getBroadcastMessage('wbz-1')"
          >get message with fetch utils
        </v-btn>
        <br />
        <br />
        <pre
          data-test="messageToShow"
          v-if="messageToShow"
          >{{ messageToShow }}
        </pre>
        <p
          data-test="errorToShow"
          v-if="errorToShow"
        >
          {{ errorToShow }}
        </p>
      </v-col>
    </v-responsive>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from "vue";
import {
  VBtn,
  VCol,
  VContainer,
  VResponsive,
  VTextField,
} from "vuetify/components";

import { useBroadcastService } from "@/composables/wlsClients/broadcastService/useBroadcastService";

const { getMessage, postMessage } = useBroadcastService();

const messageInput = ref("I am a message");
const messageToShow = ref("");
const errorToShow = ref("");

async function getBroadcastMessage(id: string) {
  clearDisplayedValues();
  const { message, error } = await getMessage(id);
  errorToShow.value = error;
  messageToShow.value = message;
}

async function postBroadcastMessage(message: string, ids: string[]) {
  clearDisplayedValues();
  const { error } = await postMessage(message, ids);
  errorToShow.value = error;
  messageInput.value = "";
}

function clearDisplayedValues() {
  errorToShow.value = "";
  messageToShow.value = "";
}
</script>
