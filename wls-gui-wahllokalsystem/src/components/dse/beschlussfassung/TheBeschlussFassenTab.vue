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

import { computed, ref } from "vue";

import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";

const { required } = useRules();

const stimmenDafuer = ref(0);
const stimmenDagegen = ref(0);
const isAbstimmungsergebnisValid = ref<boolean | null>(null);

const beschlussgruende = [{ grund: "xxx", selected: false }];

defineProps<{
  stimmzettel: Stimmzettel;
}>();

const isGueltig = computed(() => {
  return false;
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
