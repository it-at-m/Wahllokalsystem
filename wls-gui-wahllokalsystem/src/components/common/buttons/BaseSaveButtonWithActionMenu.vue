<template>
  <v-btn-group
    color="primary"
    density="compact"
  >
    <base-wls-button-save
      :disabled="disabled"
      :save-text="currentAction.title"
      @click="executeSelectedAction"
    />

    <v-menu transition="scale-transition">
      <template #activator="{ props }">
        <base-button-folding
          v-bind="props"
          :disabled="disabled"
          class="border-primary"
          active
        />
      </template>
      <v-list>
        <v-list-item
          v-for="(action, index) in actions"
          :key="index"
          @click="selectAction(action)"
        >
          <v-list-item-title
            :class="currentAction.title == action.title ? 'text-grey' : ''"
            >{{ action.title }}</v-list-item-title
          >
        </v-list-item>
      </v-list>
    </v-menu>
  </v-btn-group>
</template>

<script setup lang="ts">
import type { PropType } from "vue";

import BaseButtonFolding from "@/components/common/buttons/BaseButtonFolding.vue";
import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";

interface Action {
  title: string;
  action: () => void;
}

const currentAction = defineModel("modelValue", {
  type: Object as PropType<Action>,
  required: true,
});

defineProps({
  actions: {
    type: Object as PropType<Action[]>,
    required: true,
  },
  disabled: {
    type: Boolean,
    required: false,
    default: false,
  },
});

function selectAction(action: Action) {
  currentAction.value = action;
}

function executeSelectedAction() {
  currentAction.value.action();
}
</script>
