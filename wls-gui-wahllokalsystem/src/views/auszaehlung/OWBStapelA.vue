<template>
  <the-o-w-b-stapel-a-card
    :wahl-i-d="wahlID"
    :wahlbezirk-i-d="currentUserWahlbezirkID"
  />
</template>

<script setup lang="ts">
import TheOWBStapelACard from "@/components/ergebnisermittlung/OBW/stapelA/TheOBWStapelACard.vue";
import {storeToRefs} from "pinia";
import {useUserStore} from "@/stores/userStore.ts";
import {computed} from "vue";
import {useRoute} from "vue-router";

const route = useRoute();

const {
  currentUserWahlbezirkID,
} = storeToRefs(useUserStore());

const wahlID = computed(() => route.params.wahlId as string);
</script>
