<template>
  <v-icon :icon="icon" />
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

const icon = computed(() => typeMapping[props.inputType]);
</script>
