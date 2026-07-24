<template>
  <v-card>
    <v-card-title>Ungültige Stimmzettel und Stimmen</v-card-title>
    <v-card-text>
      <div class="mb-2">
        <v-checkbox
          label="Stimmzettel ist leer"
          hide-details
          density="compact"
        />
        <v-checkbox
          label="Stimmzettel fehlt (nur bei BWB)"
          hide-details
          density="compact"
        />
      </div>
      <v-row align="center">
        <v-col cols="7">
          ungültige Stimmen die nicht zugeordnet werden können
        </v-col>
        <v-col cols="4">
          <v-number-input
            control-variant="stacked"
            density="compact"
            hide-details
            width="100"
            :clearable="false"
            :min="0"
          />
        </v-col>
      </v-row>
    </v-card-text>
    <v-card-title>Beschlussfassung</v-card-title>
    <v-card-text>
      <v-checkbox
        :model-value="false"
        label="für Beschlussfassung vormerken"
        class="mb-4"
        density="compact"
      />
      Begründung auswählen oder eingeben
      <v-combobox
        :items="[
          'unzulässiger Zusatz/Vorbehalt',
          'Stimmzettel vollständig durchgestrichen',
          'Wählerwille nicht zweifelsfrei erkennbar',
          'handschriftlich ergänzte Person',
          'Kennzeichnung nicht eindeutig zuzuordnen',
        ]"
        class="combobox-as-textarea mt-1"
        multiple
        chips
        hide-details
      />
    </v-card-text>
  </v-card>
</template>

<style scoped>
.combobox-as-textarea :deep(.v-field) {
  height: 150px; /* Gewünschte Höhe wie bei einer Textarea */
  align-items: flex-start; /* Text/Chips oben beginnen lassen */
  overflow-y: auto;
}
</style>
