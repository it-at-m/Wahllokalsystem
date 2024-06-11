import {withMermaid} from "vitepress-plugin-mermaid"

const PATH_TECHNIK = '/technik/';
const PATH_FEATURES = '/features/';

// https://vitepress.dev/reference/site-config
export default withMermaid({
    title: "Wahllokalsystem",
    description: "Datenerfassung am Wahltag",
    lang: "de-DE",
    base: '/Wahllokalsystem/',
    srcDir: 'src', //markdown files are located in that directory
    themeConfig: {
        // https://vitepress.dev/reference/default-theme-config
        nav: [
            {text: 'Home', link: '/'},
            {text: 'About', link: '/about/'},
            {text: 'Features', link: '/features/'},
            {text: 'Technik', link: PATH_TECHNIK}
        ],

        outline: {
            label: "Auf dieser Seite"
        },

        sidebar: {
            [PATH_TECHNIK]: [
                {text: 'Tools & Frameworks', link: `${PATH_TECHNIK}`},
                {
                    text: 'Getting Started', link: `${PATH_TECHNIK}get_started/`
                },
                {text: 'Entwicklungsumgebung', link: `${PATH_TECHNIK}development/`},
                {
                    text: 'Adr', link: `${PATH_TECHNIK}adr/`, collapsed: true, items: [
                        {text: 'Renovate - ignoriere lombok', link: `${PATH_TECHNIK}adr/adr001-renovate-ignore-lombok`},
                        {
                            text: 'Verbesserung Einstiegsfreundlichkeit',
                            link: `${PATH_TECHNIK}adr/adr-improve-getting-started-of-services`
                        },
                        {
                            text: 'Shared/Separated Datenmodell',
                            link: `${PATH_TECHNIK}adr/adr002-controller-service-datamodels`
                        },
                    ]
                }
            ],
            [PATH_FEATURES]: [
                {text: 'Briefwahl-Service', link: `${PATH_FEATURES}briefwahl-service/`},
                {text: 'Infomanagement-Service', link: `${PATH_FEATURES}infomanagement-service/`},
                {text: 'EAI-Service', link: `${PATH_FEATURES}eai-service/`},
                {text: 'Monitoring-Service', link: `${PATH_FEATURES}monitoring-service/`},
            ]
        },

        socialLinks: [
            {icon: 'github', link: 'https://github.com/it-at-m/Wahllokalsystem/'}
        ]
    },
    mermaidPlugin: {
        class: "mermaid my-class", // set additional css classes for parent container
    }
})
