import TheStimmzettelContent from "@/components/dse/TheStimmzettelContent.vue";

export default {
  title: "components/dse/TheStimmzettelContent",
  component: TheStimmzettelContent,
};

const sampleWahlvorschlaege = [
  {
    identifikator: "Partei A",
    ordnungszahl: 1,
    kurzname: "A",
    erhaeltStimmen: false,
    kandidaten: [
      {
        identifikator: "101",
        name: "Alice Müller",
        listenposition: 1,
        direktkandidat: false,
        tabellenSpalteInNiederschrift: 1,
        einzelbewerber: false,
        anzahlNennungen: 2,
      },
      {
        identifikator: "102",
        name: "Bernd Schmidt",
        listenposition: 2,
        direktkandidat: false,
        tabellenSpalteInNiederschrift: 1,
        einzelbewerber: false,
        anzahlNennungen: 1,
      },
    ],
  },
  {
    identifikator: "Partei B",
    ordnungszahl: 2,
    kurzname: "B",
    erhaeltStimmen: true,
    kandidaten: [
      {
        identifikator: "201",
        name: "Albert Tester",
        listenposition: 1,
        direktkandidat: false,
        tabellenSpalteInNiederschrift: 1,
        einzelbewerber: false,
        anzahlNennungen: 2,
      },
      {
        identifikator: "202",
        name: "Bine Meier",
        listenposition: 2,
        direktkandidat: false,
        tabellenSpalteInNiederschrift: 1,
        einzelbewerber: false,
        anzahlNennungen: 1,
      },
    ],
  },
  {
    identifikator: "Partei C",
    ordnungszahl: 2,
    kurzname: "C",
    erhaeltStimmen: true,
    kandidaten: [
      {
        identifikator: "301",
        name: "Amina Raab",
        listenposition: 1,
        direktkandidat: false,
        tabellenSpalteInNiederschrift: 1,
        einzelbewerber: false,
        anzahlNennungen: 2,
      },
      {
        identifikator: "302",
        name: "Birgit Tenner",
        listenposition: 2,
        direktkandidat: false,
        tabellenSpalteInNiederschrift: 1,
        einzelbewerber: false,
        anzahlNennungen: 1,
      },
      {
        identifikator: "303",
        name: "Christa Haas",
        listenposition: 3,
        direktkandidat: false,
        tabellenSpalteInNiederschrift: 1,
        einzelbewerber: false,
        anzahlNennungen: 1,
      },
      {
        identifikator: "304",
        name: "Dominik Mausmann",
        listenposition: 4,
        direktkandidat: false,
        tabellenSpalteInNiederschrift: 1,
        einzelbewerber: false,
        anzahlNennungen: 1,
      },
    ],
  },
  {
    identifikator: "Partei D",
    ordnungszahl: 2,
    kurzname: "D",
    erhaeltStimmen: true,
    kandidaten: [
      {
        identifikator: "401",
        name: "Adam Labert",
        listenposition: 1,
        direktkandidat: false,
        tabellenSpalteInNiederschrift: 1,
        einzelbewerber: false,
        anzahlNennungen: 2,
      },
      {
        identifikator: "402",
        name: "Bianca Bohnert",
        listenposition: 2,
        direktkandidat: false,
        tabellenSpalteInNiederschrift: 1,
        einzelbewerber: false,
        anzahlNennungen: 1,
      },
    ],
  },
  {
    identifikator: "Partei E",
    ordnungszahl: 2,
    kurzname: "E",
    erhaeltStimmen: true,
    kandidaten: [
      {
        identifikator: "501",
        name: "Rosalia Mint",
        listenposition: 1,
        direktkandidat: false,
        tabellenSpalteInNiederschrift: 1,
        einzelbewerber: false,
        anzahlNennungen: 3,
      },
      {
        identifikator: "502",
        name: "Sabine Meier",
        listenposition: 2,
        direktkandidat: false,
        tabellenSpalteInNiederschrift: 1,
        einzelbewerber: false,
        anzahlNennungen: 3,
      },
      {
        identifikator: "503",
        name: "Thomas Maier",
        listenposition: 3,
        direktkandidat: false,
        tabellenSpalteInNiederschrift: 1,
        einzelbewerber: false,
        anzahlNennungen: 3,
      },
    ],
  },
];

// Story: Anzeige des Stimmzettels mit Beispiel-Daten
export const Default = {
  args: {
    wahlvorschlaege: sampleWahlvorschlaege,
    maximalErlaubteStimmenProWahlvorschlag: 3,
    activeWahlvorschlagId: sampleWahlvorschlaege[4].identifikator,
    activeKandidatId: "503",
  },
};
