<template>
  <div class="ms-1">
    <v-row class="d-flex flex-row align-items-center w-100">
      <v-col
        cols="2"
        class="d-flex flex-column ga-2"
      >
        <v-card class="mb-1">
          <v-card-title>Eingabehistorie</v-card-title>
          <v-card-text>
            <div v-if="firstHistoryItem">
              <div class="d-flex align-center ga-1 font-weight-bold">
                <input-history-icon :input-type="firstHistoryItem.type" />
                <div>
                  <div
                    v-for="text in firstHistoryItem.text"
                    :key="text"
                  >
                    {{ text }}
                  </div>
                </div>
              </div>
            </div>
            <div v-else>Noch keine Stimmen erfasst</div>
            <div
              v-if="nextFiveItems.length > 0"
              class="my-4"
            >
              <v-divider thickness="4" />
            </div>

            <template
              v-for="(item, index) in nextFiveItems"
              :key="index"
            >
              <div class="d-flex align-center ga-1">
                <input-history-icon :input-type="item.type" />
                <div>
                  <div
                    v-for="text in item.text"
                    :key="text"
                  >
                    {{ text }}
                  </div>
                </div>
              </div>
              <div class="my-2">
                <v-divider thickness="1" />
              </div>
            </template>
          </v-card-text>
        </v-card>
        <v-card>
          <v-card-title>Beschlussfassung</v-card-title>
          <v-card-text>
            <v-checkbox
              label="für Beschlussfassung vormerken"
              hide-details
            />
            <v-textarea
              label="Grund für Beschlussfassung"
              max-width="500"
              hide-details
            />
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="7">
        <v-expansion-panels>
          <v-expansion-panel>
            <v-expansion-panel-title>Kurzbefehle</v-expansion-panel-title>
            <v-expansion-panel-text class="px-0">
              <v-table density="compact">
                <tbody>
                  <tr>
                    <td class="font-weight-bold px-0"><em>&lt;101&gt;</em></td>
                    <td class="px-0">Setzt 1 Stimme</td>
                  </tr>
                  <tr>
                    <td class="font-weight-bold px-0">
                      <em>&lt;101&gt;+&lt;x&gt;</em>
                    </td>
                    <td class="px-0">Ergänzt n Stimmen</td>
                  </tr>
                  <tr>
                    <td class="font-weight-bold px-0">
                      <em>&lt;101&gt;-&lt;105&gt;</em>
                    </td>
                    <td class="px-0">Ergänzt je 1 Stimme im Bereich</td>
                  </tr>
                  <tr>
                    <td class="font-weight-bold px-0"><em>-&lt;101&gt;</em></td>
                    <td class="px-0">Streicht den Kandidat</td>
                  </tr>
                  <tr>
                    <td class="font-weight-bold px-0"><em>u&lt;101&gt;</em></td>
                    <td class="px-0">Setzt 1 ungültige Stimme</td>
                  </tr>
                  <tr>
                    <td class="font-weight-bold px-0"><em>&lt;3&gt;</em></td>
                    <td class="px-0">Setzt Listemkreuz</td>
                  </tr>
                </tbody>
              </v-table>
            </v-expansion-panel-text>
          </v-expansion-panel>
        </v-expansion-panels>
        <base-form-stimmzettel-quick-input @command="onQuickInputCommand" />
        <v-slide-group
          v-model="activeWahlvorschlagId"
          center-active
          mandatory
        >
          <v-slide-group-item
            v-for="wahlvorschlag in stimmzettelWahlvorschlaege"
            :key="wahlvorschlag.identifikator"
            v-slot="{ selectedClass }"
            :value="wahlvorschlag.identifikator"
          >
            <div
              class="mx-2 slide-item-wrapper"
              :class="selectedClass"
            >
              <div class="slide-item-scroll">
                <base-wahlvorschlag-scores-card
                  :wahlvorschlag="wahlvorschlag"
                  :max-total-votes="MAX_TOTAL_VOTES"
                  :total-user-votes="totalUserVotes"
                />
              </div>
            </div>
          </v-slide-group-item>
        </v-slide-group>
      </v-col>
      <v-col cols="3">
        <the-stimmzettel-summary-card />
        <v-card class="mt-3 mb-1">
          <v-card-title>Spezialfälle</v-card-title>
          <v-card-text class="pb-1">
            <v-row align="center">
              <v-col cols="8">
                <v-checkbox
                  label="es gibt ungültige Stimmen die nicht zugeordnet werden können"
                  hide-details
                />
              </v-col>
              <v-col cols="4">
                <v-number-input
                  control-variant="stacked"
                  density="compact"
                  hide-details
                  width="100"
                  :clearable="false"
                  :min="0"
                />
              </v-col>
            </v-row>
            <v-radio-group>
              <v-radio
                label="Stimmzettel ist leer"
                value="one"
              />
              <v-radio
                label="Stimmzettel fehlt"
                value="two"
              />
              <v-radio
                label="Stimmzettel ist offensichtlich ungültig"
                value="three"
              />
            </v-radio-group>
            <v-text-field
              label="Begründung"
              hide-details
            />
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script setup lang="ts">
import type { AbstractCommandEvent } from "@/types/experimental/AbstractCommandEvent.ts";
import type { InputHistoryItem } from "@/types/experimental/InputHistoryItem.ts";
import type { KandidatEvent } from "@/types/experimental/KandidatEvent.ts";
import type { StimmzettelEvent } from "@/types/experimental/StimmzettelEvent.ts";
import type { StimmzettelSnapshot } from "@/types/experimental/StimmzettelSnapshot.ts";
import type { StimmzettelWahlvorschlag } from "@/types/experimental/StimmzettelWahlvorschlag.ts";
import type { WahlvorschlagEvent } from "@/types/experimental/WahlvorschlagEvent.ts";
import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";
import type { PropType } from "vue";

