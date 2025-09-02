<template>
  <v-list-group value="OBW_Scores">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="🚧 Wahl des Oberbürgermeisters"
      />
    </template>
    <v-list-item title="Zählen der Stimmzettel" />
    <v-list-item title="Stapel c" />
    <v-list-item title="Stapel b" />
    <v-list-item title="Stapel a" />
    <v-list-item title="Schnellmeldung" />
    <v-list-item title="Niederschrift" />
  </v-list-group>
</template>
