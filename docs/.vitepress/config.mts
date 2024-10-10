import {withMermaid} from "vitepress-plugin-mermaid"

const PATH_TECHNIK = '/technik/';
const PATH_CODING_CONVENTIONS = PATH_TECHNIK + 'coding_conventions/';
const PATH_ADR = PATH_TECHNIK + 'adr/';
const PATH_FEATURES = '/features/';
const PATH_GUIDES = '/guides/';

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
            {text: 'Guides', link: PATH_GUIDES},
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
                {text: 'Coding Conventions', link: `${PATH_CODING_CONVENTIONS}`, collapsed: true, items: [
                        {text: 'Naming Convention - Testing', link: `${PATH_CODING_CONVENTIONS}tests_naming`}
                    ]},
                {
                    text: 'Adr', link: `${PATH_ADR}`, collapsed: true, items: [
                        {text: 'Renovate - ignoriere lombok', link: `${PATH_ADR}adr001-renovate-ignore-lombok`},
                        {
                            text: 'Verbesserung Einstiegsfreundlichkeit',
                            link: `${PATH_ADR}adr-improve-getting-started-of-services`
                        },
                        {
                            text: 'Shared/Separated Datenmodell',
                            link: `${PATH_ADR}adr002-controller-service-datamodels`
                        },
                        {
                            text: 'Vollständige Migration nach Keycloak',
                            link: `${PATH_ADR}adr-always-full-keycloak-migration`
                        },
                        {
                            text: 'Auslagern von Authority Strings',
                            link: `${PATH_ADR}adr-auslagerung-authority-strings`
                        }
                    ]
                }
            ],
            [PATH_FEATURES]: [
                {text: 'Briefwahl-Service', link: `${PATH_FEATURES}briefwahl-service/`},
                {text: 'Infomanagement-Service', link: `${PATH_FEATURES}infomanagement-service/`},
                {text: 'EAI-Service', link: `${PATH_FEATURES}eai-service/`},
                {text: 'Basisdaten-Service', link: `${PATH_FEATURES}basisdaten-service/`},
                {text: 'Monitoring-Service', link: `${PATH_FEATURES}monitoring-service/`},
                {text: 'Wahlvorstand-Service', link: `${PATH_FEATURES}wahlvorstand-service/`},
                {text: 'Ergebnismeldung-Service', link: `${PATH_FEATURES}ergebnismeldung-service/`},
                {text: 'Auth-Service', link: `${PATH_FEATURES}auth-service/`},
                {text: 'Vorfälle und Vorkommnisse-Service', link: `${PATH_FEATURES}vorfaelleundvorkommnisse-service/`},
            ],
            [PATH_GUIDES]: [
                {text: 'API-Client generieren', link: `${PATH_GUIDES}how-to-create-client-from-open-api-json.md`},
                {text: 'Tips und Tricks', link: `${PATH_GUIDES}tips-and-tricks.md`},
            ]
        },

        socialLinks: [
            {icon: 'github', link: 'https://github.com/it-at-m/Wahllokalsystem/'}
        ],

        search: {
            provider: 'local'
        }
    },
    mermaidPlugin: {
        class: "mermaid my-class", // set additional css classes for parent container
    }
})