import { computed, ref } from "vue";

import BaseFormStimmzettelQuickInput from "@/components/experimental/BaseFormStimmzettelQuickInput.vue";
import BaseWahlvorschlagScoresCard from "@/components/experimental/BaseWahlvorschlagScoresCard.vue";
import InputHistoryIcon from "@/components/experimental/InputHistoryIcon.vue";
import TheStimmzettelSummaryCard from "@/components/experimental/TheStimmzettelSummaryCard.vue";
import { getStimmzettelManger } from "@/composables/experimental/stimmzettelManager.ts";
import { KandidatEventTypeEnum } from "@/types/experimental/KandidatEventTypeEnum.ts";
import { StimmzettelEventTypeEnum } from "@/types/experimental/StimmzettelEventTypeEnum.ts";
import { WahlvorschlagEventTypeEnum } from "@/types/experimental/WahlvorschlagEventTypeEnum.ts";

const { changeHistory, wahlvorschlaege } = defineProps({
  votesOnly: {
    type: Array as PropType<StimmzettelWahlvorschlag[]>,
    required: true,
  },
  changeHistory: {
    type: Array as PropType<InputHistoryItem[]>,
    required: true,
  },
  wahlvorschlaege: {
    type: Object as PropType<Wahlvorschlaege>,
    required: true,
  },
});

const emit = defineEmits<{
  command: [command: AbstractCommandEvent];
  snapshotCreated: [stimmzettelSnapshot: StimmzettelSnapshot];
}>();

const firstHistoryItem = computed(() =>
  changeHistory.length > 0 ? changeHistory[0] : null
);
const nextFiveItems = computed(() =>
  changeHistory.filter((_, index) => index < 5 && index > 0)
);
function isWahlvorschlagEvent(event: unknown): event is WahlvorschlagEvent {
  if (typeof event !== "object" || event === null) {
    return false;
  }

  const e = event as Record<string, unknown>;

  const hasCorrectType = Object.values(WahlvorschlagEventTypeEnum).includes(
    e.type as never
  );
  const hasWahlvorschlagOrdnungszahl =
    e.wahlvorschlagOrdnungszahl !== undefined &&
    typeof e.wahlvorschlagOrdnungszahl === "number";

  return hasCorrectType && hasWahlvorschlagOrdnungszahl;
}

function isKandidatEvent(event: unknown): event is KandidatEvent {
  if (typeof event !== "object" || event === null) {
    return false;
  }

  const e = event as Record<string, unknown>;

  const hasCorrectType = Object.values(KandidatEventTypeEnum).includes(
    e.type as never
  );
  const hasKandidatOrdnungszahl =
    e.kandidatNummer !== undefined && typeof e.kandidatNummer === "number";
  const hasCount = e.count !== undefined ? typeof e.count === "number" : true;

  return hasCorrectType && hasKandidatOrdnungszahl && hasCount;
}

