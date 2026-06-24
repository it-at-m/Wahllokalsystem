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

    <div
      v-if="filteredRows.length > 0"
      class="benutzer-ledger__rows"
      role="list"
    >
      <div
        v-for="row in filteredRows"
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
import { computed, ref } from "vue";
import { VBtn, VTextField } from "vuetify/components";

/**
 * Antwort des Backends, wenn zum Wahltag keine Benutzer existieren. Wird nicht
 * als Benutzername interpretiert.
 */
const NO_USERS_MESSAGE = "Keine Nutzer zum angegebenen Wahltag gefunden.";
const COPIED_RESET_DELAY_MS = 2000;

const props = defineProps({
  csv: {
    type: String,
    required: true,
  },
});

const searchTerm = ref("");
const copied = ref(false);

const rows = computed(() =>
  props.csv
    .split(/\r?\n/)
    .map((line) => line.trim())
    .filter((line) => line.length > 0 && line !== NO_USERS_MESSAGE)
    .map((username) => {
      const separatorIndex = username.lastIndexOf("-");
      return {
        username,
        wahlbezirk:
          separatorIndex >= 0 ? username.slice(separatorIndex + 1) : username,
        kennung: separatorIndex >= 0 ? username.slice(0, separatorIndex) : "",
      };
    })
    .sort((rowA, rowB) =>
      rowA.wahlbezirk.localeCompare(rowB.wahlbezirk, "de", { numeric: true })
    )
);

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
  max-height: 360px;
  overflow-y: auto;
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
