import { defineStore } from "pinia";

export const storeID = "wahlbezirk";

export const useWahlbezirkStore = defineStore(storeID, () => {
  async function sendSchliessungsuhrzeit() {
    console.log("Sending Schliessungsuhrzeit");
  }

  return { sendSchliessungsuhrzeit };
});
