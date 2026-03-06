<template>
  <v-card>
    <v-card-title>Stimmzettelerfassung</v-card-title>
    <v-card-text>
      <v-tabs v-model="tab">
        <v-tab value="1">Erfassung</v-tab>
        <v-tab value="2">Zusammenfassung</v-tab>
      </v-tabs>

      <v-tabs-window v-model="tab">
        <v-tabs-window-item value="1">
          <base-form-stimmzettel-quick-input @command="onQuickInputCommand" />
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
        <v-tabs-window-item value="2">
          <the-stimmzettel-summary-card />
        </v-tabs-window-item>
      </v-tabs-window>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { AbstractCommandEvent } from "@/types/experimental/AbstractCommandEvent.ts";
import type { KandidatEvent } from "@/types/experimental/KandidatEvent.ts";
import type { WahlvorschlagEvent } from "@/types/experimental/WahlvorschlagEvent.ts";
import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";
import type { PropType } from "vue";

import { ref } from "vue";

import BaseFormStimmzettelQuickInput from "@/components/experimental/BaseFormStimmzettelQuickInput.vue";
import BaseWahlvorschlagScoresCard from "@/components/experimental/BaseWahlvorschlagScoresCard.vue";
import TheStimmzettelSummaryCard from "@/components/experimental/TheStimmzettelSummaryCard.vue";
import { useLogging } from "@/composables/common/logging.ts";
import { getStimmzettelManger } from "@/composables/experimental/stimmzettelManager.ts";
import { KandidatEventTypeEnum } from "@/types/experimental/KandidatEventTypeEnum.ts";
import { WahlvorschlagEventTypeEnum } from "@/types/experimental/WahlvorschlagEventTypeEnum.ts";

const MAX_VALID_VOTES_PER_KANDIDAT = 3;
const MAX_TOTAL_VOTES = 20;

const props = defineProps({
  wahlvorschlaege: {
    type: Object as PropType<Wahlvorschlaege>,
    required: true,
  },
});

const tab = ref("1");

const logger = useLogging("TheStimmzettelScoresCard");

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
const totalUserVotes = stimmzettelManager.totalKandidatenScores;

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
  }
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
</script>

<style scoped></style>
