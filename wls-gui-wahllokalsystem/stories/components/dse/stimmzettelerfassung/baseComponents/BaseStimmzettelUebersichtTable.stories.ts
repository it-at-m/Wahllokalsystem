import type { WahlvorstandBeschlussgrund } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussgrund.ts";
import type { Stimmzettel } from "@/types/dse/stimmzettelerfassung/Stimmzettel.ts";
import type { Meta, StoryFn } from "@storybook/vue3";

import BaseStimmzettelUebersichtTable from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelUebersichtTable.vue";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

function createDummyStimmzettelListe(): Stimmzettel[] {
  return [
    {
      wahlvorschlaege: [],
      invalideVotes: 0,
      gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
      wahlvorstandBeschlussvorschlag: [],
      systemBeschlussvorschlag: [],
      beschlussfassung: null,
    },
    {
      wahlvorschlaege: [],
      invalideVotes: 1,
      gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
      wahlvorstandBeschlussvorschlag: [],
      systemBeschlussvorschlag: [],
      beschlussfassung: null,
    },
    {
      wahlvorschlaege: [],
      invalideVotes: 0,
      gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
      wahlvorstandBeschlussvorschlag: [
        {
          text: "Stimmzettel zur Beschlussfassung vorgemerkt",
        } as WahlvorstandBeschlussgrund,
      ],
      systemBeschlussvorschlag: [],
      beschlussfassung: null,
    },
    {
      wahlvorschlaege: [],
      invalideVotes: 0,
      gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
      wahlvorstandBeschlussvorschlag: [
        {
          text: "Wählerwille nicht zweifelfrei erkennbar",
        } as WahlvorstandBeschlussgrund,
        {
          text: "Kennzeichnung nicht eindeutig zuzuordnen",
        } as WahlvorstandBeschlussgrund,
      ],
      systemBeschlussvorschlag: [],
      beschlussfassung: null,
    },
    {
      wahlvorschlaege: [],
      invalideVotes: 0,
      gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
      wahlvorstandBeschlussvorschlag: [],
      systemBeschlussvorschlag: [],
      beschlussfassung: null,
    },
    {
      wahlvorschlaege: [],
      invalideVotes: 0,
      gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
      wahlvorstandBeschlussvorschlag: [
        {
          text: "Wählerwille nicht zweifelfrei erkennbar",
        } as WahlvorstandBeschlussgrund,
        {
          text: "Kennzeichnung nicht eindeutig zuzuordnen",
        } as WahlvorstandBeschlussgrund,
      ],
      systemBeschlussvorschlag: [],
      beschlussfassung: null,
    },
    {
      wahlvorschlaege: [],
      invalideVotes: 0,
      gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
      wahlvorstandBeschlussvorschlag: [],
      systemBeschlussvorschlag: [],
      beschlussfassung: null,
    },
    {
      wahlvorschlaege: [],
      invalideVotes: 2,
      gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
      wahlvorstandBeschlussvorschlag: [
        {
          text: "Ungültige Kennzeichnung",
        } as WahlvorstandBeschlussgrund,
      ],
      systemBeschlussvorschlag: [],
      beschlussfassung: null,
    },
    {
      wahlvorschlaege: [],
      invalideVotes: 0,
      gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
      wahlvorstandBeschlussvorschlag: [],
      systemBeschlussvorschlag: [],
      beschlussfassung: null,
    },
    {
      wahlvorschlaege: [],
      invalideVotes: 0,
      gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
      wahlvorstandBeschlussvorschlag: [
        {
          text: "Wählerwille nicht zweifelfrei erkennbar",
        } as WahlvorstandBeschlussgrund,
        {
          text: "Sonstige Unklarheit",
        } as WahlvorstandBeschlussgrund,
      ],
      systemBeschlussvorschlag: [],
      beschlussfassung: null,
    },
    {
      wahlvorschlaege: [],
      invalideVotes: 1,
      gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
      wahlvorstandBeschlussvorschlag: [],
      systemBeschlussvorschlag: [],
      beschlussfassung: null,
    },
  ];
}

const meta: Meta = {
  title: "components/dse/BaseStimmzettelUebersichtTable",
  component: BaseStimmzettelUebersichtTable,
};

export default meta;

const Template: StoryFn = (args) => ({
  components: {
    BaseStimmzettelUebersichtTable: BaseStimmzettelUebersichtTable,
  },
  setup() {
    return { args };
  },
  template: '<BaseStimmzettelUebersichtTable v-bind="args" />',
});

export const Default = Template.bind({});
Default.args = {
  teamId: "A",
  stimmzettelListe: createDummyStimmzettelListe(),
  stimmzettelLoading: false,
};

export const Loading = Template.bind({});
Loading.args = {
  teamId: "A",
  stimmzettelListe: [],
  stimmzettelLoading: true,
};

export const Empty = Template.bind({});
Empty.args = {
  teamId: "A",
  stimmzettelListe: [],
  stimmzettelLoading: false,
};
