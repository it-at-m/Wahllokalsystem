<template>
  <div>
    <v-row class="mt-2 ml-2">
      <v-col>
        <v-radio-group v-model="isGueltig">
          <v-radio :value="true">
            <template #label>
              <v-row>
                <v-col> Die Stimmabgabe ist gültig </v-col>
                <v-col
                  cols="auto"
                  class="ml-4"
                >
                  <v-icon
                    icon="$stimmzettelGueltig"
                    color="success"
                  />
                </v-col>
              </v-row>
            </template>
          </v-radio>
          <v-radio :value="false">
            <template #label>
              <v-row>
                <v-col> Die Stimmabgabe ist ungültig </v-col>
                <v-col
                  cols="auto"
                  class="ml-4"
                >
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
        <v-row
          v-for="beschlussgrund in beschlussgruende"
          :key="beschlussgrund.grund"
        >
          <v-checkbox
            v-model="beschlussgrund.selected"
            :label="beschlussgrund.grund"
            hide-details
          />
        </v-row>
        <v-row>
          <v-checkbox-btn label="Anderer Grund:" />
          <v-text-field
            :v-model="andererGrund"
            label="Grund"
          />
        </v-row>
      </v-col>
    </v-row>
    <v-card>
      <v-card-title class="mb-4"> Abstimmungsergebnis </v-card-title>
      <v-card-text>
        <v-form v-model="isAbstimmungsergebnisValid">
          <v-row style="align-items: stretch">
            <v-col cols="5">
              <base-number-input
                :model-value="stimmenDafuer"
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
                  size="large"
                />
              </div>
            </v-col>
            <v-col cols="5">
              <base-number-input
                :model-value="stimmenDagegen"
                :rules="[required]"
                label="Stimmen dagegen"
              />
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";

import { storeToRefs } from "pinia";
import { computed, onActivated, ref, watch } from "vue";

import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { required } = useRules();
const { isBWB } = storeToRefs(useUserStore());

const isGueltig = ref(false);
const andererGrund = ref("");
const stimmenDafuer = ref(0);
const stimmenDagegen = ref(0);
const isAbstimmungsergebnisValid = ref<boolean | null>(null);

const gruende = {
  gueltig: [
    "Wählerwille ist zweifelsfrei erkennbar (lila Notiz auf dem Stimmzettel)",
    "Briefwahl: Mehrere gleich gekennzeichnete Stimmzettel im Umschlag",
    "Briefwahl: Mehrere Stimmzettel im Umschlag, einer gekennzeichnet, die anderen leer",
    "Mehr als 3 Stimmen bei mind. einer Person und 80 Stimmen gesamt nicht überschritten",
    "keine Reststimmenvergabe möglich, Einzelstimmen und mehrere Kopfleistenkreuze",
    "einzelne Stimmen ungültig",
  ],
  ungueltig: [
    "Wählerwille ist nicht zweifelsfrei erkennbar",
    "mehr als 80 Einzelstimmen oder mehrere Kopfleistenkreuze ohne Einzelstimmen",
    "Stimmzettel ist mit einem besonderen Merkmal, Zusatz oder Vorbehalt versehen",
    "Stimmzettel ist nicht amtlich hergestellt (zum Beispiel von einer anderen Gemeinde)",
    "Briefwahl: Mehrere unterschiedlich gekennzeichnete Stimmzettel im Umschlag",
  ],
};

const props = defineProps<{
  stimmzettel: Stimmzettel | undefined;
}>();

const beschlussgruende = computed(() => {
  const liste = isGueltig.value ? gruende.gueltig : gruende.ungueltig;
  if (isBWB.value) {
    return liste.map((element) => {
      return { grund: element, selected: false };
    });
  }
  return liste
    .filter((grund) => !grund.includes("Briefwahl:"))
    .map((element) => {
      return { grund: element, selected: false };
    });
});
</script>

<style scoped>
.icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
}
</style>
