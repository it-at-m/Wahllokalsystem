import type { Beschlussgrund } from "@/types/dse/Beschlussgrund.ts";
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";
import type { Meta, StoryFn } from "@storybook/vue3";

import TheStimmzettelUebersichtTable from "@/components/dse/TheStimmzettelUebersichtTable.vue";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/StimmzettelGueltigkeitEnum.ts";

function createDummyStimmzettelListe(): Stimmzettel[] {
  return [
    {
      stimmzettelkennung: 1,
      wahlvorschlaege: [],
      invalideVotes: 0,
      gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
      beschlussvorschlag: [],
      beschlussfassung: null,
    },
    {
      stimmzettelkennung: 2,
      wahlvorschlaege: [],
      invalideVotes: 1,
      gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
      beschlussvorschlag: [],
      beschlussfassung: null,
    },
    {
      stimmzettelkennung: 3,
      wahlvorschlaege: [],
      invalideVotes: 0,
      gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
      beschlussvorschlag: [
        {
          text: "Stimmzettel zur Beschlussfassung vorgemerkt",
        } as Beschlussgrund,
      ],
      beschlussfassung: null,
    },
    {
      stimmzettelkennung: 4,
      wahlvorschlaege: [],
      invalideVotes: 0,
      gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
      beschlussvorschlag: [
        {
          text: "Wählerwille nicht zweifelfrei erkennbar",
        } as Beschlussgrund,
        {
          text: "Kennzeichnung nicht eindeutig zuzuordnen",
        } as Beschlussgrund,
      ],
      beschlussfassung: null,
    },
    {
      stimmzettelkennung: 5,
      wahlvorschlaege: [],
      invalideVotes: 0,
      gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
      beschlussvorschlag: [],
      beschlussfassung: null,
    },
    {
      stimmzettelkennung: 6,
      wahlvorschlaege: [],
      invalideVotes: 0,
      gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
      beschlussvorschlag: [
        {
          text: "Wählerwille nicht zweifelfrei erkennbar",
        } as Beschlussgrund,
        {
          text: "Kennzeichnung nicht eindeutig zuzuordnen",
        } as Beschlussgrund,
      ],
      beschlussfassung: null,
    },
    {
      stimmzettelkennung: 7,
      wahlvorschlaege: [],
      invalideVotes: 0,
      gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
      beschlussvorschlag: [],
      beschlussfassung: null,
    },
    {
      stimmzettelkennung: 8,
      wahlvorschlaege: [],
      invalideVotes: 2,
      gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
      beschlussvorschlag: [
        {
          text: "Ungültige Kennzeichnung",
        } as Beschlussgrund,
      ],
      beschlussfassung: null,
    },
    {
      stimmzettelkennung: 9,
      wahlvorschlaege: [],
      invalideVotes: 0,
      gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
      beschlussvorschlag: [],
      beschlussfassung: null,
    },
    {
      stimmzettelkennung: 10,
      wahlvorschlaege: [],
      invalideVotes: 0,
      gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
      beschlussvorschlag: [
        {
          text: "Wählerwille nicht zweifelfrei erkennbar",
        } as Beschlussgrund,
        {
          text: "Sonstige Unklarheit",
        } as Beschlussgrund,
      ],
      beschlussfassung: null,
    },
    {
      stimmzettelkennung: 11,
      wahlvorschlaege: [],
      invalideVotes: 1,
      gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
      beschlussvorschlag: [],
      beschlussfassung: null,
    },
  ];
}

const meta: Meta = {
  title: "components/dse/TheStimmzettelUebersichtTable",
  component: TheStimmzettelUebersichtTable,
};

export default meta;

const Template: StoryFn = (args) => ({
  components: { TheStimmzettelUebersichtTable },
  setup() {
    return { args };
  },
  template: '<TheStimmzettelUebersichtTable v-bind="args" />',
});

export const Default = Template.bind({});
Default.args = {
  teamId: "Team A",
  stimmzettelListe: createDummyStimmzettelListe(),
  stimmzettelLoading: false,
};

export const Loading = Template.bind({});
Loading.args = {
  teamId: "Team A",
  stimmzettelListe: [],
  stimmzettelLoading: true,
};

export const Empty = Template.bind({});
Empty.args = {
  teamId: "Team A",
  stimmzettelListe: [],
  stimmzettelLoading: false,
};
