<template>
  <v-dialog
    v-if="selectedStimmzettel"
    v-model="modelValue"
  >
    <v-card>
      <v-card-title class="d-flex align-center">
        <v-icon
          icon="$beschluss"
          size="small"
        />
        <span class="ml-2">
          Beschluss fassen zu Stimmzettel
          <span class="font-weight-bold"
            >{{ currentUserTeamName }}
            {{ selectedStimmzettel.kennung }}
          </span>
        </span>
      </v-card-title>
      <v-card-text>
        <v-row align="center">
          Beschlussvorschläge vom System:
          <v-autocomplete
            v-model="selectedSuggestion"
            :items="suggestions"
            item-title="gueltigkeit"
            return-object
            hide-details
            class="ml-5"
          />
        </v-row>
        <v-row>
          <v-col cols="3">
            <v-radio-group
              v-model="selectedOption"
              class="mt-2"
            >
              <v-radio
                label="Die Stimmabgabe ist gültig"
                value="gueltig"
              />
              <v-radio
                label="Die Stimmabgabe ist teilweise gültig"
                value="twGueltig"
              />
              <v-radio
                label="Die Stimmabgabe ist ungültig"
                value="ungueltig"
              />
            </v-radio-group>
          </v-col>
          <v-col cols="9">
            <div
              v-if="selectedOption === 'gueltig'"
              class="ml-8 mt-2"
            >
              <v-checkbox
                v-for="item in checkboxItems.gueltig"
                :key="`opt1-${item.value}`"
                v-model="selections.gueltig"
                :value="item.value"
                :label="item.label"
                density="compact"
                hide-details
              />
              <v-text-field />
            </div>
            <div
              v-else-if="selectedOption === 'twGueltig'"
              class="ml-8 mt-2"
            >
              <v-checkbox
                v-for="item in checkboxItems.twGueltig"
                :key="`opt2-${item.value}`"
                v-model="selections.twGueltig"
                :value="item.value"
                :label="item.label"
                density="compact"
                hide-details
              />
              <v-text-field />
            </div>
            <div
              v-else
              class="ml-8 mt-2"
            >
              <v-checkbox
                v-for="item in checkboxItems.ungueltig"
                :key="`opt3-${item.value}`"
                v-model="selections.ungueltig"
                :value="item.value"
                :label="item.label"
                density="compact"
                hide-details
              />
              <v-text-field />
            </div>
          </v-col>
        </v-row>
        <v-row
          class="text-center"
          align="center"
        >
          Abstimmungsergebnis:
          <v-text-field
            hide-details
            class="ml-5"
          />
        </v-row>
      </v-card-text>
      <v-card-actions>
        <base-text-button>
          Stimmzettel anzeigen und bearbeiten
        </base-text-button>
        <v-spacer />
        <base-text-button> Abgelehnten Vorschlag speichern </base-text-button>
        <base-text-button
          active
          @click="modelValue = false"
        >
          Beschluss speichern
        </base-text-button>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script setup lang="ts">
import type { BeschlussTabelleItem } from "@/types/dse/BeschlussTabelleItem.ts";
import type { PropType } from "vue";

import { storeToRefs } from "pinia";
import { reactive, ref, watch } from "vue";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import { useUserStore } from "@/stores/userStore.ts";

const { currentUserTeamName } = storeToRefs(useUserStore());

const modelValue = defineModel<boolean>();

const props = defineProps({
  selectedStimmzettel: {
    type: Object as PropType<BeschlussTabelleItem | null>,
    required: true,
  },
});

type OptionKey = "gueltig" | "twGueltig" | "ungueltig";
interface Suggestion {
  gueltigkeit: OptionKey;
  gruende: Record<OptionKey, string[]>;
}

// Vorschläge (Beispieldaten an deine Realität anpassen)
const suggestions: Suggestion[] = [
  {
    gueltigkeit: "gueltig",
    gruende: { gueltig: ["opt1_b"], twGueltig: [], ungueltig: [] },
  },
  {
    gueltigkeit: "twGueltig",
    gruende: { gueltig: [], twGueltig: ["opt2_b", "opt2_c"], ungueltig: [] },
  },
  {
    gueltigkeit: "ungueltig",
    gruende: {
      gueltig: [],
      twGueltig: [],
      ungueltig: ["opt3_a", "opt3_c", "opt3_d"],
    },
  },
];

const selectedOption = ref<OptionKey>("gueltig");

const checkboxItems: Record<OptionKey, { label: string; value: string }[]> = {
  gueltig: [
    { label: "Wählerwille ist zweifelsfrei erkennbar", value: "opt1_a" },
    {
      label:
        "Briefwahl: Mehrere gleich gekennzeichnete Stimmzettel im Umschlag",
      value: "opt1_b",
    },
    {
      label: "Briefwahl: Mehrere Stimmzettel im Umschlag, einer gekennzeichnet",
      value: "opt1_c",
    },
    {
      label: "Anderer Grund:",
      value: "opt1_d",
    },
  ],
  twGueltig: [
    { label: "Mehr als 3 Stimmen bei mind. 1 Person", value: "opt2_a" },
    { label: "keine Reststimmenvergabe möglich", value: "opt2_b" },
    { label: "einzelne Stimmen ungültig", value: "opt2_c" },
    { label: "Anderer Grund:", value: "opt2_d" },
  ],
  ungueltig: [
    { label: "Wählerwille nicht zweifelsfrei erkennbar", value: "opt3_a" },
    {
      label: "mehr als 80 Einzelstimmen / mehrere Listenkreuze",
      value: "opt3_b",
    },
    {
      label: "besonderes Merkmal, Zusatz oder Vorbehalt auf Stimmzettel",
      value: "opt3_c",
    },
    { label: "Stimmzettel nicht amtlich hergestellt", value: "opt3_d" },
    {
      label:
        "Briefwahl: mehrere unterschiedlich gekennzeichnete Stimmzettel im Umschlag",
      value: "opt3_e",
    },
    { label: "Anderer Grund:", value: "opt3_f" },
  ],
};

const selections = reactive<Record<OptionKey, string[]>>({
  gueltig: [],
  twGueltig: [],
  ungueltig: [],
});

watch(selectedOption, (now, prev) => {
  if (prev && prev !== now) selections[prev] = []; // entfernen der checkboxes bei wechsel der radiogroup
  selectedSuggestion.value = null; // element aus autocomplete entfernen
});

const selectedSuggestion = ref<Suggestion | null>(null);

// update selected values je nach gewähltem vorschlag
watch(selectedSuggestion, (val) => {
  if (!val) return;
  selectedOption.value = val.gueltigkeit;

  (["gueltig", "twGueltig", "ungueltig"] as OptionKey[]).forEach((k) => {
    selections[k] = [...val.gruende[k]];
  });
});
</script>
