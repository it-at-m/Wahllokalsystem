<template>
  <v-container>
    <v-col class="text-center">
      <h2>This view shows how communication with backend-services will work</h2>
    </v-col>
    <v-col class="text-center">
      <v-btn @click="getApi"> make api call</v-btn>
    </v-col>
    <v-col>
      <pre v-if="elements"> {{ elements }} </pre>
      <p v-else>{{ error }}</p>
    </v-col>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { VBtn, VCol, VContainer } from "vuetify/components";

import { useSnackbarStore } from "@/stores/snackbar";

const snackbarStore = useSnackbarStore();
const elements = ref(null);
const error = ref("");

const GET_BROADCAST = "/api/broadcast-service/businessActions/getMessage/wbz-1";
const GET_WAHLVORSTAND =
  "/api/wahlvorstand-service/businessActions/wahlvorstand/wahlbezirkID";

function getApi() {
  fetch(GET_BROADCAST)
    .then((response) => {
      if (response.status == 204) {
        console.log(response);
        throw new Error("Es konnten keine Daten gefunden werden. (204)");
      }
      if (!response.ok) {
        switch (response.status) {
          case 401:
            throw new Error("Der Nutzer hat nicht die benötigten Rechte (401)");
          case 404:
            throw new Error("Ressource nicht gefunden (404)");
          case 431:
            throw new Error("Request Header Fields Too Large (431)");
          case 500:
            throw new Error("Interner Serverfehler (500)");
          default:
            throw new Error("Ein Fehler ist aufgetreten: ${response.status}");
        }
      }
      console.log(response);
      return response.json();
    })
    .then((data) => (elements.value = data))
    .catch((err) => {
      error.value = "Es gibt keine anzuzeigenden Daten";
      snackbarStore.showMessage(err); // todo: show message as error not info
    });
}
</script>
