<template>
  <v-dialog
    v-model="isDialogVisibleModel"
    persistent
    fullscreen
  >
    <v-card>
      <v-tabs v-model="tab">
        <v-tab value="one">
          <v-icon
            icon="$stimmzettelBeschluss"
            size="x-large"
            class="mr-2"
          />
          Beschluss fassen
        </v-tab>
        <v-tab value="two"> Stimmzettel anzeigen und bearbeiten </v-tab>
        <v-spacer />
        <base-stimmzettelkennung-strong-text
          :stimmzettelkennung="stimmzettel.stimmzettelkennung"
          :team-name="stimmzettel.teamID"
          compact
          class="mr-2"
        />
      </v-tabs>
      <v-tabs-window v-model="tab">
        <v-tabs-window-item value="one">
          <the-beschluss-fassen-tab :stimmzettel="stimmzettel" />
        </v-tabs-window-item>
        <v-tabs-window-item
          value="two"
          eager
        >
          <v-card>
            <base-stimmzettel-erfassung-card-content
              v-model="stimmzettelManager"
              :stimmzettel-gueltigkeit="stimmzettelGueltigkeit"
              :wahlvorschlaege="wahlvorschlaege"
              :stimmzettel="stimmzettel"
            />
          </v-card>
        </v-tabs-window-item>
      </v-tabs-window>
      <v-spacer />
      <v-card-actions>
        <base-text-button @click="onCancelClicked">Abbrechen</base-text-button>
        <base-wls-button-save
          v-if="tab === 'one'"
          save-text="Beschluss speichern"
          @click="onSaveClicked"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import { storeToRefs } from "pinia";
import { computed, ref, watch } from "vue";
import { useRoute } from "vue-router";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import TheBeschlussFassenTab from "@/components/dse/beschlussfassung/TheBeschlussFassenTab.vue";
import BaseStimmzettelErfassungCardContent from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelErfassungCardContent.vue";
import BaseStimmzettelkennungStrongText from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelkennungStrongText.vue";
import { useStimmzettelerfassungDialogUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelerfassungDialogUtils.ts";
import { useWahlvorschlaegeState } from "@/composables/dse/stimmzettelerfassung/wahlvorschlaegeState.ts";
import { useUserStore } from "@/stores/userStore.ts";

const isDialogVisibleModel = defineModel("modelValue", {
  type: Boolean,
  required: false,
});

const props = defineProps<{
  stimmzettel: PersistedStimmzettel;
}>();

const route = useRoute();
const wahlID = route.params.wahlId as string;
const wahlbezirkID = route.params.wahlbezirkId as string;
const { currentUserTeamName } = storeToRefs(useUserStore());
const { wahlvorschlaege } = useWahlvorschlaegeState(wahlID, wahlbezirkID);

const { stimmzettelManager } = useStimmzettelerfassungDialogUtils(
  computed(() => props.stimmzettel.stimmzettelkennung),
  wahlvorschlaege.value,
  wahlID,
  currentUserTeamName.value
);

const emit = defineEmits<{
  cancel: [];
  save: [];
}>();

const tab = ref("one");

const stimmzettelGueltigkeit = computed(
  () =>
    stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmzettel.value
      .gueltigkeit
);

watch(isDialogVisibleModel, (isVisible) => {
  if (isVisible) {
    tab.value = "one";
  }
});

function onCancelClicked() {
  emit("cancel");
}

function onSaveClicked() {
  emit("save");
}
</script>
