<template>
  <base-dialog
    :visible="props.visible"
    dialogtitle="Übertragung der Stimmzettelkennung"
    icon="$information"
    confirmtext="Bestätigen"
    canceltext="Abbrechen"
    @confirm="onConfirmClicked"
    @cancel="$emit('cancel')"
  >
    <div>
      Bitte übertragen Sie die Stimmzettelkennung auf den Papier-Stimmzettel.
    </div>
    <div class="font-weight-bold text-center">
      {{ currentUserTeamName }} {{ stimmzettelkennung }}
    </div>
  </base-dialog>
</template>

<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";

import { ref, watch } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import { useStimmzettelService } from "@/composables/dse/stimmzettelService.ts";
import { useStimmzettelUtils } from "@/composables/dse/stimmzettelUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { currentUserWahlbezirkID, currentUserTeamName } = useUserStore();
const { getNextStimmzettelNumber } = useStimmzettelUtils();
const { saveStimmzettel } = useStimmzettelService();

const props = defineProps<{
  visible: boolean;
  wahlID: string;
}>();

const emit = defineEmits<{
  cancel: [];
  confirm: [];
}>();

const stimmzettelkennung = ref<number | null>(null);

watch(
  () => props.visible,
  async (newVal) => {
    if (newVal) {
      stimmzettelkennung.value = await getNextStimmzettelNumber(
        props.wahlID,
        currentUserWahlbezirkID,
        currentUserTeamName
      );
    }
  }
);

async function onConfirmClicked() {
  const newStimmzettel = {
    stimmzettelkennung: stimmzettelkennung.value,
    selectedWahlvorschlaegeOrdnungszahlen: [],
    kandidaten: [],
  } as Stimmzettel;

  await saveStimmzettel(
    props.wahlID,
    currentUserWahlbezirkID,
    currentUserTeamName,
    [newStimmzettel]
  );
  emit("confirm");
}
</script>
