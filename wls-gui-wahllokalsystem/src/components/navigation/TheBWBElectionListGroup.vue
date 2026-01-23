<template>
  <v-list-group value="Wahlhandlung">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="Wahlbriefzulassung"
        :disabled="isGroupDisabled"
        :lines="groupActivatorListItemLines"
      >
        <template
          v-if="disabledMessage"
          #subtitle
        >
          {{ disabledMessage }}
        </template>
      </v-list-item>
    </template>
    <v-list-item
      title="Beginn Stimmabgabe"
      :to="routeWithName(ROUTE_BEGINN_STIMMABGABE)"
      :disabled="isGroupDisabled"
    />
    <v-list-item
      title="Wahlumgebung"
      :to="routeWithName(ROUTE_WAHLUMGEBUNG)"
      :disabled="isGroupDisabled"
    />
    <v-list-item
      title="Wahlbriefe erfassen"
      :to="routeWithName(ROUTE_ERFASSUNG_WAHLBRIEFE)"
      :disabled="isGroupDisabled || !isWahlumgebungErfasst"
    />
    <v-list-item
      title="Wahlbriefe zulassen"
      :to="routeWithName(ROUTE_WAHLBRIEFE_ZULASSEN)"
      :disabled="isGroupDisabled"
    />
  </v-list-group>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed } from "vue";

import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import {
  ROUTE_BEGINN_STIMMABGABE,
  ROUTE_ERFASSUNG_WAHLBRIEFE,
  ROUTE_WAHLBRIEFE_ZULASSEN,
  ROUTE_WAHLUMGEBUNG,
} from "@/constants.ts";
import { useStatusStore } from "@/stores/statusStore.ts";

const { disabled } = defineProps({
  disabled: {
    type: [Boolean, String],
    default: false,
  },
});

const { routeWithName } = useNavigationUtils();
const { isWahlumgebungErfasst } = storeToRefs(useStatusStore());

const isGroupDisabled = computed(() => !!disabled);

const disabledMessage = computed(() => {
  return typeof disabled === "string" ? disabled : null;
});

const groupActivatorListItemLines = computed(() =>
  disabledMessage.value ? false : "one"
);
</script>
