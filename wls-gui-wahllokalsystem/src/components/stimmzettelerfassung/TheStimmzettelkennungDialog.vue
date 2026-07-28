<template>
  <base-dialog
    :visible="props.visible"
    dialogtitle="Übertragung der Stimmzettelkennung"
    icon="$information"
    confirmtext="Bestätigen"
    canceltext="Abbrechen"
    @confirm="onConfirmClicked"
    @cancel="onCancelClicked"
  >
    <div>
      Bitte übertragen Sie die Stimmzettelkennung auf den Papier-Stimmzettel.
    </div>
    <div>{{ currentUserTeamName }}{{ stimmzettelkennung }}</div>
  </base-dialog>
</template>

<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";

import { onMounted, ref } from "vue";

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

const stimmzettelkennung = ref<number | null>(null);

onMounted(async () => {
  stimmzettelkennung.value = await getNextStimmzettelNumber(
    props.wahlID,
    currentUserWahlbezirkID,
    currentUserTeamName
  );
});

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
}

function onCancelClicked() {
  //close dialog
}
</script>
