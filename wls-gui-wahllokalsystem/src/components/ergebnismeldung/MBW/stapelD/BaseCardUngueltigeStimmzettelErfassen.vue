<template>
  <v-card>
    <v-form v-model="isFormValid">
      <v-card-title>Ungültige Stimmzettel</v-card-title>
      <v-card-text>
        <v-table class="ungueltigeStimmzettelTable">
          <tbody>
            <tr>
              <td
                class="descriptionCol"
                style="padding-bottom: 20px"
              >
                {{
                  isUWB
                    ? "Anzahl leerer Stimmzettel"
                    : "Anzahl leerer Stimmzettel und Stimmzettelumschläge"
                }}
              </td>
              <td class="valueCol pr-0">
                <base-number-input
                  v-model="modelValue.ergebnis"
                  :rules="[required]"
                  min-width="11rem"
                />
              </td>
            </tr>
            <tr>
              <td>
                <div class="my-4">
                  Anzahl ungültiger Stimmzettel nach Beschluss
                </div>
              </td>
              <td class="text-end">
                {{ ungueltigeStimmzettelNachBeschluss }}
              </td>
            </tr>
          </tbody>
          <tfoot>
            <tr>
              <td class="font-weight-bold">Summe ungültiger Stimmzettel</td>
              <td class="font-weight-bold text-end">
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

import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { SAVE_CONTINUE } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { required } = useRules();
const { isUWB } = storeToRefs(useUserStore());

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
  max-width: 30rem;
}

.descriptionCol {
  width: 66%;
}
</style>
