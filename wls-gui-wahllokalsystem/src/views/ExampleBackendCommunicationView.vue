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
        <v-btn
          class="get-message-btn"
          @click="getMessage('wbz-1')"
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
import {
  VBtn,
  VCol,
  VContainer,
  VResponsive,
  VTextField,
} from "vuetify/components";

import { useBroadcastMessage } from "@/composables/wlsClients/broadcastService/broadcastMessage";

const { messageInput, message, errors, getMessage, postMessage } =
  useBroadcastMessage();
</script>
