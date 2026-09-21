<template>
  <div>
    <v-row class="mt-2 ml-2">
      <v-col>
        <v-radio-group v-model="isGueltig">
          <v-radio
            :value="true"
            class="full-width-radio"
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
            class="full-width-radio"
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
        <v-row
          v-for="beschlussgrund in beschlussgruende"
          :key="beschlussgrund.grund"
          dense
        >
          <v-col>
            <v-checkbox
              v-model="beschlussgrund.selected"
              :label="beschlussgrund.grund"
              hide-details
            />
          </v-col>
        </v-row>
        <v-row>
          <v-col cols="auto">
            <v-checkbox-btn
              v-model="andererGrundChecked"
              label="Anderer Grund:"
            />
          </v-col>
          <v-col>
            <v-textarea
              v-model="andererGrund"
              label="Grund"
              :disabled="!andererGrundChecked"
              rows="1"
              auto-grow
              max-width="500"
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
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

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
  common: {
    gueltig: [
      "Wählerwille ist zweifelsfrei erkennbar (lila Notiz auf dem Stimmzettel)",
      "Mehr als 3 Stimmen bei mind. einer Person und 80 Stimmen gesamt nicht überschritten",
      "keine Reststimmenvergabe möglich, Einzelstimmen und mehrere Kopfleistenkreuze",
      "einzelne Stimmen ungültig",
    ],
    ungueltig: [
      "Wählerwille ist nicht zweifelsfrei erkennbar",
      "mehr als 80 Einzelstimmen oder mehrere Kopfleistenkreuze ohne Einzelstimmen",
      "Stimmzettel ist mit einem besonderen Merkmal, Zusatz oder Vorbehalt versehen",
      "Stimmzettel ist nicht amtlich hergestellt (zum Beispiel von einer anderen Gemeinde)",
    ],
  },
  bwb: {
    gueltig: [
      "Mehrere gleich gekennzeichnete Stimmzettel im Umschlag",
      "Mehrere Stimmzettel im Umschlag, einer gekennzeichnet, die anderen leer",
    ],
    ungueltig: [
      "Mehrere unterschiedlich gekennzeichnete Stimmzettel im Umschlag",
    ],
  },
};

const props = defineProps<{
  stimmzettel: PersistedStimmzettel | undefined;
}>();

interface BeschlussgrundOption {
  grund: string;
  selected: boolean;
}
const beschlussgruende = ref<BeschlussgrundOption[]>([]);

function rebuildBeschlussgruende() {
  const gruendeList = isGueltig.value
    ? isBWB.value
      ? [...gruende.common.gueltig, ...gruende.bwb.gueltig]
      : gruende.common.gueltig
    : isBWB.value
      ? [...gruende.common.ungueltig, ...gruende.bwb.ungueltig]
      : gruende.common.ungueltig;
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

  const wahlvorstandBeschlussvorschlag =
    props.stimmzettel?.wahlvorstandBeschlussvorschlag ?? [];
  beschlussgrundOptions.map((beschlussgrundOption) => {
    for (const beschlussvorschlag of wahlvorstandBeschlussvorschlag) {
      if (beschlussgrundOption.grund === beschlussvorschlag.text) {
        beschlussgrundOption.selected = true;
      }
    }
  });

  const texts = wahlvorstandBeschlussvorschlag
    .filter(
      (beschlussvorschlag) =>
        !beschlussgrundOptions.find(
          (beschlussgrund) => beschlussgrund.grund === beschlussvorschlag.text
        )
    )
    .map((w) => w.text);
  andererGrund.value = texts.join(", ");
  andererGrundChecked.value = texts.length > 0;

  beschlussgruende.value = beschlussgrundOptions;
}

watch(
  () => props.stimmzettel,
  (stimmzettel) => {
    if (stimmzettel) {
      const systemBeschlussvorschlaege =
        props.stimmzettel?.systemBeschlussvorschlag ?? [];
      const wahlvorstandBeschlussvorschlag =
        props.stimmzettel?.wahlvorstandBeschlussvorschlag ?? [];

      isGueltig.value =
        !systemBeschlussvorschlaege.some((beschlussvorschlag) => {
          const mappedReason =
            mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText(
              beschlussvorschlag.reason
            );
          return [...gruende.common.ungueltig, ...gruende.bwb.ungueltig].some(
            (grund) => grund === mappedReason
          );
        }) &&
        !wahlvorstandBeschlussvorschlag.some((beschlussvorschlag) => {
          return [...gruende.common.ungueltig, ...gruende.bwb.ungueltig].some(
            (grund) => grund === beschlussvorschlag.text
          );
        });

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
