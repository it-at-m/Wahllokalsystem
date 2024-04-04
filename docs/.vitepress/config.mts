import {withMermaid} from "vitepress-plugin-mermaid"

const PATH_TECHNIK = '/technik/';

// https://vitepress.dev/reference/site-config
export default withMermaid({
  title: "Wahllokalsystem",
  description: "Datenerfassung am Wahltag",
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
        { text: 'Entwicklungsumgebung', link: `${PATH_TECHNIK}development/`}
      ]
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/it-at-m/Wahllokalsystem/' }
    ]
  },
  mermaidPlugin: {
    class: "mermaid my-class", // set additional css classes for parent container
  }
})
