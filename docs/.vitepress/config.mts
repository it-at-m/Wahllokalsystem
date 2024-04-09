import {defineConfig} from 'vitepress'

const PATH_TECHNIK = '/technik/';

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "Wahllokalsystem",
  description: "Datenerfassung am Wahltag",
  lang: "de-DE",
  base: '/Wahllokalsystem/',
  srcDir: 'src', //markdown files are located in that directory
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      { text: 'Home', link: '/' },
      { text: 'About', link: '/about/' },
      { text: 'Features', link: '/features/' },
      { text: 'Technik', link: PATH_TECHNIK }
    ],

    sidebar: {
      [PATH_TECHNIK]: [
        { text: 'Tools & Frameworks', link: `${PATH_TECHNIK}` },
        {
          text: 'Getting Started', link: `${PATH_TECHNIK}get_started/`
        },
        { text: 'Adr', link: `${PATH_TECHNIK}adr/`, collapsed: true, items: [
            { text: 'Renovate - ignoriere lombok', link: `${PATH_TECHNIK}adr/adr001-renovate-ignore-lombok` }
        ] }
      ]
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/it-at-m/Wahllokalsystem/' }
    ]
  }
})
