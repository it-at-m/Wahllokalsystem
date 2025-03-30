<template>
  <div>
    <div v-html="renderedMarkdownContent" />
    <select
      v-model="selectedService"
      name="service"
      id="selected-service"
    >
      <option
        value="ServiceA"
        selected
      >
        ServiceA
      </option>
      <option value="ServiceB">ServiceB</option>
      <option value="ServiceC">ServiceC</option>
      <option value="ServiceD">ServiceD</option>
    </select>
    <mermaid-diagram :diagram="mermaidContentForSelectedService" />
  </div>
</template>

<script setup lang="ts">
import MarkdownIt from "markdown-it";
import mermaid from "mermaid";
import { computed, onMounted, ref, Ref, watch } from "vue";

import MermaidDiagram from "../MermaidDiagram.vue";

const renderMermaid = () => {
  mermaid.initialize({ startOnLoad: true });
  mermaid.contentLoaded();
};

const selectedService: Ref<string | null> = ref(null);

const relations: { source: string; target: string }[] = [
  { source: "ServiceA", target: "ServiceB" },
  { source: "ServiceA", target: "ServiceC" },
  { source: "ServiceA", target: "ServiceD" },
  { source: "ServiceB", target: "ServiceC" },
  { source: "ServiceB", target: "ServiceD" },
];

const markdownContent = `
# Heading via Markdown
`;

const mermaidContentForSelectedService = computed(
  () => `flowchart TD

${createFlowRelations()}
`
);

onMounted(() => renderMermaid());
watch(() => mermaidContentForSelectedService, renderMermaid);

const renderedMarkdownContent = new MarkdownIt().render(markdownContent);

function createFlowRelations(): string {
  const relationsArray = relations.map((relation) =>
    createFlowRelation(relation.source, relation.target)
  );

  return relationsArray.join("\n");
}

function createFlowRelation(source: string, target: string): string {
  if (
    (selectedService.value &&
      (selectedService.value === source || selectedService.value === target)) ||
    !selectedService.value
  ) {
    return `${source} --> ${target}`;
  } else {
    return "";
  }
}

renderMermaid();
</script>
