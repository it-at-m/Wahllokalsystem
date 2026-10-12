<template>
  <div>
    <the-mbw-dse-niederschrift-card
      v-if="isDseAktiv"
      :wahlbezirk-i-d="currentUserWahlbezirkID"
      :wahl-i-d="wahlID"
      :ereignisse="ereignisse"
      :is-sending-niederschrift="isSendingNiederschrift"
      :is-korrigieren-valid="isKorrigierenValid"
      :is-drucken-active="isDruckenActive"
      :is-drucken-loading="isDruckenLoading"
      :is-senden-active="isSendenActive"
      :is-beschlussentscheidungen-drucken-loading="
        isBeschlussentscheidungenDruckenLoading
      "
      @save="onSendenClicked"
      @edit="onKorrigierenClicked"
      @print-beschlussentscheidungen="onBeschlussentscheidungenDruckenClicked"
      @print="onDruckenClicked"
    />
    <the-mbw-stapel-niederschrift-card
      v-else
      :wahlbezirk-i-d="currentUserWahlbezirkID"
      :wahl-i-d="wahlID"
      :ereignisse="ereignisse"
      :is-sending-niederschrift="isSendingNiederschrift"
      :is-korrigieren-valid="isKorrigierenValid"
      :is-drucken-active="isDruckenActive"
      :is-drucken-loading="isDruckenLoading"
      :is-senden-active="isSendenActive"
      @save="onSendenClicked"
      @edit="onKorrigierenClicked"
      @print="onDruckenClicked"
    />
    <offline-syncer-dialog
      :is-dialog-visible="isOfflineSyncDialogVisible"
      @sync-success="onSyncSuccess"
      @sync-error="onSyncError"
    />
    <base-dialog
      :visible="isSyncErrorDialogVisible"
      dialogtitle="Fehler bei der Synchronisation"
      confirmtext="Hinweis schließen"
      icon="$information"
      @confirm="isSyncErrorDialogVisible = false"
    >
      <div class="mb-4">
        Bei der Synchronisation der Offline-Daten ist ein Fehler aufgetreten. Um
        zu verhindern, dass beim Senden der Niederschrift unvollständige Daten
        verschickt werden, wurde der Vorgang abgebrochen.
      </div>
    </base-dialog>
    <base-beschlussentscheidungen-drucken-info-dialog
      v-model="isBeschlussentscheidungenDruckenDialogVisble"
    />
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { useRoute, useRouter } from "vue-router";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import BaseBeschlussentscheidungenDruckenInfoDialog from "@/components/dse/beschlussfassung/BaseBeschlussentscheidungenDruckenInfoDialog.vue";
import TheMbwDseNiederschriftCard from "@/components/ergebnismeldung/MBW/TheMbwDseNiederschriftCard.vue";
import TheMbwStapelNiederschriftCard from "@/components/ergebnismeldung/MBW/TheMbwStapelNiederschriftCard.vue";
import OfflineSyncerDialog from "@/components/wlsComponents/OfflineSyncerDialog.vue";
import { useMbwNiederschriftViewUtils } from "@/composables/ergebnismeldung/MBW/mbwNiederschriftViewUtils.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";

const route = useRoute();
const router = useRouter();
const { isDseAktiv } = storeToRefs(useInfomanagementStore());

const currentUserWahlbezirkID = route.params.wahlbezirkId as string;
const wahlID = route.params.wahlId as string;
const {
  ereignisse,
  isBeschlussentscheidungenDruckenLoading,
  isDruckenActive,
  isDruckenLoading,
  isKorrigierenValid,
  isOfflineSyncDialogVisible,
  isSendenActive,
  isSendingNiederschrift,
  isSyncErrorDialogVisible,
  isBeschlussentscheidungenDruckenDialogVisble,
  onBeschlussentscheidungenDruckenClicked,
  onDruckenClicked,
  onKorrigierenClicked,
  onSendenClicked,
  onSyncError,
  onSyncSuccess,
} = useMbwNiederschriftViewUtils(wahlID, currentUserWahlbezirkID, router);
</script>
