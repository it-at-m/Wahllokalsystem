<template>
  <div>
    <div v-html="renderedMarkdownContent" />
    <v-select
      v-model="selectedService"
      :items="['ServiceA', 'ServiceB', 'ServiceC', 'ServiceD']"
      clearable
      label="Service"
      id="selected-service"
    />

    <v-select
      v-model="direction"
      label="Ausrichtung"
      :items="['TD', 'LR']"
    />
    <v-checkbox
      v-model="withDetails"
      label="Details anzeigen"
    />
    <mermaid-diagram :diagram="mermaidContentForSelectedService" />
  </div>
</template>

<script setup lang="ts">
import MarkdownIt from "markdown-it";
import mermaid from "mermaid";
import { computed, onMounted, ref, Ref, watch } from "vue";
import { VCheckbox, VSelect } from "vuetify/components";

import MermaidDiagram from "../MermaidDiagram.vue";

type Relation = { source: string; target: string; details: string };

const renderMermaid = () => {
  mermaid.initialize({
    startOnLoad: true,
  });
  mermaid.contentLoaded();
};

const selectedService: Ref<string | null> = ref(null);
const direction = ref("TD");
const withDetails = ref(false);
const selectedRelations = computed(() => {
  let relationsMatchingSelection = !selectedService.value
    ? relations
    : relations.filter(
        (relation) =>
          relation.source === selectedService.value ||
          relation.target === selectedService.value
      );

  if (withDetails.value) {
  } else {
    relationsMatchingSelection = relationsMatchingSelection.filter(
      (relation, index, array) =>
        index === array.findIndex((r) => hasEqualSourceAndTarget(r, relation))
    );
  }

  return relationsMatchingSelection;
});

function hasEqualSourceAndTarget(relation: Relation, other: Relation): boolean {
  return relation.source === other.source && relation.target === other.target;
}

const relations: Relation[] = [
  { source: "ServiceA", target: "ServiceB", details: "operationB1" },
  { source: "ServiceA", target: "ServiceB", details: "operationB2" },
  { source: "ServiceA", target: "ServiceC", details: "operationC1" },
  { source: "ServiceA", target: "ServiceD", details: "operationD1" },
  { source: "ServiceB", target: "ServiceC", details: "operationC1" },
  { source: "ServiceB", target: "ServiceD", details: "operationD1" },
];

const markdownContent = `
# Heading via Markdown
`;

const mermaidContentForSelectedService = computed(
  () => `flowchart ${direction.value}

${createFlowRelations()}
`
);

onMounted(() => renderMermaid());
watch(() => mermaidContentForSelectedService, renderMermaid);

const renderedMarkdownContent = new MarkdownIt().render(markdownContent);

function createFlowRelations(): string {
  const relationsArray = selectedRelations.value.map((relation) =>
    createFlowRelation(relation, withDetails.value)
  );

  return relationsArray.join("\n");
}

function createFlowRelation(relation: Relation, withDetails = false): string {
  if (withDetails) {
    return `${relation.source} -->|${relation.details}| ${relation.target}`;
  } else {
    return `${relation.source} --> ${relation.target}`;
  }
}

renderMermaid();
</script>
