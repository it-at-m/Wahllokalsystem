<template>
  <v-dialog
    v-model="isDialogVisibleModel"
    persistent
    fullscreen
  >
    <v-card style="height: 100vh; min-height: 0">
      <v-card-title>
        Erfassung Stimmzettel Nummer {{ currentUserTeamName }}
        {{ stimmzettel.stimmzettelkennung }}
      </v-card-title>
      <v-card-text
        style="min-height: 0"
        class="ga-3 d-flex"
      >
        <div
          class="flex-0-0-0"
          style="max-width: 16%"
        >
          <the-eingabehistorie-card
            :change-history="changeHistory.changeHistoryInReverseOrder.value"
          />
          <base-stimmzettel-zusammenfassung-card
            class="mt-2"
            :listenstimmen="
              stimmzettelManager.managedStimmzettel
                .wahlvorschlaegeWithListenkreuz.value
            "
            :ungueltigestimmen="
              stimmzettelManager.managedStimmzettel.stimmenSummary.value
                .ungueltigeStimmen
            "
            :direktstimmen="
              stimmzettelManager.managedStimmzettel.stimmenSummary.value
                .einzelstimmen
            "
            :reststimmen="
              stimmzettelManager.managedStimmzettel.stimmenSummary.value
                .reststimmen
            "
            :streichungen="
              stimmzettelManager.managedStimmzettel.stimmenSummary.value
                .streichungen
            "
            :gueltigkeit="'VALID'"
          />
        </div>
        <div
          class="flex-1-1 d-flex flex-column"
          style="min-height: 0; max-width: 84%"
        >
          <the-stimmzettel-command-processing-text-field
            class="flex-0-0"
            :stimmzettel-manager="stimmzettelManager"
          />
          <div style="flex: 1 1 auto; min-height: 0; display: flex">
            <the-stimmzettel-content
              v-if="true"
              :active-wahlvorschlag-id="
                wahlvorschlagIdOfLatestChangeWahlvorschlag
              "
              :active-kandidat="latestChangedKandidat"
              :wahlvorschlaege="
                stimmzettelManager.managedStimmzettel.stimmzettel.value
                  .wahlvorschlaege
              "
              style="min-height: 0; overflow-y: auto"
            />
            <!--            <div-->
            <!--              v-for="i in items"-->
            <!--              :key="i"-->
            <!--            >-->
            <!--              <div>-->
            <!--                Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed-->
            <!--                diam nonumy eirmod tempor invidunt ut labore et dolore magna-->
            <!--                aliquyam erat, sed diam voluptua. At vero eos et accusam et-->
            <!--                justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea-->
            <!--                takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum-->
            <!--                dolor sit amet, consetetur sadipscing elitr, sed diam nonumy-->
            <!--                eirmod tempor invidunt ut labore et dolore magna aliquyam erat,-->
            <!--                sed diam voluptua. At vero eos et accusam et justo duo dolores-->
            <!--                et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus-->
            <!--                est Lorem ipsum dolor sit amet.-->
            <!--              </div>-->
            <!--              <div style="height: 48px">{{ i }}.1</div>-->
            <!--              <div>{{ i }}.2</div>-->
            <!--              <div>{{ i }}.3</div>-->
            <!--              <div>{{ i }}.4</div>-->
            <!--              <div>{{ i }}.5</div>-->
            <!--              <div>{{ i }}.6</div>-->
            <!--              <div>{{ i }}.7</div>-->
            <!--              <div>{{ i }}.8</div>-->
            <!--              <div>{{ i }}.9</div>-->
            <!--              <div>{{ i }}.10</div>-->
            <!--              <div>{{ i }}.11</div>-->
            <!--            </div>-->
          </div>
        </div>
      </v-card-text>
      <v-card-actions>
        <base-text-button @click="onCancelClicked">Abbrechen</base-text-button>
        <base-wls-button-save @click="onSavedClicked" />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { PropType } from "vue";

import { storeToRefs } from "pinia";
import { computed } from "vue";
import { useRoute } from "vue-router";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import BaseStimmzettelZusammenfassungCard from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelZusammenfassungCard.vue";
import TheEingabehistorieCard from "@/components/dse/stimmzettelerfassung/TheEingabehistorieCard.vue";
import TheStimmzettelCommandProcessingTextField from "@/components/dse/stimmzettelerfassung/TheStimmzettelCommandProcessingTextField.vue";
import TheStimmzettelContent from "@/components/dse/stimmzettelerfassung/TheStimmzettelContent.vue";
import { useStimmzettelerfassungDialogUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelerfassungDialogUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";

const items = [1, 2, 3, 4, 5];

const isDialogVisibleModel = defineModel("modelValue", {
  type: Boolean,
  required: false,
});

const props = defineProps({
  stimmzettel: {
    type: Object as PropType<Stimmzettel>,
    required: true,
  },
  wahlvorschlaege: {
    type: Array as PropType<Wahlvorschlag[]>,
    required: true,
  },
});

const emit = defineEmits<{
  cancel: [];
  confirm: [stimmzettel: Stimmzettel];
}>();

const route = useRoute();
const wahlID = route.params.wahlId as string;

const { stimmzettelManager } = useStimmzettelerfassungDialogUtils(
  props.wahlvorschlaege,
  wahlID
);

const { currentUserTeamName } = storeToRefs(useUserStore());

const changeHistory = computed(
  () => stimmzettelManager.managedStimmzettel.changeHistory
);
const wahlvorschlagIdOfLatestChangeWahlvorschlag = computed<string | null>(
  () =>
    changeHistory.value.lastUsedWahlvorschlag?.value?.wahlvorschlagID ?? null
);
const latestChangedKandidat = computed<Kandidat | null>(
  () => changeHistory.value.lastUsedKandidat.value ?? null
);

function onCancelClicked() {
  emit("cancel");
}

function onSavedClicked() {
  emit("confirm", props.stimmzettel);
}
</script>
