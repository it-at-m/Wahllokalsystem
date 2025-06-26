<template>
  <base-dialog
    :visible="visible"
    dialogtitle="Anwesenheit kontrollieren"
    :confirmtext="BUTTON_TITLE_CONFIRM"
    canceltext="Bleiben"
    icon="$information"
    @confirm="onConfirmClicked"
    @cancel="onCancelClicked"
  >
    <div>
      Es ist {{ hourOfTimeToCheck }} Uhr. Beim Schichtwechsel können sich die
      Anwesenheiten ändern.
    </div>
    <div>
      Klicken Sie auf "{{ BUTTON_TITLE_CONFIRM }}", um den Schichtwechsel sofort
      einzutragen. Oder Sie klicken Sie auf "bleiben" und erledigen es später.
    </div>
  </base-dialog>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import { useDateOfActionTimeout } from "@/composables/dateOfActionTimeout.ts";
import { ROUTE_WAHLVORSTAND } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";

const { timeToCheckAnwesenheit } = storeToRefs(useInfomanagementStore());
const { resetAllAnwesenheiten } = useWahlvorstandStore();

useDateOfActionTimeout(timeToCheckAnwesenheit, showDialog);

const BUTTON_TITLE_CONFIRM = "Anwesenheit erfassen";

const visible = ref(false);

const hourOfTimeToCheck = computed(() => {
  return timeToCheckAnwesenheit.value
    ? new Date(timeToCheckAnwesenheit.value).getHours()
    : 0;
});

function closeDialog() {
  visible.value = false;
}

function onCancelClicked() {
  closeDialog();
}

function onConfirmClicked() {
  resetAllAnwesenheiten();
  router.push({ name: ROUTE_WAHLVORSTAND });
  closeDialog();
}

function showDialog() {
  visible.value = true;
}
</script>