function isStimmzettelEvent(event: unknown): event is StimmzettelEvent {
  if (typeof event !== "object" || event === null) {
    return false;
  }

  const e = event as Record<string, unknown>;

  return Object.values(StimmzettelEventTypeEnum).includes(
    e.stimmzettelEventType as never
  );
}

function saveCurrentStimmzettelSnapshot() {
  const stimmzettelSnapshot = stimmzettelManager.createSnapshot();
  emit("snapshotCreated", stimmzettelSnapshot);
  stimmzettelManager.reset();
}

function onQuickInputCommand(command: AbstractCommandEvent) {
  if (isWahlvorschlagEvent(command)) {
    if (command.type === WahlvorschlagEventTypeEnum.SELECT) {
      stimmzettelManager.selectWahlvorschlagByOrdnungszahl(
        command.wahlvorschlagOrdnungszahl
      );
      setActiveByOrdnungszahl(command.wahlvorschlagOrdnungszahl);
    } else if (command.type === WahlvorschlagEventTypeEnum.DESELECT) {
      stimmzettelManager.deselectWahlvorschlagByOrdnungszahl(
        command.wahlvorschlagOrdnungszahl
      );
      setActiveByOrdnungszahl(command.wahlvorschlagOrdnungszahl);
    }
  } else if (isKandidatEvent(command)) {
    // auf den betroffenen Wahlvorschlag fokussieren
    const ordnungszahl = Math.floor(command.kandidatNummer / 100);
    setActiveByOrdnungszahl(ordnungszahl);
    const kandidatId = stimmzettelManager.getKandidatIdForKandidatNummer(
      command.kandidatNummer
    );
    if (!kandidatId) {
      return;
    }

    switch (command.type) {
      case KandidatEventTypeEnum.ADD_VOTE: {
        if (command.count !== undefined) {
          stimmzettelManager.addKandidatVote(kandidatId, command.count);
        }
        break;
      }
      case KandidatEventTypeEnum.REMOVE_VOTE: {
        if (command.count !== undefined) {
          stimmzettelManager.removeKandidatVote(kandidatId, command.count);
        }
        break;
      }
      case KandidatEventTypeEnum.SET_VOTE: {
        if (command.count !== undefined) {
          stimmzettelManager.setKandidatVote(kandidatId, command.count);
        }
        break;
      }
      case KandidatEventTypeEnum.DISCARD: {
        stimmzettelManager.discardKandidat(kandidatId);
        break;
      }
      case KandidatEventTypeEnum.REMOVE_DISCARD: {
        stimmzettelManager.revokeDiscardedKandidat(kandidatId);
        break;
      }
    }
  } else if (isStimmzettelEvent(command)) {
    saveCurrentStimmzettelSnapshot();
  }
}

const MAX_VALID_VOTES_PER_KANDIDAT = 3;
const MAX_TOTAL_VOTES = 20;

const stimmzettelManager = getStimmzettelManger(
  {
    wahlId: "wahlId",
    wahlbezirkId: "wahlbezirkId",
  },
  {
    maxValidVotesPerKandidat: MAX_VALID_VOTES_PER_KANDIDAT,
    maxTotalVotes: MAX_TOTAL_VOTES,
  }
);
stimmzettelManager.setWahlvorschlaege(wahlvorschlaege.wahlvorschlaege);

const stimmzettelWahlvorschlaege =
  stimmzettelManager.stimmzettelWahlvorschlaege;

// Aktives Element für v-slide-group; mit mandatory wird initial das erste Item ausgewählt
const activeWahlvorschlagId = ref<string | null>(
  stimmzettelWahlvorschlaege.value.length > 0
    ? stimmzettelWahlvorschlaege.value[0].identifikator
    : null
);

const totalUserVotes = stimmzettelManager.totalKandidatenScores;

function setActiveByOrdnungszahl(ordnungszahl: number) {
  const item = stimmzettelWahlvorschlaege.value.find(
    (w) => w.ordnungszahl === ordnungszahl
  );
  if (item) {
    activeWahlvorschlagId.value = item.identifikator;
  }
}
</script>
<style scoped>
.slide-item-wrapper {
  min-width: 300px;
  max-width: 900px;
}
.slide-item-scroll {
  max-height: 60vh; /* Sichtfenster-Höhe, bei Bedarf anpassen */
  overflow-y: auto;
}
</style>
