import BaseKandidatListItemContent from "@/components/dse/BaseKandidatListItemContent.vue";

export default {
  title: "components/dse/BaseKandidatListItemContent",
  component: BaseKandidatListItemContent,
};

const sampleDefault = {
  kandidat: {
    identifikator: "101",
    name: "Max Mustermann",
    gesamtStimmen: 6,
    ungueltigeStimmen: 3,
    gueltigeStimmen: 3,
    restStimmen: 0,
    durchgestrichen: false,
  },
};

const sampleRest = {
  kandidat: {
    identifikator: "102",
    name: "Erika Beispiel",
    gesamtStimmen: 0,
    ungueltigeStimmen: 0,
    gueltigeStimmen: 0,
    restStimmen: 1,
    durchgestrichen: false,
  },
};

const sampleUngueltig = {
  kandidat: {
    identifikator: "102",
    name: "Merlin Mustermann",
    gesamtStimmen: 0,
    ungueltigeStimmen: 3,
    gueltigeStimmen: 0,
    restStimmen: 0,
    durchgestrichen: false,
  },
};

const sampleStrikethrough = {
  kandidat: {
    identifikator: "102",
    name: "Merlin Mustermann",
    gesamtStimmen: 0,
    ungueltigeStimmen: 0,
    gueltigeStimmen: 0,
    restStimmen: 0,
    durchgestrichen: true,
  },
};

export const Default = {
  args: sampleDefault,
};

export const Reststimmen = {
  args: sampleRest,
};

export const Ungueltig = {
  args: sampleUngueltig,
};

export const Durchgestrichen = {
  args: sampleStrikethrough,
};
