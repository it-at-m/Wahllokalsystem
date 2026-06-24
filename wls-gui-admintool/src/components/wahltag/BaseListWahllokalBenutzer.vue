<template>
  <div
    class="benutzer-ledger"
    data-test="list-wahllokalbenutzer"
  >
    <div
      class="benutzer-ledger__head d-flex align-center justify-space-between flex-wrap ga-2 mb-2"
    >
      <div
        class="benutzer-ledger__summary"
        data-test="benutzer-summary"
      >
        <span class="benutzer-ledger__count">{{ userCount }}</span>
        Benutzer
        <span class="text-medium-emphasis">
          · {{ wahlbezirkCount }}
          {{ wahlbezirkCount === 1 ? "Wahlbezirk" : "Wahlbezirke" }}
        </span>
      </div>
      <v-btn
        :prepend-icon="copied ? mdiCheck : mdiContentCopy"
        :disabled="userCount === 0"
        variant="text"
        size="small"
        data-test="copy-benutzer"
        @click="onCopyClicked"
        >{{ copied ? "Kopiert" : "Kopieren" }}</v-btn
      >
    </div>

    <v-text-field
      v-model="searchTerm"
      :prepend-inner-icon="mdiMagnify"
      density="compact"
      variant="outlined"
      label="Wahlbezirk oder Kennung suchen"
      clearable
      hide-details
      class="mb-2"
      data-test="search-benutzer"
    />

    <template v-if="filteredRows.length > 0">
      <div
        class="benutzer-ledger__rows"
        role="list"
      >
        <div
          v-for="row in pagedRows"
          :key="row.username"
          class="benutzer-ledger__row"
          role="listitem"
          data-test="benutzer-row"
        >
          <span class="benutzer-ledger__wahlbezirk">{{ row.wahlbezirk }}</span>
          <span class="benutzer-ledger__username">{{ row.username }}</span>
        </div>
      </div>

      <div
        class="d-flex align-center justify-space-between flex-wrap ga-2 mt-2"
      >
        <span
          class="text-caption text-medium-emphasis"
          data-test="benutzer-range"
        >
          {{ rangeStart }}–{{ rangeEnd }} von {{ filteredRows.length }}
        </span>
        <v-pagination
          v-if="pageCount > 1"
          v-model="page"
          :length="pageCount"
          :total-visible="5"
          density="comfortable"
          data-test="benutzer-pagination"
        />
      </div>
    </template>
    <div
      v-else
      class="text-medium-emphasis py-4 text-center"
      data-test="benutzer-empty"
    >
      Keine Benutzer gefunden.
    </div>
  </div>
</template>
<script setup lang="ts">
import { mdiCheck, mdiContentCopy, mdiMagnify } from "@mdi/js";
import { computed, ref, watch } from "vue";
import { VBtn, VPagination, VTextField } from "vuetify/components";

import { useWahllokalBenutzerFormatter } from "@/composables/wahllokalbenutzer/wahllokalbenutzerFormatter.ts";

const PAGE_SIZE = 50;
const COPIED_RESET_DELAY_MS = 2000;

const { parseBenutzer } = useWahllokalBenutzerFormatter();

const props = defineProps({
  csv: {
    type: String,
    required: true,
  },
});

const searchTerm = ref("");
const copied = ref(false);
const page = ref(1);

const rows = computed(() => parseBenutzer(props.csv));

const userCount = computed(() => rows.value.length);
const wahlbezirkCount = computed(
  () => new Set(rows.value.map((row) => row.wahlbezirk)).size
);

const filteredRows = computed(() => {
  const term = searchTerm.value?.trim().toLowerCase() ?? "";
  if (term.length === 0) {
    return rows.value;
  }
  return rows.value.filter(
    (row) =>
      row.username.toLowerCase().includes(term) ||
      row.wahlbezirk.toLowerCase().includes(term)
  );
});

const pageCount = computed(() =>
  Math.max(1, Math.ceil(filteredRows.value.length / PAGE_SIZE))
);
const pagedRows = computed(() =>
  filteredRows.value.slice((page.value - 1) * PAGE_SIZE, page.value * PAGE_SIZE)
);
const rangeStart = computed(() =>
  filteredRows.value.length === 0 ? 0 : (page.value - 1) * PAGE_SIZE + 1
);
const rangeEnd = computed(() =>
  Math.min(page.value * PAGE_SIZE, filteredRows.value.length)
);

// Bei neuer Suche oder neuen Daten zurück auf die erste Seite.
watch([searchTerm, () => props.csv], () => {
  page.value = 1;
});

async function onCopyClicked() {
  if (!navigator.clipboard) {
    return;
  }
  await navigator.clipboard.writeText(props.csv);
  copied.value = true;
  setTimeout(() => {
    copied.value = false;
  }, COPIED_RESET_DELAY_MS);
}
</script>
<style scoped>
.benutzer-ledger__count {
  font-size: 1.25rem;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
}

.benutzer-ledger__rows {
  border-top: 1px solid rgba(var(--v-theme-on-surface), 0.08);
}

.benutzer-ledger__row {
  display: grid;
  grid-template-columns: 7ch 1fr;
  gap: 1rem;
  align-items: baseline;
  padding: 6px 12px;
  border-left: 3px solid rgb(var(--v-theme-primary));
  border-bottom: 1px solid rgba(var(--v-theme-on-surface), 0.06);
}

.benutzer-ledger__row:nth-child(even) {
  background: rgba(var(--v-theme-on-surface), 0.03);
}

.benutzer-ledger__wahlbezirk {
  font-weight: 600;
  font-variant-numeric: tabular-nums;
}

.benutzer-ledger__username {
  font-family: "Roboto Mono", ui-monospace, "SFMono-Regular", Menlo, monospace;
  letter-spacing: 0.02em;
  word-break: break-all;
}
</style>
