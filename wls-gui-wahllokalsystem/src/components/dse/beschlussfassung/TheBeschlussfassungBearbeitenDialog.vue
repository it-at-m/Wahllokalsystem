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
          <the-beschluss-fassen-tab
            :stimmzettel="stimmzettelForBeschlussfassung"
          />
        </v-tabs-window-item>
        <v-tabs-window-item
          value="two"
          eager
        >
          <v-card>
            <base-stimmzettel-erfassung-card-content
              v-if="stimmzettelForBeschlussfassung"
              v-model="stimmzettelManager"
              :stimmzettel-gueltigkeit="stimmzettelGueltigkeit"
              :wahlvorschlaege="wahlvorschlaege"
              :stimmzettel="stimmzettelForBeschlussfassung"
            />
          </v-card>
        </v-tabs-window-item>
      </v-tabs-window>
      <v-spacer />
      <v-card-actions>
        <base-text-button @click="onCancelClicked">Abbrechen</base-text-button>
        <base-wls-button-save
          v-if="tab === 'one'"
          :save-text="saveButtonText"
          @click="onSaveClicked"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { storeToRefs } from "pinia";
import { computed, onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import TheBeschlussFassenTab from "@/components/dse/beschlussfassung/TheBeschlussFassenTab.vue";
import BaseStimmzettelErfassungCardContent from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelErfassungCardContent.vue";
import BaseStimmzettelkennungStrongText from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelkennungStrongText.vue";
import { useStimmzettelerfassungDialogUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelerfassungDialogUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";

const isDialogVisibleModel = defineModel("modelValue", {
  type: Boolean,
  required: false,
});

const props = defineProps<{
  stimmzettel: PersistedStimmzettel;
  wahlvorschlaege: Wahlvorschlag[];
}>();

const route = useRoute();
const wahlID = route.params.wahlId as string;
const { currentUserTeamName } = storeToRefs(useUserStore());

const { stimmzettelManager } = useStimmzettelerfassungDialogUtils(
  computed(() => props.stimmzettel.stimmzettelkennung),
  props.wahlvorschlaege,
  wahlID,
  currentUserTeamName.value
);

const emit = defineEmits<{
  cancel: [];
  save: [];
}>();

const tab = ref("one");
const stimmzettelForBeschlussfassung = ref<PersistedStimmzettel>();
const stimmzettelChanged = ref(false);

const stimmzettelGueltigkeit = computed(
  () =>
    stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmzettel.value
      .gueltigkeit
);

onMounted(() => {
  stimmzettelForBeschlussfassung.value = props.stimmzettel;
});

watch(
  () => isDialogVisibleModel.value,
  () => {
    if (isDialogVisibleModel.value) {
      tab.value = "one";
      stimmzettelChanged.value = false;
      stimmzettelForBeschlussfassung.value = props.stimmzettel;
      stimmzettelManager.setActiveStimmzettelWhenEditing(props.stimmzettel);
    }
  }
);

watch(
  () => tab.value,
  () => {
    stimmzettelChanged.value =
      !(
        JSON.stringify(stimmzettelForBeschlussfassung.value) ===
        JSON.stringify(stimmzettelManager.getStimmzettelSnapshot())
      ) || stimmzettelChanged.value;
    stimmzettelForBeschlussfassung.value =
      stimmzettelManager.getStimmzettelSnapshot();
  }
);

const saveButtonText = computed(() =>
  stimmzettelChanged.value
    ? "Stimmzettel und Beschluss speichern"
    : "Beschluss speichern"
);

function onCancelClicked() {
  emit("cancel");
}

function onSaveClicked() {
  emit("save");
}
</script>
