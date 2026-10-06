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
          :stimmzettelkennung="stimmzettelKennung"
          :team-name="teamID"
          compact
          class="mr-2"
        />
      </v-tabs>
      <v-tabs-window v-model="tab">
        <v-tabs-window-item value="one">
          <the-beschluss-fassen-tab
            v-model:beschluss-details="beschlussDetails"
            v-model:abstimmungsergebnis="abstimmungsergebnis"
            :stimmzettel-gueltigkeit-aus-beschluss="
              stimmzettelGueltigkeitAusBeschluss
            "
            :is-beschluss-gefasst="isBeschlussGefasst"
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
          :disabled="isBeschlussSpeichernButtonDisabled"
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
import { useTheBeschlussfassungBearbeitenDialogUtils } from "@/composables/dse/beschlussfassung/theBeschlussfassungBearbeitenDialogUtils.ts";
import { useStimmzettelerfassungDialogUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelerfassungDialogUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

const isDialogVisibleModel = defineModel("modelValue", {
  type: Boolean,
  required: false,
});

const stimmzettel = defineModel<PersistedStimmzettel | undefined>(
  "stimmzettel"
);

const props = defineProps<{
  wahlvorschlaege: Wahlvorschlag[];
}>();

const route = useRoute();
const wahlID = route.params.wahlId as string;
const { currentUserTeamName } = storeToRefs(useUserStore());

const { stimmzettelManager } = useStimmzettelerfassungDialogUtils(
  computed(() => stimmzettelKennung.value),
  props.wahlvorschlaege,
  wahlID,
  currentUserTeamName.value
);

const emit = defineEmits<{
  cancel: [];
  save: [stimmzettel: PersistedStimmzettel];
}>();

const tab = ref("one");
const stimmzettelForBeschlussfassung = ref<PersistedStimmzettel | undefined>();
const stimmzettelChanged = ref(false);

const {
  abstimmungsergebnis,
  beschlussDetails,
  stimmzettelGueltigkeitAusBeschluss,
  isBeschlussSpeichernButtonDisabled,
  isBeschlussGefasst,
} = useTheBeschlussfassungBearbeitenDialogUtils(stimmzettelForBeschlussfassung);

const stimmzettelKennung = computed(
  () => stimmzettel.value?.stimmzettelkennung ?? 0
);

const teamID = computed(() => stimmzettel.value?.teamID ?? "");

const stimmzettelGueltigkeit = computed(
  () =>
    stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmzettel.value
      .gueltigkeit
);

onMounted(() => {
  if (stimmzettel.value) {
    stimmzettelForBeschlussfassung.value = stimmzettel.value;
  }
});

watch(
  () => isDialogVisibleModel.value,
  () => {
    if (isDialogVisibleModel.value) {
      tab.value = "one";
      stimmzettelChanged.value = false;
      if (stimmzettel.value) {
        stimmzettelForBeschlussfassung.value = stimmzettel.value;
        stimmzettelManager.setActiveStimmzettelWhenEditing(stimmzettel.value);
      }
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
  if (!stimmzettelForBeschlussfassung.value) return;
  emit("save", {
    ...stimmzettelForBeschlussfassung.value,
    gueltigkeit: beschlussDetails.value.isStimmzettelGueltig
      ? StimmzettelGueltigkeitEnum.Valid
      : StimmzettelGueltigkeitEnum.Invalid,
    beschlussfassung: {
      pro: abstimmungsergebnis.value.stimmenDafuer ?? 0,
      contra: abstimmungsergebnis.value.stimmenDagegen ?? 0,
      text: beschlussDetails.value.beschlussText,
    },
  });
}
</script>
