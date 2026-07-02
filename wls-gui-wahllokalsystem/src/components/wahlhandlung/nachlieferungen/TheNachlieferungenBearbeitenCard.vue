<template>
  <v-card>
    <v-card-title>
      Anzahl der nach
      {{
        toTimeWithHoursAndOptionalMinutes(
          createTodayWithTime(fruehesteSchliessungsuhrzeit)
        )
      }}
      Uhr nachgelieferten Wahlbriefe
    </v-card-title>
    <v-card-text>
      <base-feedback-card
        title="Nachlieferungen bearbeiten"
        type="information"
        class="mb-2"
      >
        <div>
          Bitte warten Sie auf die Lieferung aus der
          {{
            toTimeWithHoursAndOptionalMinutes(
              createTodayWithTime(fruehesteSchliessungsuhrzeit)
            )
          }}
          Uhr Leerung.
        </div>
      </base-feedback-card>
      <v-form
        ref="nachtraeglichUeberbrachteForm"
        v-model="nachtraeglichUeberbrachtValuesValid"
        data-test="nachtraeglichUeberbrachteForm"
      >
        <div class="d-flex flex-wrap justify-start">
          <div>
            <base-number-input
              v-model="wahlbriefdaten.nachtraeglichUeberbrachte"
              class="mr-4"
              data-test="textFieldNachtraeglichUeberbrachteAnzahl"
              label="Anzahl Wahlbriefe"
              :min-width="WIDTH"
              :max-width="WIDTH"
              :rules="[required, minNumber(0)]"
            />
          </div>
          <div>
            <base-time-input
              v-model="wahlbriefdaten.zeitNachtraeglichUeberbrachte"
              class="mr-4"
              :min-width="WIDTH"
              :max-width="WIDTH"
              data-test="timeInputZeitNachtraeglichUeberbrachteAnzahl"
              :rules="[
                required,
                timeNotInFuture,
                timeGreaterOrEqual(fruehesteSchliessungsuhrzeit),
              ]"
            />
          </div>
        </div>
      </v-form>
      <beanstandete-wahlbriefe-tabs
        title-tab-one="Wahlbriefezulassungen aktualisieren"
        :triggers-navigation="false"
        :nachtraeglich-ueberbracht-valid="nachtraeglichUeberbrachtValuesValid"
        @save="onSaveClicked"
      />
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { Wahlbriefdaten } from "@/types/briefwahl/Wahlbriefdaten.ts";

import { storeToRefs } from "pinia";
import { onMounted, ref } from "vue";

import BaseFeedbackCard from "@/components/common/cards/BaseFeedbackCard.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import BaseTimeInput from "@/components/common/inputs/BaseTimeInput.vue";
import BeanstandeteWahlbriefeTabs from "@/components/wahlhandlung/beanstandeteWahlbriefe/BeanstandeteWahlbriefeTabs.vue";
import { useBriefwahlService } from "@/composables/briefwahl/briefwahlService.ts";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";
import { useRules } from "@/composables/common/rules.ts";
import { useNavigationService } from "@/composables/navigation/navigationService.ts";
import router from "@/plugins/router.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";

const { fruehesteSchliessungsuhrzeit } = storeToRefs(useInfomanagementStore());
const { toTimeWithHoursAndOptionalMinutes } = useDateTimeFormatter();
const { createTodayWithTime } = useDateTimeUtils();
const { getWahlbriefdaten, postWahlbriefdaten } = useBriefwahlService();
const { required, timeNotInFuture, timeGreaterOrEqual, minNumber } = useRules();
const { currentUserWahlbezirkID } = storeToRefs(useUserStore());
const { getNextRoute } = useNavigationService();
const { isNachlieferungenBearbeitenErfasst } = storeToRefs(useWorkflowStore());

const WIDTH = 300;

const nachtraeglichUeberbrachtValuesValid = ref<null | boolean>(null);

const wahlbriefdaten = ref<Wahlbriefdaten>({
  wahlbriefe: undefined,
  verzeichnisseUngueltige: undefined,
  nachtraege: undefined,
  nachtraeglichUeberbrachte: undefined,
  zeitNachtraeglichUeberbrachte: undefined,
});

onMounted(async () => {
  wahlbriefdaten.value = await getWahlbriefdaten(currentUserWahlbezirkID.value);
});

async function onSaveClicked() {
  await postWahlbriefdaten(currentUserWahlbezirkID.value, wahlbriefdaten.value);
  isNachlieferungenBearbeitenErfasst.value = true;
  await router.push(getNextRoute());
}
</script>
