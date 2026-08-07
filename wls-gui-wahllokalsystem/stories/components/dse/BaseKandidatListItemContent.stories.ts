import BaseKandidatListItemContent from "@/components/dse/BaseKandidatListItemContent.vue";

export default {
  title: "components/dse/BaseKandidatListItemContent",
  component: BaseKandidatListItemContent,
};

const sampleDefault = {
  id: "101",
  name: "Max Mustermann",
  anzahlStimmen: 6,
  ungueltigeStimmen: 3,
  gueltigeStimmen: 3,
  restStimmenWahlvorschlag: 0,
  durchgestrichen: false,
};

const sampleRest = {
  id: "102",
  name: "Erika Beispiel",
  anzahlStimmen: 0,
  ungueltigeStimmen: 0,
  gueltigeStimmen: 0,
  restStimmenWahlvorschlag: 1,
  durchgestrichen: false,
};

const sampleStrikethrough = {
  id: "102",
  name: "Merlin Mustermann",
  anzahlStimmen: 0,
  ungueltigeStimmen: 0,
  gueltigeStimmen: 0,
  restStimmenWahlvorschlag: 0,
  durchgestrichen: true,
};

export const Default = {
  args: sampleDefault,
};

export const Reststimmen = {
  args: sampleRest,
};

export const Durchgestrichen = {
  args: sampleStrikethrough,
};
