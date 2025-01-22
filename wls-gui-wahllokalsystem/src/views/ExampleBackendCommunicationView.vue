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
          data-test="messageInput"
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
          v-if="messageToShow"
          data-test="messageToShow"
          >{{ messageToShow }}
        </pre>
        <p
          v-if="errorToShow"
          data-test="errorToShow"
        >
          {{ errorToShow }}
        </p>
      </v-col>
    </v-responsive>
  </v-container>
</template>

<script setup lang="ts">
import type {
  BroadcastMessageDTO,
  BroadcastRequest,
  DeleteMessageRequest,
  GetMessageRequest,
} from "@/api/wls-clients/generated-broadcast-api";

import { ref } from "vue";
import {
  VBtn,
  VCol,
  VContainer,
  VResponsive,
  VTextField,
} from "vuetify/components";

import { postConfig } from "@/api/fetch-utils";
import {
  BroadcastControllerApi,
  Configuration,
  WLSError,
} from "@/api/wls-clients/generated-broadcast-api";
import { BROADCAST_SERVICE_API_URL } from "@/constants";

const messageInput = ref("I am a message");
const messageToShow = ref("");
const errorToShow = ref("");

const broadcastCA = new BroadcastControllerApi(
  new Configuration({
    basePath: BROADCAST_SERVICE_API_URL,
  })
);

async function getBroadcastMessage(wahlbezirkID: string) {
  clearDisplayedValues();

  const getParams: GetMessageRequest = { wahlbezirkID };
  broadcastCA
    .getMessage(getParams)
    .then((content) => {
      const nachrichtID = content.oid;
      const deleteParams: DeleteMessageRequest = { nachrichtID };
      broadcastCA.deleteMessage(deleteParams, postConfig()).catch(() => {
        errorToShow.value =
          "Es ist ein Fehler beim Lesen der Nachricht aufgetreten";
      });
      messageToShow.value = content.nachricht;
    })
    .catch((error: WLSError) => {
      errorToShow.value = error.message;
    });
}

async function postBroadcastMessage(
  nachricht: string,
  wahlbezirkIDs: string[]
) {
  clearDisplayedValues();

  const broadcastMessageDTO = {
    wahlbezirkIDs,
    nachricht,
  } as BroadcastMessageDTO;
  const postParams: BroadcastRequest = { broadcastMessageDTO };
  broadcastCA
    .broadcast(postParams, postConfig())
    .then(() => {
      errorToShow.value = "";
    })
    .catch((error: WLSError) => {
      errorToShow.value =
        error.service + " - " + error.message + " (Code: " + error.code + ")";
    });
  messageInput.value = "";
}

function clearDisplayedValues() {
  errorToShow.value = "";
  messageToShow.value = "";
}
</script>
