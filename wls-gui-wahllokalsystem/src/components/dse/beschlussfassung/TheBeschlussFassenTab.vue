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
            :label="beschlussgrund.grund"
            hide-details
          />
          <div class="d-flex align-center">
            <v-checkbox
              :model-value="andererGrundChecked"
              readonly
            />
            <v-text-field
              v-model="andererGrund"
              label="Andere Gründe"
            />
          </div>
        </v-col>
      </v-row>
    </v-card-text>
    <v-card-title class="mb-4"> Abstimmungsergebnis </v-card-title>
    <v-card-text>
      <v-row style="align-items: stretch">
        <v-col cols="5">
          <base-number-input
            v-model="stimmenDafuer"
            :rules="[required]"
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
            v-model="stimmenDagegen"
            :rules="[required]"
            label="Stimmen dagegen"
          />
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import { computed, ref, watch } from "vue";

import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useTheBeschlussFassenTabUtils } from "@/composables/dse/beschlussfassung/theBeschlussFassenTabUtils.ts";

const { required } = useRules();

const {
  createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit,
  isStimmzettelGueltigBasedOnVormerkungsgruenden,
} = useTheBeschlussFassenTabUtils();

const props = defineProps<{
  stimmzettel: PersistedStimmzettel | undefined;
}>();

interface BeschlussgrundOption {
  grund: string;
  selected: boolean;
}
const beschlussgruende = ref<BeschlussgrundOption[]>([]);
const isGueltig = ref<boolean | null>(null);
const andererGrund = ref("");
const andererGrundChecked = computed(() => !!andererGrund.value);
const stimmenDafuer = ref<number | null>(null);
const stimmenDagegen = ref<number | null>(null);

watch(
  () => props.stimmzettel,
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
      props.stimmzettel
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
