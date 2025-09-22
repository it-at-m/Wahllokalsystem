<template>
  <v-card>
    <v-card-title>Gültige Stimmabgaben</v-card-title>
    <v-form v-model="isFormValid">
      <v-card-text>
        <v-table>
          <thead>
            <tr>
              <th />
              <th class="font-weight-bold">Wahlvorschlag</th>
              <th class="font-weight-bold">Stapel a: zweifelsfrei gültig</th>
              <th class="font-weight-bold">Stapel c: laut Beschluss gültig</th>
              <th class="font-weight-bold">Insgesamt</th>
            </tr>
          </thead>
          <tbody>
            <base-row-obw-stapel-a
              v-for="(
                ergebnisWithWahlvorschlag, index
              ) in ergebnisseAndWahlvorschlaege"
              :key="index"
              :model-value="ergebnisWithWahlvorschlag.ergebnis"
              :wahlvorschlag="ergebnisWithWahlvorschlag.wahlvorschlag"
              :ergebnis-stapel-c="
                getErgebnisStapelC(
                  ergebnisWithWahlvorschlag.ergebnis.wahlvorschlagID
                )
              "
            />
          </tbody>
          <tfoot>
            <tr>
              <td />
              <td class="font-weight-bold">Gültige Stimmen insgesamt</td>
              <td class="font-weight-bold">{{ sumOfValidVotes }}</td>
            </tr>
          </tfoot>
        </v-table>
      </v-card-text>
      <v-card-actions>
        <base-button-save
          :disabled="!isFormValid"
          @click="onSaveClicked"
        />
      </v-card-actions>
    </v-form>
  </v-card>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseRowObwStapelA from "@/components/ergebnisermittlung/OBW/stapelA/BaseRowOBWStapelA.vue";
import { useOBWStapelAUtils } from "@/composables/ergebnisermittlung/obwStapelAUtils.ts";
import { useOBWStapelCUtils } from "@/composables/ergebnisermittlung/obwStapelCUtils.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { sendErgebnisseByStapelArt } = useErgebnismeldungStore();

const STAPEL = StapelArtEnum.ObwA;

const props = defineProps({
  wahlID: {
    type: String,
    required: true,
  },
  wahlbezirkID: {
    type: String,
    required: true,
  },
});

const { stapelCGueltigErgebnisse } = useOBWStapelCUtils(
  computed(() => props.wahlID),
  computed(() => props.wahlbezirkID)
);

const { ergebnisseAndWahlvorschlaege, sumOfValidVotes } = useOBWStapelAUtils(
  computed(() => props.wahlID),
  computed(() => props.wahlbezirkID)
);

const isFormValid = ref<boolean | null>(null);

function onSaveClicked() {
  sendErgebnisseByStapelArt(props.wahlID, STAPEL);
}

function getErgebnisStapelC(wahlvorschlagID: string | null) {
  const foundItem = stapelCGueltigErgebnisse.value.find(
    (item) => item.ergebnis.wahlvorschlagID === wahlvorschlagID
  );
  return foundItem ? foundItem.ergebnis.ergebnis : 0;
}
</script>
