<template>
  <v-list-group value="BAW_Scores">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="🚧 Wahl des Bezirksausschusses"
      />
    </template>
    <v-list-item title="Zählen der Stimmzettel" />
    <v-list-item title="Ungültige Stimmzettel" />
    <v-list-item title="Gültige Stimmzettel" />
    <v-list-item title="Schnellmeldung" />
    <v-list-item title="Kandidatinnen- und Kandidatenstimmen" />
    <v-list-item title="Niederschrift" />
  </v-list-group>
</template>
