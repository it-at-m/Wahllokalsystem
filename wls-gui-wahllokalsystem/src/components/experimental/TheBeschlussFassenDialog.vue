<template>
  <v-dialog
    v-if="selectedStimmzettel"
    v-model="modelValue"
    width="1500"
    height="750"
  >
    <v-card>
      <v-card-title class="d-flex align-center py-0 pl-0 text-center">
        <v-tabs
          v-model="tab"
          bg-color="grey-lighten-3"
          slider-color="primary"
          color="primary"
        >
          <v-tab
            value="one"
            prepend-icon="$beschluss"
          >
            <span class="font-weight-bold"> Beschluss fassen </span>
          </v-tab>
          <v-tab value="two"> Stimmzettel anzeigen und bearbeiten</v-tab>
        </v-tabs>
        <v-spacer />
        <h2 class="mx-2">
          {{ currentUserTeamName }}
          {{ selectedStimmzettel.kennung }}
        </h2>
        <!--        <v-icon
          icon="$beschluss"
          size="small"
        />
        <span class="ml-2">
          Beschluss fassen zu Stimmzettel
          <span class="font-weight-bold"
            >{{ currentUserTeamName }}
            {{ selectedStimmzettel.kennung }}
          </span>
        </span>-->
      </v-card-title>
      <v-card-text class="pa-1">
        <v-tabs-window v-model="tab">
          <v-tabs-window-item
            value="one"
            class="ma-5 fill-height"
          >
            <v-row>
              <v-col cols="4">
                <v-radio-group
                  v-model="selectedOption"
                  class="mt-2"
                >
                  <v-radio
                    value="gueltig"
                    class="my-2 full-width-radio"
                  >
                    <template #label>
                      <v-row>
                        <v-col cols="8">
                          <div>Die Stimmabgabe ist gültig</div>
                        </v-col>
                        <v-col>
                          <v-icon
                            class="mx-2"
                            icon="$stimmzettelValid"
                            color="success"
                          />
                        </v-col>
                      </v-row>
                    </template>
                  </v-radio>
                  <v-radio
                    value="ungueltig"
                    class="my-2 full-width-radio"
                  >
                    <template #label>
                      <v-row>
                        <v-col cols="8">
                          <div>Die Stimmabgabe ist ungültig</div>
                        </v-col>
                        <v-col>
                          <v-icon
                            class="mx-2"
                            icon="$stimmzettelInvalid"
                            color="error"
                          />
                        </v-col>
                      </v-row>
                    </template>
                  </v-radio>
                </v-radio-group>
              </v-col>
              <v-col cols="8">
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
                  v-else-if="selectedOption === 'ungueltig'"
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
                <div
                  v-else
                  class="ml-8 mt-2"
                >
                  Bitte wählen Sie zuerst die Gültigkeit des Stimmzettels aus.
                </div>
              </v-col>
            </v-row>
            <v-row class="mt-4">
              <v-col cols="12">
                <v-switch
                  v-model="votesUnanimous"
                  color="primary"
                  :label="
                    votesUnanimous
                      ? 'Einstimmig angenommen'
                      : 'Mehrheitsentscheid'
                  "
                  hide-details
                />
              </v-col>
              <v-col
                cols="4"
                md="4"
              >
                <base-number-input
                  label="Stimmen dafür"
                  :model-value="votes.dafuer"
                />
              </v-col>
              <v-col
                cols="4"
                md="4"
              >
                <base-number-input
                  label="Stimmen dagegen"
                  :model-value="votes.dagegen"
                  @update:model-value="onVotesAgainstAdded"
                />
              </v-col>
              <v-col
                cols="4"
                md="4"
              >
                <base-number-input
                  label="Enthaltungen"
                  :model-value="votes.enthaltungen"
                />
              </v-col>
            </v-row>
          </v-tabs-window-item>
          <v-tabs-window-item value="two">
            <the-simple-stimmzettel-erfassung
              :wahlvorschlaege="wahlvorschlaege"
              :votes-only="stimmzettelWahlvorschlaege"
              :change-history="changeHistory"
              @command="onQuickInputCommand"
            />
          </v-tabs-window-item>
        </v-tabs-window>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <base-text-button @click="modelValue = false">
          Abbrechen
        </base-text-button>
        <base-text-button
          active
          disabled
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

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { storeToRefs } from "pinia";
import { computed, reactive, ref, watch } from "vue";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import TheSimpleStimmzettelErfassung from "@/components/experimental/TheSimpleStimmzettelErfassung.vue";
import { getStimmzettelManger } from "@/composables/experimental/stimmzettelManager.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { currentUserTeamName } = storeToRefs(useUserStore());
const { prepareWahlvorschlaege, createWahlvorschlag } =
  useWahlvorschlaegeTestDataFactory();
