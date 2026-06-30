<template>
  <div>
    <v-card-text>
      <v-form
        v-model="isFormValid"
        validate-on="input"
      >
        <base-stimmzettel-beschlussfassung-rows
          v-model:bedenkliche-stimmzettel="bedenklicheStimmzettel"
        />
      </v-form>
      <base-feedback-card
        title="Bearbeitungshinweis"
        type="information"
        class="mb-2 mt-5"
      >
        <div>
          <p class="mb-2">
            <strong>Gültige</strong> und
            <strong>teilweise gültige</strong> Stimmzettel sind bei Stapel
            <strong>a</strong>, <strong>b</strong> und <strong>c</strong> zu
            erfassen und anschließend in die Wahlverhandlungstasche zu legen.
          </p>
          <p>
            <strong>Ungültige</strong> Stimmzettel sind bei Stapel
            <strong>d</strong> zu erfassen und anschließend in die
            Wahlverhandlungstasche zu legen.
          </p>
        </div>
      </base-feedback-card>
    </v-card-text>
    <v-card-actions>
      <base-text-button
        prepend-icon="$add"
        @click="onAddBedenklicherStimmzettelClicked()"
        >Neuen Beschluss erfassen</base-text-button
      >
      <base-wls-button-save
        :loading="isSaving"
        :disabled="isMBWAuszaehlungDone || !isFormValid"
        :save-text="SAVE_CONTINUE"
        @click="onSave"
      />
    </v-card-actions>
  </div>
</template>

<script setup lang="ts">
import type { BedenklicherStimmzettel } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/BedenklicherStimmzettel.ts";

import { computed, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import BaseFeedbackCard from "@/components/common/cards/BaseFeedbackCard.vue";
import BaseStimmzettelBeschlussfassungRows from "@/components/ergebnismeldung/MBW/stapelE/BaseStimmzettelBeschlussfassungRows.vue";
import { useLogging } from "@/composables/common/logging.ts";
import { useBedenklicheStimmzettelService } from "@/composables/ergebnismeldung/MBW/bedenklicheStimmzettelService.ts";
import { useCommonNavigationService } from "@/composables/navigation/commonNavigationService.ts";
import { SAVE_CONTINUE } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { MbwStepsEnum } from "../../../../types/navigation/MbwStepsEnum.ts";

const { setStepDone, isElectionFinished } = useWorkflowStore();
const { getWahlbezirkIdFromWahlMetaDataByWahlId } = useUserStore();
const route = useRoute();
const router = useRouter();
const { getNextRoute } = useCommonNavigationService();
const { logError } = useLogging("theStimmzettelBeschlussfassungTable");
const { saveBedenklicheStimmzettel } = useBedenklicheStimmzettelService();

const bedenklicheStimmzettel = defineModel<BedenklicherStimmzettel[]>(
  "bedenklicheStimmzettel",
  {
    required: true,
  }
);

const isSaving = ref(false);
const isFormValid = ref<boolean>(true);

const wahlID = route.params.wahlId as string;
const wahlbezirkID = getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID);

const isMBWAuszaehlungDone = computed(() =>
  isElectionFinished(wahlID, wahlbezirkID ?? "")
);

function onAddBedenklicherStimmzettelClicked() {
  const defaultBedenklicherStimmzettelToAdd: BedenklicherStimmzettel = {
    orderIndex: bedenklicheStimmzettel.value.length + 1,
    supplements: [],
    validity: undefined,
  };
  bedenklicheStimmzettel.value.push(defaultBedenklicherStimmzettelToAdd);
}
const emit = defineEmits<{
  formValid: [newValue: boolean];
}>();

watch(isFormValid, () => {
  emit("formValid", isFormValid.value);
});

async function onSave() {
  try {
    isSaving.value = true;
    if (wahlbezirkID) {
      await saveBedenklicheStimmzettel(
        wahlID,
        wahlbezirkID,
        bedenklicheStimmzettel.value,
        true
      );
      setStepDone(wahlID, wahlbezirkID, MbwStepsEnum.MBW_STAPEL_E);
      await router.push(getNextRoute());
    }
  } catch (error) {
    logError("Fehler beim Speichern der bedenklichen Stimmzettel: ", error);
    throw error;
  } finally {
    isSaving.value = false;
  }
}
</script>
