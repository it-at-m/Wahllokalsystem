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
          <v-checkbox-btn
            v-model="andererGrundChecked"
            label="Anderer Grund:"
          />
          <v-col>
            <v-textarea
              v-model="andererGrund"
              label="Grund"
              :disabled="!andererGrundChecked"
              rows="1"
              auto-grow
              max-width="300"
            />
          </v-col>
        </v-row>
      </v-col>
    </v-row>
    <v-card>
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
  </div>
</template>

<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";

import { storeToRefs } from "pinia";
import { ref, watch } from "vue";

import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useSystemBeschlussgrundReasonEnumTools } from "@/composables/dse/beschlussfassung/systemBeschlussgrundReasonEnumTools.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { required } = useRules();
const { isBWB } = storeToRefs(useUserStore());

const { mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText } =
  useSystemBeschlussgrundReasonEnumTools();

const isGueltig = ref<boolean | null>(null);
const andererGrund = ref("");
const andererGrundChecked = ref(false);
const stimmenDafuer = ref<number | null>(null);
const stimmenDagegen = ref<number | null>(null);

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

interface BeschlussgrundOption {
  grund: string;
  selected: boolean;
}
const beschlussgruende = ref<BeschlussgrundOption[]>([]);

function rebuildBeschlussgruende() {
  const gruendeList = (
    isGueltig.value ? gruende.gueltig : gruende.ungueltig
  ).filter((grund) => (isBWB.value ? true : !grund.includes("Briefwahl:")));
  const beschlussgrundOptions: BeschlussgrundOption[] = gruendeList.map(
    (element) => ({
      grund: element,
      selected: false,
    })
  );

  const systemBeschlussvorschlaege =
    props.stimmzettel?.systemBeschlussvorschlag ?? [];
  for (const beschlussvorschlag of systemBeschlussvorschlaege) {
    const reasonAsGrund =
      mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText(
        beschlussvorschlag.reason
      );
    if (reasonAsGrund) {
      const entry = beschlussgrundOptions.find(
        (beschlussgrundOption) => beschlussgrundOption.grund === reasonAsGrund
      );
      if (entry) {
        entry.selected = true;
      }
    }
  }
  beschlussgruende.value = beschlussgrundOptions;
}

watch(
  () => props.stimmzettel,
  (stimmzettel) => {
    if (stimmzettel) {
      const systemBeschlussvorschlaege =
        props.stimmzettel?.systemBeschlussvorschlag ?? [];

      isGueltig.value = systemBeschlussvorschlaege.some(
        (beschlussvorschlag) => {
          const mappedReason =
            mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText(
              beschlussvorschlag.reason
            );
          return gruende.gueltig.some((grund) => grund === mappedReason);
        }
      );

      const texts = (stimmzettel.wahlvorstandBeschlussvorschlag ?? []).map(
        (w) => w.text
      );
      andererGrund.value = texts.join(", ");
      andererGrundChecked.value = texts.length > 0;

      rebuildBeschlussgruende();
    }
  },
  { immediate: true }
);

watch(
  () => isGueltig.value,
  () => rebuildBeschlussgruende()
);
</script>

<style scoped>
.icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
}
</style>
