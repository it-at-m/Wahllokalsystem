<template>
  <v-card>
    <v-card-title v-if="showTitle">Stimmzettelerfassung </v-card-title>
    <v-card-text>
      <v-tabs-window v-model="subViewStimmzettelerfassung">
        <v-tabs-window-item value="1">
          <base-form-stimmzettel-quick-input @command="onQuickInputCommand" />
          <div class="d-flex ga-1 mb-3">
            <v-btn
              :loading="isSavingStimmzettel"
              @click="onSaveClicked"
              >Speichern</v-btn
            >
            <v-btn @click="onResetStimmzettelClicked">Zurücksetzen</v-btn>
          </div>
          <v-row>
            <v-col
              v-for="wahlvorschlag in stimmzettelWahlvorschlaege"
              :key="wahlvorschlag.identifikator"
            >
              <base-wahlvorschlag-scores-card
                :wahlvorschlag="wahlvorschlag"
                :max-total-votes="MAX_TOTAL_VOTES"
                :total-user-votes="totalUserVotes"
              />
            </v-col>
          </v-row>
        </v-tabs-window-item>

        <v-tabs-window-item value="4">
          <the-simple-stimmzettel-erfassung
            :votes-only="stimmzettelWahlvorschlaege"
            :change-history="changeHistory"
            @command="onQuickInputCommand"
          />
        </v-tabs-window-item>

        <v-tabs-window-item value="2">
          <the-stimmzettel-summary-card />
        </v-tabs-window-item>

        <v-tabs-window-item value="3">
          <div v-if="stimmzettelSnapshots.length > 0">
            <template
              v-for="(snapshot, index) in stimmzettelSnapshots"
              :key="index"
            >
              <div>
                {{ index }}
                <v-btn
                  class="ms-2"
                  @click="onLoadStimmzettelSnapshotClicked(snapshot)"
                  >Laden</v-btn
                >
              </div>
              <div>
                Anzahl erfasster Kandidaten:
                {{ snapshot.kandidatenSnapshot.length }}
              </div>
              <div>
                Anzahl gesetzter Listenkreuze:
                {{ snapshot.selectedWahlvorschlaegeOrdnungszahlen.length }}
              </div>
              <v-divider
                v-if="index < stimmzettelSnapshots.length - 1"
                class="my-2"
              />
            </template>
          </div>
          <div v-else>Keine gespeicherten Stimmzettel</div>
        </v-tabs-window-item>
        <v-tabs-window-item value="5">
          <the-mocked-stimmzettel-overview-card />
        </v-tabs-window-item>
      </v-tabs-window>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { AbstractCommandEvent } from "@/types/experimental/AbstractCommandEvent.ts";
import type { KandidatEvent } from "@/types/experimental/KandidatEvent.ts";
import type { StimmzettelEvent } from "@/types/experimental/StimmzettelEvent.ts";
import type { StimmzettelSnapshot } from "@/types/experimental/StimmzettelSnapshot.ts";
import type { WahlvorschlagEvent } from "@/types/experimental/WahlvorschlagEvent.ts";
import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";
import type { PropType } from "vue";

import { storeToRefs } from "pinia";
import { computed } from "vue";

import BaseFormStimmzettelQuickInput from "@/components/experimental/BaseFormStimmzettelQuickInput.vue";
import BaseWahlvorschlagScoresCard from "@/components/experimental/BaseWahlvorschlagScoresCard.vue";
import TheMockedStimmzettelOverviewCard from "@/components/experimental/TheMockedStimmzettelOverviewCard.vue";
import TheSimpleStimmzettelErfassung from "@/components/experimental/TheSimpleStimmzettelErfassung.vue";
import TheStimmzettelSummaryCard from "@/components/experimental/TheStimmzettelSummaryCard.vue";
import { useLogging } from "@/composables/common/logging.ts";
import { getStimmzettelManger } from "@/composables/experimental/stimmzettelManager.ts";
import { useExperimentalFeaturesStore } from "@/stores/experimentalFeaturesStore.ts";
import { KandidatEventTypeEnum } from "@/types/experimental/KandidatEventTypeEnum.ts";
import { StimmzettelEventTypeEnum } from "@/types/experimental/StimmzettelEventTypeEnum.ts";
import { WahlvorschlagEventTypeEnum } from "@/types/experimental/WahlvorschlagEventTypeEnum.ts";

const MAX_VALID_VOTES_PER_KANDIDAT = 3;
const MAX_TOTAL_VOTES = 20;

const props = defineProps({
  wahlvorschlaege: {
    type: Object as PropType<Wahlvorschlaege>,
    required: true,
  },
  stimmzettelSnapshots: {
    type: Array as PropType<StimmzettelSnapshot[]>,
    required: true,
  },
  isSavingStimmzettel: {
    type: Boolean,
    required: false,
    default: false,
  },
  showTitle: {
    type: Boolean,
    required: false,
    default: true,
  },
});

const emit = defineEmits<{
  snapshotCreated: [stimmzettelSnapshot: StimmzettelSnapshot];
}>();

const logger = useLogging("TheStimmzettelScoresCard");
const { subViewStimmzettelerfassung } = storeToRefs(
  useExperimentalFeaturesStore()
);

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
stimmzettelManager.setWahlvorschlaege(props.wahlvorschlaege.wahlvorschlaege);

const stimmzettelWahlvorschlaege =
  stimmzettelManager.stimmzettelWahlvorschlaege;
const changeHistory = computed(() =>
  stimmzettelManager.changeHistory.value.toReversed()
);
const totalUserVotes = stimmzettelManager.totalKandidatenScores;

function onLoadStimmzettelSnapshotClicked(snapshot: StimmzettelSnapshot) {
  stimmzettelManager.loadSnapshot(snapshot);
}

function onQuickInputCommand(command: AbstractCommandEvent) {
  logger.log(`processing command > ${JSON.stringify(command)}`);

  if (isWahlvorschlagEvent(command)) {
    logger.log(`isWahlvorschlagEvent`);
    stimmzettelManager.selectWahlvorschlagByOrdnungszahl(
      command.wahlvorschlagOrdnungszahl
    );
  } else if (isKandidatEvent(command)) {
    logger.log(`isKandidatEvent`);
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
    }
  } else if (isStimmzettelEvent(command)) {
    saveCurrentStimmzettelSnapshot();
  }
}

function onResetStimmzettelClicked() {
  stimmzettelManager.reset();
}

function onSaveClicked() {
  saveCurrentStimmzettelSnapshot();
}

function saveCurrentStimmzettelSnapshot() {
  const stimmzettelSnapshot = stimmzettelManager.createSnapshot();
  emit("snapshotCreated", stimmzettelSnapshot);
  stimmzettelManager.reset();
}

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

  const hasCorrectType = Object.values(StimmzettelEventTypeEnum).includes(
    e.stimmzettelEventType as never
  );

  return hasCorrectType;
}
</script>

<style scoped></style>