const { getRandomItem } = useCommonTestDataFactory();

const modelValue = defineModel<boolean>();

watch(modelValue, (visible) => {
  if (visible) {
    const gueltigkeit = getRandomItem(["gueltig", "ungueltig"]) as OptionKey;
    selectedOption.value = gueltigkeit;

    selections[gueltigkeit] = getRandomCheckboxSelections(
      checkboxItems[gueltigkeit]
    );
  }
});

function getRandomCheckboxSelections(
  options,
  min = 1,
  max = options.length - 2
) {
  const count = Math.floor(Math.random() * (max - min + 1)) + min; // Zufällige Anzahl zwischen min und max
  const shuffled = options.slice().sort(() => 0.5 - Math.random()); // Array zufällig mischen
  const selectedOptions = shuffled.slice(0, count); // Erste 'count' Elemente nehmen
  return selectedOptions.map((option) => option.value); // Nur die 'value'-Eigenschaften zurückgeben
}

const tab = ref("one");
const props = defineProps({
  selectedStimmzettel: {
    type: Object as PropType<BeschlussTabelleItem | null>,
    required: true,
  },
});

type OptionKey = "gueltig" | "ungueltig";

const selectedOption = ref<OptionKey | null>(null);

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
    { label: "Mehr als 3 Stimmen bei mind. 1 Person", value: "opt2_a" },
    { label: "keine Reststimmenvergabe möglich", value: "opt2_b" },
    { label: "einzelne Stimmen ungültig", value: "opt2_c" },
    {
      label: "Anderer Grund:",
      value: "opt1_d",
    },
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
  ungueltig: [],
});

watch(selectedOption, (now, prev) => {
  if (prev && prev !== now) selections[prev] = []; // entfernen der checkboxes bei wechsel der radiogroup
});

const wahlvorschlaege = prepareWahlvorschlaege()
  .wahlvorschlaege([
    createWahlvorschlag(),
    createWahlvorschlag(),
    createWahlvorschlag(),
  ])
  .build();
const stimmzettelManager = getStimmzettelManger(
  {
    wahlId: "wahlId",
    wahlbezirkId: "wahlbezirkId",
  },
  {
    maxValidVotesPerKandidat: 3,
    maxTotalVotes: 80,
  }
);
stimmzettelManager.setWahlvorschlaege(wahlvorschlaege.wahlvorschlaege);
const stimmzettelWahlvorschlaege =
  stimmzettelManager.stimmzettelWahlvorschlaege;
const changeHistory = computed(() =>
  stimmzettelManager.changeHistory.value.toReversed()
);

function onQuickInputCommand() {
  console.log("xxx");
}

const begruendung = ref("");
const abstimmungsergebnis = ref("");
const hasAnySelection = computed(() =>
  (["gueltig", "ungueltig"] as OptionKey[]).some(
    (k) => selections[k]?.length > 0
  )
);

const votes = reactive({ dafuer: 5, dagegen: 0, enthaltungen: 0 });
const voteMode = ref<"unanimous" | "majority">("majority");

watch(voteMode, (mode) => {
  if (mode === "unanimous") {
    votes.dagegen = 0;
    votes.enthaltungen = 0;
  }
});

const votesUnanimous = ref(true);

// Wenn einstimmig: “dagegen” und “Enthaltungen” zurücksetzen
watch(votesUnanimous, (on) => {
  if (on) {
    votes.dagegen = 0;
    votes.enthaltungen = 0;
    votes.dafuer = 5;
  }
});

function onVotesAgainstAdded(votes: number | undefined | null) {
  if (!votes) return;

  if (votes > 0) {
    voteMode.value = "majority";
  }
}
</script>
<style scoped>
.full-width-radio :deep(.v-selection-control__wrapper + .v-label) {
  flex: 1;
}
</style>
