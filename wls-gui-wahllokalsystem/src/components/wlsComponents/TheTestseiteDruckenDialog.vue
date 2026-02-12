<template>
  <base-dialog
    :visible="visible"
    dialogtitle="Testdruck"
    confirmtext="Testdruck starten"
    canceltext="Schließen"
    :cancel-disabled="cancelDisabled"
    icon="$information"
    @confirm="onConfirmClicked"
    @cancel="onCancelClicked"
  >
    <div class="d-flex flex-column ga-5">
      <div>
        Bitte nehmen Sie sich ein paar Sekunden Zeit für einen Testdruck.
      </div>
      <div class="ml-10">
        <ul>
          <li>
            Schalten Sie den Drucker ein und legen Sie ein Blatt Papier ein
          </li>
          <li>Im Anschluss starten Sie bitte den Testdruck</li>
        </ul>
      </div>
      <div>
        So können Sie sicherstellen, dass Ihr Drucker einwandfrei funktioniert.
      </div>
      <v-divider :thickness="2" />
      <div>
        Sie können den Test auch später wiederholen. Öffnen Sie hierfür das
        Hilfe-Menü (Fragezeichen-Symbol oben rechts). Dort finden Sie das Feld
        "Testdruck starten".
      </div>
      <v-divider :thickness="2" />
      <div>
        Sollten Sie Probleme beim Drucken haben, informieren Sie bitte das
        Wahlamt.
      </div>
    </div>
  </base-dialog>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { ref } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import { useHelpIconCallbacks } from "@/composables/basisdaten/helpIconCallbacks.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";

const { printTestdruck } = useHelpIconCallbacks();
const { isTestseiteGedruckt } = storeToRefs(useWorkflowStore());

const visible = ref(true);
const cancelDisabled = ref(true);

function closeDialog() {
  visible.value = false;
}

function onCancelClicked() {
  isTestseiteGedruckt.value = true;
  closeDialog();
}

function onConfirmClicked() {
  printTestdruck();
  cancelDisabled.value = false;
}
</script>
