<template>
  <div>
    <base-form-stimmzettel-quick-input @command="onQuickInputCommand" />
    <v-row class="d-flex flex-row align-items-center w-100">
      <v-col cols="3">
        <v-card class="ms-1 mb-1">
          <v-card-title>Eingabehistorie</v-card-title>
          <v-card-text>
            <div class="d-flex align-center ga-1 font-weight-bold">
              <v-icon icon="$stimmzettelCommandAddVote" />
              <div>
                <div>105 +1</div>
                <div>Max Müller</div>
              </div>
            </div>
            <div class="my-4"><v-divider thickness="4" /></div>
            <div class="d-flex align-center ga-1">
              <v-icon icon="$stimmzettelCommandAcceptList" />
              <div>
                <div>Wahlvorschlag Nr. 2</div>
              </div>
            </div>
            <div class="my-2"><v-divider thickness="1" /></div>
            <div class="d-flex align-center ga-1">
              <v-icon icon="$stimmzettelCommandDiscardKandidat" />
              <div>
                <div>202</div>
                <div>Hassan Al-Rashid</div>
              </div>
            </div>
            <div class="my-2"><v-divider thickness="1" /></div>
            <div class="d-flex align-center ga-1 mt-3">
              <v-icon icon="$stimmzettelCommandRemoveVote" />
              <div>
                <div>105 +7</div>
                <div>Max Müller</div>
              </div>
            </div>
            <div class="my-2"><v-divider thickness="1" /></div>
            <div class="d-flex align-center ga-1 mt-3">
              <v-icon icon="$stimmzettelCommandAddVote" />
              <div>
                <div>105 +7</div>
                <div>Max Müller</div>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col>
        <div>
          <template v-if="wahlvorschlaegeWithDecisions.length > 0">
            <v-card
              v-for="(wahlvorschlag, index) in wahlvorschlaegeWithDecisions"
              :key="index"
            >
              <v-card-title>
                <div class="d-flex align-center ga-2">
                  <v-icon
                    v-if="wahlvorschlag.isSelected"
                    icon="$stimmzettelCommandAcceptList"
                  />
                  <div
                    v-else
                    style="min-width: 30px"
                  />
                  Wahlvorschlag Nr. {{ wahlvorschlag.ordnungszahl }}
                </div>
              </v-card-title>
              <v-card-text>
                <div
                  v-for="(kandidat, index) in getKandidatenWithVotes(
                    wahlvorschlag
                  )"
                  :key="index"
                >
                  <div class="d-flex align-center ga-2">
                    <div>{{ kandidat.listenposition }}</div>
                    <div>{{ kandidat.name }}</div>
                    <base-kandidate-votes :kandidat="kandidat" />
                  </div>
                </div>
                <v-divider
                  thickness="2"
                  class="my-2"
                />
                <div
                  v-for="(kandidat, index) in getKandidatenDiscarded(
                    wahlvorschlag
                  )"
                  :key="index"
                >
                  <div class="d-flex align-center ga-2">
                    <base-button-kandidat-discard
                      :disabled="true"
                      :model-value="kandidat.isDiscarded"
                    />
                    <div>{{ kandidat.listenposition }}</div>
                    <div>{{ kandidat.name }}</div>
                    <base-kandidate-votes :kandidat="kandidat" />
                  </div>
                </div>
              </v-card-text>
            </v-card>
          </template>
          <div v-else>Es wurden noch keine Stimmen vergeben.</div>
        </div>
      </v-col>
    </v-row>
  </div>
</template>

<script setup lang="ts">
import type { AbstractCommandEvent } from "@/types/experimental/AbstractCommandEvent.ts";
import type { StimmzettelWahlvorschlag } from "@/types/experimental/StimmzettelWahlvorschlag.ts";
import type { PropType } from "vue";

import { computed } from "vue";

import BaseButtonKandidatDiscard from "@/components/experimental/BaseButtonKandidatDiscard.vue";
import BaseFormStimmzettelQuickInput from "@/components/experimental/BaseFormStimmzettelQuickInput.vue";
import BaseKandidateVotes from "@/components/experimental/BaseKandidateVotes.vue";

const { votesOnly } = defineProps({
  votesOnly: {
    type: Array as PropType<StimmzettelWahlvorschlag[]>,
    required: true,
  },
});

const emit = defineEmits<{
  command: [command: AbstractCommandEvent];
}>();

const wahlvorschlaegeWithDecisions = computed(() =>
  votesOnly.filter(
    (wahlvorschlag) =>
      wahlvorschlag.isSelected ||
      wahlvorschlag.kandidaten.some(
        (kandidat) => kandidat.votesByVoter > 0 || kandidat.isDiscarded
      )
  )
);

function getKandidatenWithVotes(wahlvorschlag: StimmzettelWahlvorschlag) {
  return wahlvorschlag.kandidaten.filter(
    (kandidat) => kandidat.votesByVoter > 0 || kandidat.votesByWahlvorschlag > 0
  );
}

function getKandidatenDiscarded(wahlvorschlag: StimmzettelWahlvorschlag) {
  return wahlvorschlag.kandidaten.filter((kandidat) => kandidat.isDiscarded);
}

function onQuickInputCommand(command: AbstractCommandEvent) {
  emit("command", command);
}
</script>
