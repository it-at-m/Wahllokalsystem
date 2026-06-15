<template>
  <v-card>
    <v-form v-model="isFormValid">
      <v-card-title>Ungültige Stimmzettel</v-card-title>
      <v-card-text>
        <v-table class="ungueltigeStimmzettelTable">
          <tbody>
            <tr>
              <td class="descriptionCol">Anzahl ungültiger Stimmzettel</td>
              <td class="valueCol">
                <base-number-input
                  v-model="modelValue.ergebnis"
                  :rules="[required]"
                  min-width="5rem"
                />
              </td>
            </tr>
            <tr>
              <td>Anzahl ungültiger Stimmzettel nach Beschluss</td>
              <td class="text-end">
                {{ ungueltigeStimmzettelNachBeschluss }}
              </td>
            </tr>
            <tr>
              <td class="font-weight-bold">Summe ungültiger Stimmzettel</td>
              <td class="font-weight-bold text-end">
                {{ summeUngueltigerStimmzettel }}
              </td>
            </tr>
          </tbody>
        </v-table>
      </v-card-text>

      <v-card-text>
        <div>
          <base-number-input
            v-model="modelValue.ergebnis"
            :rules="[required]"
            min-width="20rem"
          />
          <v-divider class="my-2" />
          <v-row>
            <v-col cols="4">
              <div class="d-flex align-center ga-1">
                <v-icon icon="$add" />Anzahl ungültiger Stimmzettel nach
                Beschluss
              </div>
            </v-col>
            <v-col
              cols="1"
              class="d-flex align-center"
              >{{ ungueltigeStimmzettelNachBeschluss }}</v-col
            >
          </v-row>
          <v-row class="font-weight-bold">
            <v-col cols="4">
              <div class="d-flex align-center ga-1">
                <v-icon icon="$equal" />
                Summe ungültiger Stimmzettel
              </div></v-col
            >
            <v-col
              cols="1"
              class="d-flex align-center"
              >{{ summeUngueltigerStimmzettel }}</v-col
            >
          </v-row>
        </div>
      </v-card-text>

      <v-card-text>
        <v-table>
          <thead>
            <tr>
              <th />
              <th class="font-weight-bold text-right">Zweifelsfrei ungültig</th>
              <th class="font-weight-bold text-right">
                Laut Beschluss ungültig
              </th>
              <th class="font-weight-bold text-right">Insgesamt</th>
            </tr>
          </thead>
          <tfoot>
            <tr>
              <td class="font-weight-bold">Ungültige Stimmzettel</td>
              <td class="font-weight-bold text-right">
                <base-number-input
                  v-model="modelValue.ergebnis"
                  :rules="[required]"
                  min-width="5rem"
                />
              </td>
              <td class="font-weight-bold text-right">
                {{ ungueltigeStimmzettelNachBeschluss }}
              </td>
              <td class="font-weight-bold text-right">
                {{ summeUngueltigerStimmzettel }}
              </td>
            </tr>
          </tfoot>
        </v-table>
      </v-card-text>
      <v-card-actions>
        <base-wls-button-save
          :loading="isSaving"
          :disabled="isWahlFinished || !isFormValid"
          :save-text="SAVE_CONTINUE"
          @click="onSaveClicked"
        />
      </v-card-actions>
    </v-form>
  </v-card>
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/common/Ergebnis.ts";

import { computed, ref } from "vue";

import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { SAVE_CONTINUE } from "@/constants.ts";

const { required } = useRules();

const isFormValid = ref<null | boolean>(null);

const modelValue = defineModel<Ergebnis>({ required: true });

const { ungueltigeStimmzettelNachBeschluss } = defineProps({
  isSaving: {
    type: Boolean,
    required: false,
    default: false,
  },
  isWahlFinished: {
    type: Boolean,
    required: true,
  },
  ungueltigeStimmzettelNachBeschluss: {
    type: Number,
    required: true,
  },
});

const emit = defineEmits<{
  save: [];
}>();

const summeUngueltigerStimmzettel = computed(
  () => ungueltigeStimmzettelNachBeschluss + (modelValue.value.ergebnis || 0)
);

function onSaveClicked() {
  emit("save");
}
</script>

<style scoped>
.ungueltigeStimmzettelTable {
  max-width: 600px;
}

.descriptionCol {
  width: 66%;
}
</style>
