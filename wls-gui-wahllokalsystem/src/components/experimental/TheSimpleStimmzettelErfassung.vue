<template>
  <div class="ms-1">
    <v-row class="d-flex flex-row align-items-center w-100">
      <v-col
        cols="2"
        class="d-flex flex-column ga-2"
      >
        <v-expansion-panels>
          <v-expansion-panel>
            <v-expansion-panel-title>Kurzbefehle</v-expansion-panel-title>
            <v-expansion-panel-text>
              <div>Befehle bestehen aus &lt;Object&gt; und &lt;action&gt;</div>
              <strong>Objects</strong>
              <ul>
                <li><em>&lt;Ordnungszahl&gt;</em> ... Kandidat</li>
                <li><em>&lt;OZ1&gt;,,&lt;OZ2&gt;</em> ... Kandidatenbereich</li>
                <li>
                  <em>&lt;OZx&gt;,&lt;OZy&gt;,&lt;OZn&gt;</em> ... Liste an
                  Kandidaten
                </li>
                <li><em>&lt;Listennummer&gt;</em> ... Wahlvorschlag</li>
              </ul>
              <strong>Actions</strong>
              <ul>
                <li><em>+</em> ... 1 Stimmen setzen</li>
                <li><em>+x</em> ... x Stimmen setzen</li>
                <li><em>--</em> ... Streichen/Entfernen</li>
              </ul>
              <strong>sonstige Befehle</strong>
              <ul>
                <li><em>000</em> ... leeren Stimmzettel erfassen</li>
                <li><em>***</em> ... für Beschluss vormerken</li>
                <li><em>,,,</em> ... Stimmzettel abschließen</li>
              </ul>
            </v-expansion-panel-text>
          </v-expansion-panel>
        </v-expansion-panels>
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
      </v-col>
      <v-col>
        <base-form-stimmzettel-quick-input @command="onQuickInputCommand" />
        <div class="d-flex ga-2 flex-row mb-1">
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
import type { InputHistoryItem } from "@/types/experimental/InputHistoryItem.ts";
import type { StimmzettelWahlvorschlag } from "@/types/experimental/StimmzettelWahlvorschlag.ts";
import type { PropType } from "vue";

import { computed } from "vue";

import BaseButtonKandidatDiscard from "@/components/experimental/BaseButtonKandidatDiscard.vue";
import BaseFormStimmzettelQuickInput from "@/components/experimental/BaseFormStimmzettelQuickInput.vue";
import BaseKandidateVotes from "@/components/experimental/BaseKandidateVotes.vue";
import InputHistoryIcon from "@/components/experimental/InputHistoryIcon.vue";

const { votesOnly, changeHistory } = defineProps({
  votesOnly: {
    type: Array as PropType<StimmzettelWahlvorschlag[]>,
    required: true,
  },
  changeHistory: {
    type: Array as PropType<InputHistoryItem[]>,
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

const firstHistoryItem = computed(() =>
  changeHistory.length > 0 ? changeHistory[0] : null
);
const nextFiveItems = computed(() =>
  changeHistory.filter((_, index) => index < 5 && index > 0)
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
