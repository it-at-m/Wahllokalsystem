<template>
  <base-dialog
    :visible="visible"
    dialogtitle="Anwesenheit kontrollieren"
    :confirmtext="BUTTON_TITLE_CONFIRM"
    :canceltext="BUTTON_TITLE_CANCEL"
    icon="$information"
    @confirm="onConfirmClicked"
    @cancel="onCancelClicked"
  >
    <div>
      Es ist {{ timeToCheck }} Uhr. Beim Schichtwechsel können sich die
      Anwesenheiten ändern.
    </div>
    <div>
      Klicken Sie auf "{{ BUTTON_TITLE_CONFIRM }}", um den Schichtwechsel sofort
      einzutragen. Oder klicken Sie auf "{{ BUTTON_TITLE_CANCEL }}" und
      erledigen es später.
    </div>
  </base-dialog>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, onMounted, onUnmounted, ref } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useDateOfActionTimeout } from "@/composables/scheduler/dateOfActionTimeout.ts";
import { ROUTE_WAHLVORSTAND } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";

const { dateTimeToCheckAnwesenheit } = storeToRefs(useInfomanagementStore());
const { resetAllAnwesenheiten } = useWahlvorstandStore();
const { toTimeWithHoursAndOptionalMinutes } = useDateTimeFormatter();

const { setupTimer, clearTimer } = useDateOfActionTimeout(
  "Anwesenheitscheck Timeout",
  dateTimeToCheckAnwesenheit,
  showDialog
);

const BUTTON_TITLE_CONFIRM = "Anwesenheit erfassen";
const BUTTON_TITLE_CANCEL = "Bleiben";

const visible = ref(false);

const timeToCheck = computed(() => {
  return dateTimeToCheckAnwesenheit.value
    ? toTimeWithHoursAndOptionalMinutes(dateTimeToCheckAnwesenheit.value)
    : 0;
});

onMounted(() => {
  setupTimer();
});

onUnmounted(() => {
  clearTimer();
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
