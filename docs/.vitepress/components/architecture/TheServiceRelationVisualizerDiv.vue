<template>
  <div class="mt-3">
    <v-autocomplete
      v-model="selectedService"
      :items="BACKENDSERVICES"
      clearable
      persistent-clear
      hide-details
      label="Service"
      id="selected-service"
    />
    <div class="mt-3">
      <div class="ml-0 d-flex align-center">
        <v-btn-toggle
          v-model="selectedDirection"
          density="compact"
          variant="outlined"
          mandatory
        >
          <v-btn value="LR">LR</v-btn>
          <v-btn value="TD">TD</v-btn>
        </v-btn-toggle>
        <span class="ml-2">Ausrichtung</span>
      </div>
      <v-checkbox
        v-model="withOperations"
        label="Operationen anzeigen"
        hide-details
      />
    </div>
    <mermaid-diagram :diagram="mermaidContentForSelectedService" />
  </div>
</template>

<script setup lang="ts">
import { computed, ref, Ref } from "vue";
import { VAutocomplete, VBtn, VBtnToggle, VCheckbox } from "vuetify/components";

import {
  BACKENDSERVICE_RELATIONS,
  BACKENDSERVICES,
} from "../../types/architecture/Constants";
import { Relation } from "../../types/architecture/Relation";
import MermaidDiagram from "../MermaidDiagram.vue";

const selectedService: Ref<string | null> = ref(null);
const selectedDirection = ref("LR");
const withOperations = ref(false);

const selectedRelations = computed(() => {
  let relationsMatchingSelection = !selectedService.value
    ? BACKENDSERVICE_RELATIONS
    : BACKENDSERVICE_RELATIONS.filter(
        (relation) =>
          relation.source === selectedService.value ||
          relation.target === selectedService.value
      );

  if (!withOperations.value) {
    //reduce to distinguish relations
    relationsMatchingSelection = relationsMatchingSelection.filter(
      (relation, index, array) =>
        index === array.findIndex((r) => hasEqualSourceAndTarget(r, relation))
    );
  }

  return relationsMatchingSelection;
});

const mermaidContentForSelectedService = computed(
  () => `flowchart ${selectedDirection.value}

  ${createStyleDefinitions()}

  ${createFlowchartRelations()}
`
);

function createStyleDefinitions() {
  return selectedService.value
    ? `style ${selectedService.value} stroke-width:4px,stroke:#D50000`
    : "";
}

function createFlowchartRelations(): string {
  const relationsArray = selectedRelations.value.map((relation) =>
    createFlowchartRelation(relation, withOperations.value)
  );

  return relationsArray.join("\n");
}

function createFlowchartRelation(
  relation: Relation,
  withDetails = true
): string {
  if (withDetails && relation.operation.trim()) {
    return `${relation.source} -- ${relation.operation} --> ${relation.target}`;
  } else {
    return `${relation.source} --> ${relation.target}`;
  }
}

function hasEqualSourceAndTarget(relation: Relation, other: Relation): boolean {
  return relation.source === other.source && relation.target === other.target;
}
</script>
