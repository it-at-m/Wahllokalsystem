<template>
  <base-dialog
    :visible="visible"
    dialogtitle="Stimmzettelerfassung abbrechen"
    confirmtext="Erfassung abbrechen"
    canceltext="Weiter erfassen"
    icon="$information"
  >
    <div>
      Sie befinden sich gegenwärtig in der aktiven Erfassung eines Stimmzettels
      mit der Nummer:
    </div>
    <base-stimmzettelkennung-strong-text
      :team-name="teamName"
      :stimmzettelkennung="stimmzettelkennung"
    />
    <div>
      Bitte bestätigen Sie, dass Sie beabsichtigen, die Erfassung vor der
      Fertigstellung des Stimmzettels abzubrechen und die Stimmzettelkennung
      wieder freizugeben.
    </div>
    <div class="text-warning">
      Ein fehlender oder leerer Stimmzettel muss als Sonderfall erfasst werden!
    </div>
  </base-dialog>
</template>

<script setup lang="ts">
import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import BaseStimmzettelkennungStrongText from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelkennungStrongText.vue";

defineProps({
  visible: Boolean,
  teamName: {
    type: String,
    required: true,
  },
  stimmzettelkennung: {
    type: Number,
    required: true,
  },
});
</script>
