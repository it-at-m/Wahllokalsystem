import {withMermaid} from "vitepress-plugin-mermaid"

const PATH_TECHNIK = '/technik/';
const PATH_ECOSYSTEM = PATH_TECHNIK + '/ecosystem/';
const PATH_CODING_CONVENTIONS = PATH_TECHNIK + 'naming_conventions/';
const PATH_ADR = PATH_TECHNIK + 'adr/';
const PATH_GUIDES = PATH_TECHNIK + '/guides/';
const PATH_SERVICES = '/services/';
const PATH_SYSSPEC = PATH_TECHNIK + "systemspecification/";

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
            {text: 'About', link: '/about/'},
            {text: 'Services', link: '/services/'},
            {text: 'Technik', link: PATH_TECHNIK}
        ],

        docFooter: {
            prev: 'Vorherige Seite',
            next: 'Nächste Seite'
        },

        outline: {
            label: "Auf dieser Seite"
        },

        sidebar: {
            [PATH_TECHNIK]: [
                {
                    text: 'Getting Started', link: `${PATH_TECHNIK}get_started/`
                },
                {text: 'Ecosystem', collapsed: true, items: [
                        {text: 'Tools & Frameworks', link: `${PATH_ECOSYSTEM}toolsAndFrameworks`},
                        {text: 'Workflows', link: `${PATH_ECOSYSTEM}workflows`}
                    ]
                },
                {
                    text: 'Designentscheidungen', link: `${PATH_ADR}`, collapsed: true, items: [
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
                        },
                        {
                            text: 'Frontend-Refarch-Template',
                            link: `${PATH_ADR}adr-frontend-template`
                        }
                    ]
                },
                {
                    text: 'Naming Conventions', link: `${PATH_CODING_CONVENTIONS}`, collapsed: true, items: [
                        {text: 'Flyway', link: `${PATH_CODING_CONVENTIONS}flyway`},
                        {text: 'Tests', link: `${PATH_CODING_CONVENTIONS}testing`},
                        {text: 'Workflows', link: `${PATH_CODING_CONVENTIONS}workflows`}
                    ]
                },
                {
                    text: 'Guides', link: `${PATH_GUIDES}`, collapsed: true, items: [
                        {
                            text: 'API-Client generieren',
                            link: `${PATH_GUIDES}how-to-create-client-from-open-api-json.md`
                        },
                        {text: 'Datenbankzugriff', link: `${PATH_GUIDES}db-access.md`},
                        {text: 'Neuer Microservice', link: `${PATH_GUIDES}new-service.md`}
                    ]
                },
                {
                    text: "Systemspezifikation",
                    link: `${PATH_SYSSPEC}`,
                    collapsed: true,
                    items: [
                        {
                            text: "Sicherheit",
                            link: `${PATH_SYSSPEC}security`,
                        },
                    ],
                }
            ],
            [PATH_SERVICES]: [
                {text: 'Admin-Service', link: `${PATH_SERVICES}admin-service/`},
                {text: 'Auth-Service', link: `${PATH_SERVICES}auth-service/`},
                {text: 'Basisdaten-Service', link: `${PATH_SERVICES}basisdaten-service/`},
                {text: 'Briefwahl-Service', link: `${PATH_SERVICES}briefwahl-service/`},
                {text: 'EAI-Service', link: `${PATH_SERVICES}eai-service/`},
                {text: 'Ergebnismeldung-Service', link: `${PATH_SERVICES}ergebnismeldung-service/`},
                {text: 'Infomanagement-Service', link: `${PATH_SERVICES}infomanagement-service/`},
                {text: 'Monitoring-Service', link: `${PATH_SERVICES}monitoring-service/`},
                {text: 'Vorfälle und Vorkommnisse-Service', link: `${PATH_SERVICES}vorfaelleundvorkommnisse-service/`},
                {text: 'Wahlvorstand-Service', link: `${PATH_SERVICES}wahlvorstand-service/`},
            ],
        },

        socialLinks: [
            {icon: 'github', link: 'https://github.com/it-at-m/Wahllokalsystem/'}
        ],

        search: {
            provider: 'local',
            options: {
                locales: {
                    root: {
                        translations: {
                            button: {
                                buttonText: 'Suche',
                                buttonAriaLabel: 'Suche'
                            },
                            modal: {
                                displayDetails: 'Anzeigen',
                                resetButtonTitle: 'Ersetzen',
                                backButtonTitle: 'Schließen',
                                noResultsText: 'Keine Ergebnisse',
                                footer: {
                                    selectText: 'Auswählen',
                                    selectKeyAriaLabel: 'Eingeben',
                                    navigateText: 'Navigieren',
                                    navigateUpKeyAriaLabel: 'Oben',
                                    navigateDownKeyAriaLabel: 'Unten',
                                    closeText: 'Schließen',
                                    closeKeyAriaLabel: 'Esc'
                                }
                            }
                        }
                    }
                }
            }
        }
    },
    mermaidPlugin: {
        class: "mermaid my-class", // set additional css classes for parent container
    }
})
