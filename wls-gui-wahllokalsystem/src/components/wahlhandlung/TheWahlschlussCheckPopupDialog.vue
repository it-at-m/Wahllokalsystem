<template>
  <base-dialog
    :visible="visible"
    :dialogtitle="titleBasedOnWahlbezirksArt"
    :confirmtext="confirmTextBasedOnWahlbezirksArt"
    canceltext="Bleiben"
    icon="$information"
    @confirm="onConfirmClicked"
    @cancel="onCancelClicked"
  >
    <div>
      {{ descriptionBasedOnWahlbezirksArt }}
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
import { ROUTE_STIMMABGABE, ROUTE_WAHLVORSTAND } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { dateTimeToCheckWahlschluss } = storeToRefs(useInfomanagementStore());
const { toTimeWithHoursAndOptionalMinutes } = useDateTimeFormatter();
const { isUWB } = storeToRefs(useUserStore());

const WAHLSCHLUSS_ERFASSEN = "Wahlschluss erfassen";
const AUSZAEHLUNG_BEGINNEN = "Auszählung beginnen";
const URNENOEFFNUNG_ERFASSEN = "Wahlurnenöffnung erfassen";

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

const titleBasedOnWahlbezirksArt = computed(() =>
  isUWB.value ? WAHLSCHLUSS_ERFASSEN : AUSZAEHLUNG_BEGINNEN
);

const confirmTextBasedOnWahlbezirksArt = computed(() =>
  isUWB.value ? WAHLSCHLUSS_ERFASSEN : URNENOEFFNUNG_ERFASSEN
);

const descriptionBasedOnWahlbezirksArt = computed(() =>
  isUWB.value
    ? `Es ist ${timeToCheck.value} Uhr und der Wahlschluss sollte erfasst werden.`
    : `Es ist ${timeToCheck.value} Uhr und mit der Auszählung kann begonnen werden.`
);

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
  if (isUWB.value) {
    router.push({ name: ROUTE_STIMMABGABE });
  } else {
    router.push({ name: ROUTE_WAHLVORSTAND });
  }
  closeDialog();
}

function showDialog() {
  visible.value = true;
}
</script>
