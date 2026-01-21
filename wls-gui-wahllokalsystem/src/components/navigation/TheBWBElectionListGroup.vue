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
          v-if="disablingReason"
          #subtitle
        >
          {{ disablingReason }}
        </template>
      </v-list-item>
    </template>
    <v-list-item
      title="Beginn Stimmabgabe"
      :to="routeWithName(ROUTE_BEGINN_STIMMABGABE)"
    />
    <v-list-item
      title="Wahlumgebung"
      :to="routeWithName(ROUTE_WAHLUMGEBUNG)"
    />
    <v-list-item
      title="Wahlbriefe erfassen"
      :to="routeWithName(ROUTE_ERFASSUNG_WAHLBRIEFE)"
      :disabled="isGroupDisabled || !isWahlumgebungErfasst"
    />
    <v-list-item
      title="Wahlbriefe zulassen"
      :to="routeWithName(ROUTE_WAHLBRIEFE_ZULASSEN)"
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

const properties = defineProps({
  disabled: {
    type: [Boolean, String],
    default: false,
  },
});

const { routeWithName } = useNavigationUtils();
const { isWahlumgebungErfasst } = storeToRefs(useStatusStore());

const isGroupDisabled = computed(() => !!properties.disabled);

const disablingReason = computed(() => {
  return typeof properties.disabled === "string" ? properties.disabled : null;
});

const groupActivatorListItemLines = computed(() =>
  disablingReason.value ? false : "one"
);
</script>
