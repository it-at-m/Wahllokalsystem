<template>
  <base-dialog
    :visible="visible"
    :dialogtitle="WAHLSCHLUSS_ERFASSEN"
    :confirmtext="WAHLSCHLUSS_ERFASSEN"
    canceltext="Bleiben"
    icon="$information"
    @confirm="onConfirmClicked"
    @cancel="onCancelClicked"
  >
    <div>
      Es ist {{ timeToCheck }} Uhr und der Wahlschluss sollte erfasst werden.
    </div>
    <div>
      Stimmen Sie der sofortigen Weiterleitung zu, oder führen Sie diese Aufgabe
      eigenverantwortlich später durch?
    </div>
  </base-dialog>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, onMounted, onUnmounted, ref } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useDateOfActionTimeout } from "@/composables/scheduler/dateOfActionTimeout.ts";
import { ROUTE_STIMMABGABE } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";

const { dateTimeToCheckWahlschluss } = storeToRefs(useInfomanagementStore());
const { toTimeWithHoursAndOptionalMinutes } = useDateTimeFormatter();

const WAHLSCHLUSS_ERFASSEN = "Wahlschluss erfassen";

const { setupTimer, clearTimer } = useDateOfActionTimeout(
  "Wahlschlusscheck Timeout",
  dateTimeToCheckWahlschluss,
  showDialog
);

const visible = ref(false);

const timeToCheck = computed(() => {
  return dateTimeToCheckWahlschluss.value
    ? toTimeWithHoursAndOptionalMinutes(dateTimeToCheckWahlschluss.value)
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
  router.push({ name: ROUTE_STIMMABGABE });
  closeDialog();
}

function showDialog() {
  visible.value = true;
}
</script>
