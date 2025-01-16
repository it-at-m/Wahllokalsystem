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
        <v-btn @click="postBroadcastMessage(messageInput, ['wbz-1', 'wbz-2'])"
          >post message with fetch utils
        </v-btn>
        <br />
        <br />
        <v-btn @click="getBroadcastMessage('wbz-1')"
          >get message with fetch utils
        </v-btn>
        <br />
        <br />
        <pre v-if="messageToShow"> {{ messageToShow }} </pre>
        <p v-if="errorToShow">{{ errorToShow }}</p>
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
  ResponseError,
} from "@/api/wls-clients/generated-broadcast-api";

const messageInput = ref("I am a message");
const messageToShow = ref("");
const errorToShow = ref("");

const broadcastCA = new BroadcastControllerApi();

async function getBroadcastMessage(wahlbezirkID: string) {
  clearDisplayedValues();

  const getParams: GetMessageRequest = { wahlbezirkID };
  broadcastCA
    .getMessage(getParams)
    .then((content) => {
      // TODO: der 204 code kann nicht abgefangen werden, weil hier nicht ein response objekt sondern immer ein MessageDTO zurückgegeben wird
      // ggf kann "getMessageRaw" in Zeile 193 ergänzt werden
      const nachrichtID = content.oid;
      const deleteParams: DeleteMessageRequest = { nachrichtID };
      broadcastCA
        .deleteMessage(deleteParams, postConfig(nachrichtID))
        .catch(() => {
          errorToShow.value =
            "Es ist ein Fehler beim Lesen der Nachricht aufgetreten";
        });
      messageToShow.value = content.nachricht;
    })
    .catch((responseError: ResponseError) => {
      console.log(responseError);
      errorToShow.value = responseError.toString();
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
    // TODO issue erstellen für phase 3, dass hier nicht 2x der body übergeben werden muss
    .broadcast(postParams, postConfig(broadcastMessageDTO))
    .then(() => {
      errorToShow.value = "";
    })
    .catch((responseError: ResponseError) => {
      // todo: wls error zurückgeben (auch beim get!!)
      errorToShow.value = responseError.toString();
    });
  messageInput.value = "";
}

function clearDisplayedValues() {
  errorToShow.value = "";
  messageToShow.value = "";
}
</script>
