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
    <div>
      {{ stimmzettelkennung }}
    </div>
  </base-dialog>
</template>

<script setup lang="ts">
import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import {computed} from "vue";
import {useUserStore} from "@/stores/userStore.ts";

const { currentUserTeamName } = useUserStore();

const props = defineProps<{
  visible: boolean;
}>();

const stimmzettelkennung = computed(() => {
  return currentUserTeamName;
});

function onConfirmClicked() {

}

function onCancelClicked() {

}
</script>