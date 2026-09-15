<template>
  <div>
    <v-row class="mt-2">
      <v-col>
        <v-radio-group>
          <v-radio>
            <template #label>
              Die Stimmabgabe ist gültig
              <v-icon
                icon="$stimmzettelGueltig"
                color="success"
              />
            </template>
          </v-radio>
          <v-radio>
            <template #label>
              Die Stimmabgabe ist ungültig
              <v-icon
                icon="$stimmzettelUngueltig"
                color="error"
              />
            </template>
          </v-radio>
        </v-radio-group>
      </v-col>
      <v-col> y </v-col>
    </v-row>
    <v-card>
      <v-card-title> Abstimmungsergebnis </v-card-title>
    </v-card>
  </div>
</template>


