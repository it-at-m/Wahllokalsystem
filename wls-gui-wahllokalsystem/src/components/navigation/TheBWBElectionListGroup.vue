<template>
  <v-list-group value="Wahlhandlung">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="Wahlbriefzulassung"
        :lines="groupActivatorListItemLines"
      >
        <template
          v-if="disabled"
          #subtitle
        >
          {{ disabledMessage }}
        </template>
        <template
          v-if="disabled"
          #prepend
        >
          <v-icon
            :icon="disabled ? '$disabled' : ''"
            size="small"
          />
        </template>
      </v-list-item>
    </template>
    <v-list-item
      title="Beginn Stimmabgabe"
      :to="routeWithName(ROUTE_BEGINN_STIMMABGABE)"
      :disabled="disabled || !isWahlvorstandErfasst"
    >
      <template #prepend>
        <v-icon
          :icon="
            disabled
              ? '$disabled'
              : isWahleroeffnungErfasst
                ? '$valid'
                : '$edit'
          "
          size="small"
        />
      </template>
    </v-list-item>
    <v-list-item
      title="Wahlumgebung"
      :to="routeWithName(ROUTE_WAHLUMGEBUNG)"
      :disabled="disabled || !isWahleroeffnungErfasst"
    >
      <template #prepend>
        <v-icon
          :icon="
            disabled || !isWahleroeffnungErfasst
              ? '$disabled'
              : isWahlumgebungErfasst
                ? '$valid'
                : '$edit'
          "
          size="small"
        />
      </template>
    </v-list-item>
    <v-list-item
      title="Wahlbriefe erfassen"
      :to="routeWithName(ROUTE_ERFASSUNG_WAHLBRIEFE)"
      :disabled="disabled || !isWahlumgebungErfasst"
    >
      <template #prepend>
        <v-icon
          :icon="
            disabled || !isWahlumgebungErfasst
              ? '$disabled'
              : isWahlbriefeErfassenErfasst
                ? '$valid'
                : '$edit'
          "
          size="small"
        />
      </template>
    </v-list-item>
    <v-list-item
      title="Wahlbriefe zulassen"
      :to="routeWithName(ROUTE_WAHLBRIEFE_ZULASSEN)"
      :disabled="disabled || !isWahlbriefeErfassenErfasst"
    >
      <template #prepend>
        <v-icon
          :icon="
            disabled || !isWahlbriefeErfassenErfasst
              ? '$disabled'
              : isWahlbriefeZulassenErfasst
                ? '$valid'
                : '$edit'
          "
          size="small"
        />
      </template>
    </v-list-item>
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
import { useWorkflowStore } from "@/stores/workflowStore.ts";

const properties = defineProps({
  disabled: {
    type: Boolean,
    default: false,
  },
  disabledMessage: {
    type: String,
    default: "",
  },
});

const { routeWithName } = useNavigationUtils();
const {
  isWahlumgebungErfasst,
  isWahleroeffnungErfasst,
  isWahlbriefeErfassenErfasst,
  isWahlbriefeZulassenErfasst,
  isWahlvorstandErfasst,
} = storeToRefs(useWorkflowStore());

const groupActivatorListItemLines = computed(() =>
  properties.disabledMessage && properties.disabled ? false : "one"
);
</script>
