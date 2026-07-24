<template>
  <v-icon
    :icon="icon"
    :color="color"
  />
</template>

<script setup lang="ts">
import type { PropType } from "vue";

import { computed } from "vue";

import { InputHistoryTypeEnum } from "@/types/experimental/InputHistoryTypeEnum.ts";

const props = defineProps({
  inputType: { type: String as PropType<InputHistoryTypeEnum>, required: true },
});

const typeMapping: Record<InputHistoryTypeEnum, string> = {
  ADD_USER_VOTE: "$stimmzettelCommandAddVote",
  DISCARD_KANDIDAT: "$stimmzettelCommandDiscardKandidat",
  REMOVE_USER_VOTE: "$stimmzettelCommandRemoveVote",
  REVOKE_DISCARDED_KANDIDAT: "$stimmzettelCommandRevokeDiscardKandidat",
  REVOKE_WAHLVORSCHLAG: "$stimmzettelCommandRemoveAcceptList",
  SET_WAHLVORSCHLAG: "$stimmzettelCommandAcceptList",
};
const colorMapping: Record<InputHistoryTypeEnum, string> = {
  ADD_USER_VOTE: "success",
  DISCARD_KANDIDAT: "success",
  REMOVE_USER_VOTE: "error",
  REVOKE_DISCARDED_KANDIDAT: "error",
  REVOKE_WAHLVORSCHLAG: "error",
  SET_WAHLVORSCHLAG: "success",
};

const icon = computed(() => typeMapping[props.inputType]);
const color = computed(() => colorMapping[props.inputType]);
</script>
