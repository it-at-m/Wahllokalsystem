<template>
  <div>
    <v-breadcrumbs :items="breadcrumbs" />
    <div class="overflow-hidden">
      <div v-if="activeGroup === null">
        <v-btn
          v-for="i in countGroups"
          :key="i"
          class="touchBtn ma-2"
          @click="selectGroup(i)"
        >
          {{ rangeOf(i) }}
        </v-btn>
        <v-btn
          class="touchBtn ma-2"
          @click="endList()"
          >X</v-btn
        >
      </div>
      <div v-if="activeGroup !== null && !activeWahlvorschlag">
        <div class="d-flex flex-wrap">
          <v-btn
            v-for="wahlvorschlag in wahlvorchlaegeOfActiveGroup"
            :key="wahlvorschlag.nummer"
            class="touchBtn ma-2"
            @click="selectWahlvorschlag(wahlvorschlag.nummer)"
            >{{ wahlvorschlag.nummer }}</v-btn
          >
          <v-btn
            class="touchBtn ma-2"
            @click="abortGroup()"
            >X</v-btn
          >
        </div>
      </div>
      <div v-if="activeWahlvorschlag">
        <div>
          <div class="d-flex flex-wrap">
            <v-btn
              v-for="voteCount in maxAllowedVotes"
              :key="voteCount"
              class="touchBtn ma-2"
              @click="addVote(activeWahlvorschlag.nummer, voteCount)"
              >{{ voteCount }}</v-btn
            >
            <v-btn
              class="touchBtn ma-2"
              @click="abortGivingVotes()"
              >X</v-btn
            >
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Wahlvorschlag } from "@/types/wahlvorschlag/Wahlvorschlag";
import type { WahlvorschlagListe } from "@/types/wahlvorschlag/WahlvorschlagListe";
import type { PropType, Ref } from "vue";

import { computed, ref } from "vue";
import { VBreadcrumbs, VBtn } from "vuetify/components";

const props = defineProps({
  wahlvorschlagListe: {
    type: Object as PropType<WahlvorschlagListe>,
    required: true,
  },
  list: {
    type: String,
    required: true,
  },
});

const groupSize = 10;

const activeGroup: Ref<number | null> = ref(null);
const activeWahlvorschlagNummer: Ref<number | null> = ref(null);
const maxAllowedVotes = 3;

const emits =
  defineEmits<
    (
      e: "addVote",
      paylod: { wahlvorschlagNummer: number; votes: number }
    ) => void
  >();

const countGroups = computed(() =>
  Math.ceil(props.wahlvorschlagListe.wahlvorschlaege.length / groupSize)
);

const breadcrumbs = computed(() => {
  const result = [];
  result.push(props.list);

  let minNummer = null;
  let maxNummer = null;
  if (wahlvorchlaegeOfActiveGroup.value.length > 0) {
    minNummer = wahlvorchlaegeOfActiveGroup.value[0].nummer;
    maxNummer =
      wahlvorchlaegeOfActiveGroup.value[
        wahlvorchlaegeOfActiveGroup.value.length - 1
      ].nummer;
  }
  if (minNummer && maxNummer) {
    result.push(`${minNummer} - ${maxNummer}`);
  }

  if (activeWahlvorschlag.value) {
    result.push(
      `${activeWahlvorschlag.value.nummer} - ${activeWahlvorschlag.value.name}`
    );
  }
  return result;
});

const wahlvorchlaegeOfActiveGroup = computed(() => {
  if (activeGroup.value !== null) {
    return props.wahlvorschlagListe.wahlvorschlaege.slice(
      (activeGroup.value - 1) * groupSize,
      activeGroup.value * groupSize
    );
  } else {
    return [] as Wahlvorschlag[];
  }
});
const activeWahlvorschlag = computed(() =>
  props.wahlvorschlagListe.wahlvorschlaege.find(
    (w) => w.nummer === activeWahlvorschlagNummer.value
  )
);

function endList() {
  alert("List ended");
}

function abortGivingVotes() {
  activeWahlvorschlagNummer.value = null;
  activeGroup.value = null;
}

function abortGroup() {
  activeGroup.value = null;
}

function addVote(wahlvorschlagNummer: number, voteCount: number) {
  emits("addVote", {
    wahlvorschlagNummer: wahlvorschlagNummer,
    votes: voteCount,
  });
  abortGivingVotes();
}

function selectGroup(i: number) {
  activeGroup.value = i;
}

function selectWahlvorschlag(i: number) {
  activeWahlvorschlagNummer.value = i;
}

function rangeOf(groupNumber: number): string {
  const min = 100 + (groupNumber - 1) * groupSize + 1;
  const max = 100 + groupNumber * groupSize;

  return `${min} - ${max}`;
}
</script>

<style scoped>
.touchBtn {
  height: 96px;
  width: 96px;
}
</style>
