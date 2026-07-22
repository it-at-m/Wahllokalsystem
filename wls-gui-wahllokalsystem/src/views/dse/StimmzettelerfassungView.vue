<template>
  <v-card>
    <v-card-title>Stimmzettelerfassung</v-card-title>
    <v-card-text>
      <v-skeleton-loader boilerplate type="card-avatar" class="mt-3"/>
    </v-card-text>
    <v-card-actions>
      <base-text-button :active="status!='IN_BEARBEITUNG'" @click="startErfassung">Starten</base-text-button>
      <base-text-button :active="status=='IN_BEARBEITUNG'" @click="unterbrechen">Unterbrechen</base-text-button>
      <base-text-button class="ms-auto" :active="status=='ABGESCHLOSSEN'" @click="beenden">Beenden</base-text-button>
    </v-card-actions>
  </v-card>
  <base-dialog
      dialogtext="🚧 TBD 🚧"
      dialogtitle="Stimmzettel erfassen"
      :model-value="erfassungDialog"
      cancel-disabled
      confirmtext="Ok"
      @confirm="erfassungDialog = !erfassungDialog"
  >
  </base-dialog>
  <base-dialog
      dialogtext="🚧 TBD 🚧"
      dialogtitle="Stimmzettelerfassung beenden"
      :model-value="beendenDialog"
      cancel-disabled
      confirmtext="Ok"
      @confirm="beendenDialog = !beendenDialog"
  >
  </base-dialog>
</template>
<script setup lang="ts">
import { ref } from "vue";
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";

//TeamErfassungStatus: REGISTRIERT, IN_BEARBEITUNG,    UNTERBROCHEN,    ABGESCHLOSSEN
const status = ref("REGISTRIERT");
const erfassungDialog = ref(false);
const beendenDialog = ref(false);

async function startErfassung() {
  status.value="IN_BEARBEITUNG";
  erfassungDialog.value = true;
}

async function unterbrechen() {
  status.value="UNTERBROCHEN";
}

async function beenden() {
  status.value="ABGESCHLOSSEN";
  beendenDialog.value=true;
}
</script>