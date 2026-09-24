<template>
  <v-card>
    <v-card-text>
      <v-row>
        <v-col>
          <v-radio-group v-model="isGueltig">
            <v-radio
              :value="true"
              class="my-2 full-width-radio"
            >
              <template #label>
                <v-row>
                  <v-col> Die Stimmabgabe ist gültig </v-col>
                  <v-col>
                    <v-icon
                      icon="$stimmzettelGueltig"
                      color="success"
                    />
                  </v-col>
                </v-row>
              </template>
            </v-radio>
            <v-radio
              :value="false"
              class="my-2 full-width-radio"
            >
              <template #label>
                <v-row>
                  <v-col> Die Stimmabgabe ist ungültig </v-col>
                  <v-col>
                    <v-icon
                      icon="$stimmzettelUngueltig"
                      color="error"
                    />
                  </v-col>
                </v-row>
              </template>
            </v-radio>
          </v-radio-group>
        </v-col>
        <v-col>
          <v-checkbox
            v-for="beschlussgrund in beschlussgruende"
            :key="beschlussgrund.grund"
            v-model="beschlussgrund.selected"
            :label="getBeschlussgrundEnumValueAsString(beschlussgrund.grund)"
            hide-details
          />
          <div class="d-flex align-center">
            <v-checkbox
              :model-value="andererGrundChecked"
              readonly
            />
            <v-textarea
              v-model="andererGrund"
              label="Andere Gründe"
              rows="1"
              auto-grow
            />
          </div>
        </v-col>
      </v-row>
    </v-card-text>
    <v-card-title class="mb-4"> Abstimmungsergebnis </v-card-title>
    <v-card-text v-if="abstimmungsergebnis">
      <v-row style="align-items: stretch">
        <v-col cols="5">
          <base-number-input
            v-model="abstimmungsergebnis.stimmenDafuer"
            :rules="[
              required,
              minNumber(1),
              maxNumber(anwesendeWahlvorstandsmitgliederAnzahl),
            ]"
            label="Stimmen dafür"
          />
        </v-col>
        <v-col
          cols="2"
          style="display: flex"
        >
          <div class="icon-wrapper">
            <v-icon
              icon="$beschlussAbstimmung"
              size="x-large"
            />
          </div>
        </v-col>
        <v-col cols="5">
          <base-number-input
            v-model="abstimmungsergebnis.stimmenDagegen"
            :rules="[
              required,
              minNumber(0),
              maxNumber(anwesendeWahlvorstandsmitgliederAnzahl),
            ]"
            label="Stimmen dagegen"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <base-feedback-card
            v-if="abstimmungsergebnis.abstimmungIsUnentschieden"
            title="Die Abstimmung ist unentschieden"
            type="warning"
          >
            <div>
              <p>
                Bei einem Gleichstand ist die Stimme des Wahlvorstehers / der
                Wahlvorsteherin ausschlaggebend.
              </p>
              <p>
                Bitte bestätigen Sie, dass der/die Wahlvorsteher/in
                <span class="font-weight-bold"> dafür </span> gestimmt hat, und
                das Abstimmungsergebnis somit
                <span class="font-weight-bold">
                  {{ (abstimmungsergebnis.stimmenDafuer ?? 0) + 1 }} zu
                  {{ abstimmungsergebnis.stimmenDagegen }} für den
                  Beschlussvorschlag
                </span>
                ist.
              </p>
              <v-checkbox
                v-model="abstimmungsergebnis.hasWahlvorsteherVotedDafuer"
                label="Der/Die Wahlvorsteher/in hat dafür gestimmt"
                hide-details
              />
            </div>
          </base-feedback-card>
          <base-feedback-card
            v-if="abstimmungsergebnis.abstimmungIsUngueltig"
            title="Ungültige Zusammensetzung an Stimmen"
            type="error"
          >
            <ul>
              <li>
                Es müssen sich mindestens
                <span class="font-weight-bold"> 3 </span> Personen an der
                Abstimmung beteiligen.
              </li>
              <li>
                Es können nicht mehr als
                <span class="font-weight-bold">
                  {{ anwesendeWahlvorstandsmitgliederAnzahl }}
                </span>
                Personen an der Abstimmung teilnehmen.
              </li>
              <li>
                Die Anzahl der "Stimmen dagegen" darf nicht größer sein, als die
                Anzahl der "Stimmen dafür". Über einen abgelehnten
                Beschlussvorschlag muss neu abgestimmt werden.
              </li>
            </ul>
          </base-feedback-card>
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { BeschlussAbstimmungsergebnis } from "@/types/dse/beschlussfassung/BeschlussAbstimmungsergebnis.ts";
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import { storeToRefs } from "pinia";
import { computed, ref, watch } from "vue";

import BaseFeedbackCard from "@/components/common/cards/BaseFeedbackCard.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useBeschlussgrundTools } from "@/composables/dse/beschlussfassung/beschlussgrundTools.ts";
import { useTheBeschlussFassenTabUtils } from "@/composables/dse/beschlussfassung/theBeschlussFassenTabUtils.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";

const { required, minNumber, maxNumber } = useRules();

const {
  createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit,
  isStimmzettelGueltigBasedOnVormerkungsgruenden,
} = useTheBeschlussFassenTabUtils();
const { getBeschlussgrundEnumValueAsString } = useBeschlussgrundTools();
const { anwesendeWahlvorstandsmitgliederAnzahl } = storeToRefs(
  useWahlvorstandStore()
);

const stimmzettel = defineModel<PersistedStimmzettel | undefined>(
  "stimmzettel"
);
const abstimmungsergebnis = defineModel<BeschlussAbstimmungsergebnis>(
  "abstimmungsergebnis"
);

interface BeschlussgrundOption {
  grund: string;
  selected: boolean;
}
const beschlussgruende = ref<BeschlussgrundOption[]>([]);
const isGueltig = ref<boolean | null>(null);
const andererGrund = ref("");
const andererGrundChecked = computed(() => !!andererGrund.value);

watch(
  () => stimmzettel.value,
  (stimmzettel) => {
    if (!stimmzettel) return;

    isGueltig.value =
      isStimmzettelGueltigBasedOnVormerkungsgruenden(stimmzettel);
    rebuildBeschlussgruende();
  },
  { immediate: true }
);

watch(
  () => isGueltig.value,
  () => rebuildBeschlussgruende()
);

function rebuildBeschlussgruende() {
  const gruende =
    createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit(
      isGueltig.value,
      stimmzettel.value
    );
  andererGrund.value = gruende.andererGrund;
  beschlussgruende.value = gruende.beschlussgruende;
}
</script>

<style scoped>
.icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.full-width-radio :deep(.v-selection-control__wrapper + .v-label) {
  flex: 1;
}
</style>
