<template>
  <v-card>
    <v-card-title>Stimmzettelerfassung</v-card-title>
    <v-card-text>
      <v-row>
        <v-col
          v-for="(wahlvorschlag, index) in wahlvorschlaege.wahlvorschlaege"
          :key="index"
        >
          <v-card>
            <v-card-title
              >Wahlvorschlag Nr. {{ wahlvorschlag.ordnungszahl }}</v-card-title
            >
            <v-card-subtitle>
              <v-checkbox>
                <template #label>
                  {{ wahlvorschlag.kurzname }}
                </template>
              </v-checkbox>
            </v-card-subtitle>
            <div
              v-for="(kandidat, index) in wahlvorschlag.kandidaten"
              :key="index"
              class="mb-2"
            >
              <v-btn
                @click="
                  addStimme(wahlvorschlag.identifikator, kandidat.identifikator)
                "
                >+</v-btn
              ><v-btn
                class="ml-2"
                @click="
                  removeStimme(
                    wahlvorschlag.identifikator,
                    kandidat.identifikator
                  )
                "
                >-</v-btn
              >
              {{ kandidat.listenposition }}
              {{ kandidat.name }}
              Stimmen:
              {{
                getErgebnis(wahlvorschlag.identifikator, kandidat.identifikator)
                  .ergebnis
              }}
            </div>
          </v-card>
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";
import type { PropType } from "vue";

import { ref } from "vue";

defineProps({
  wahlvorschlaege: {
    type: Object as PropType<Wahlvorschlaege>,
    required: true,
  },
});

const ergebnisse = ref<Ergebnis[]>([]);

function addStimme(
  wahlvorschlagIdentifikator: string,
  kandidatIdentifikator: string
) {
  const ergebnis = getErgebnis(
    wahlvorschlagIdentifikator,
    kandidatIdentifikator
  );
  ergebnis.ergebnis = (ergebnis.ergebnis ?? 0) + 1;
}
function removeStimme(
  wahlvorschlagIdentifikator: string,
  kandidatIdentifikator: string
) {
  const ergebnis = getErgebnis(
    wahlvorschlagIdentifikator,
    kandidatIdentifikator
  );
  ergebnis.ergebnis = (ergebnis.ergebnis ?? 0) - 1;
}

function getErgebnis(
  wahlvorschlagIdentifikator: string,
  kandidatIdentifikator: string
) {
  let ergebnisFound = ergebnisse.value.find(
    (ergebnis) =>
      ergebnis.wahlvorschlagID === wahlvorschlagIdentifikator &&
      ergebnis.kandidatID === kandidatIdentifikator
  );
  if (!ergebnisFound) {
    ergebnisFound = {
      kandidatID: kandidatIdentifikator,
      wahlvorschlagID: wahlvorschlagIdentifikator,
      ergebnis: null,
      wahlvorschlagsOrdnungszahl: null,
      numIndex: null,
    };
    ergebnisse.value.push(ergebnisFound);
  }
  return ergebnisFound;
}
</script>

<style scoped></style>
