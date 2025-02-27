import {withMermaid} from "vitepress-plugin-mermaid"

const PATH_SERVICES = '/services/';
const PATH_SERVICES_BACKEND = PATH_SERVICES + 'backend-services/';
const PATH_SERVICES_FRONTEND = PATH_SERVICES + 'frontend-services/';
const PATH_TECHNIK = '/technik/';
const PATH_ECOSYSTEM = PATH_TECHNIK + 'ecosystem/';
const PATH_ADR = PATH_TECHNIK + 'adr/';
const PATH_NAMING_CONVENTIONS = PATH_TECHNIK + 'naming_conventions/';
const PATH_GUIDES = PATH_TECHNIK + 'guides/';
const PATH_API_CLIENT_GENERATION = PATH_GUIDES + 'api-client-generation/'
const PATH_MICROSERVICE_GENERATION = PATH_GUIDES + 'new-microservice/'
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
            {text: 'Technik', link: `${PATH_TECHNIK}get_started`}
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
                {
                    text: 'Ecosystem', link: `${PATH_ECOSYSTEM}`, collapsed: true, items: [
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
                            text: 'Auslagern von Authoritystrings',
                            link: `${PATH_ADR}adr-auslagerung-authority-strings`
                        },
                        {
                            text: 'Frontend-Refarch-Template',
                            link: `${PATH_ADR}adr-frontend-template`
                        },
                        {
                            text: 'Pfadvariablen als Parameter',
                            link: `${PATH_ADR}adr-issue804-pathVariableAsMethodArguments`
                        },
                        {
                            text: 'Kein i18n von Beginn an',
                            link: `${PATH_ADR}adr-no-use-of-i18n-at-start`
                        }
                    ]
                },
                {
                    text: 'Naming Conventions', link: `${PATH_NAMING_CONVENTIONS}`, collapsed: true, items: [
                        {text: 'Flyway', link: `${PATH_NAMING_CONVENTIONS}flyway`},
                        {text: 'Tests', collapsed: true, link: `${PATH_NAMING_CONVENTIONS}tests`},
                        {text: 'Workflows', link: `${PATH_NAMING_CONVENTIONS}workflows`},
                        {text: 'Frontend', link: `${PATH_NAMING_CONVENTIONS}frontend`}
                    ]
                },
                {
                    text: 'Guides', link: `${PATH_GUIDES}`, collapsed: true, items: [
                        {text: 'Api-Client generieren', link: `${PATH_API_CLIENT_GENERATION}`, collapsed: true, items: [
                            {
                                text: 'API-Client im Backend',
                                link: `${PATH_API_CLIENT_GENERATION}how-to-create-client-from-open-api-json.md`
                            },
                            {
                                text: 'API-Client im Frontend',
                                link: `${PATH_API_CLIENT_GENERATION}generate-client-from-openapi-json-frontend.md`
                            },
                        ]},
                        {text: 'Datenbankzugriff', link: `${PATH_GUIDES}db-access.md`},
                        {text: 'Microservice anlegen', link: `${PATH_MICROSERVICE_GENERATION}`, collapsed: true, items: [
                            {
                                text: "Neuer Microservice Backend",
                                link: `${PATH_MICROSERVICE_GENERATION}new-service-backend.md`
                            },
                            {
                                text: "Neues Frontend-Projekt",
                                link: `${PATH_MICROSERVICE_GENERATION}new-service-frontend.md`
                            }
                        ]},
                        {text: 'Mock-Server', link: `${PATH_GUIDES}mock-server.md`},
                        {text: "ArchUnit Rules Testen", link: `${PATH_GUIDES}archunit-rule-tests.md`}
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
                        {
                            text: "Frontend",
                            link: `${PATH_SYSSPEC}frontend`
                        }
                    ],
                }
            ],
            [PATH_SERVICES]: [
                {text: 'Backend Services', link: `${PATH_SERVICES_BACKEND}`, collapsed: false, items:
                    [
                        {text: 'Admin-Service', link: `${PATH_SERVICES_BACKEND}admin-service/`},
                        {text: 'Auth-Service', link: `${PATH_SERVICES_BACKEND}auth-service/`},
                        {text: 'Basisdaten-Service', link: `${PATH_SERVICES_BACKEND}basisdaten-service/`},
                        {text: 'Briefwahl-Service', link: `${PATH_SERVICES_BACKEND}briefwahl-service/`},
                        {text: 'EAI-Service', link: `${PATH_SERVICES_BACKEND}eai-service/`},
                        {text: 'Ergebnismeldung-Service', link: `${PATH_SERVICES_BACKEND}ergebnismeldung-service/`},
                        {text: 'Infomanagement-Service', link: `${PATH_SERVICES_BACKEND}infomanagement-service/`},
                        {text: 'Monitoring-Service', link: `${PATH_SERVICES_BACKEND}monitoring-service/`},
                        {text: 'Vorfälle und Vorkommnisse-Service', link: `${PATH_SERVICES_BACKEND}vorfaelleundvorkommnisse-service/`},
                        {text: 'Wahlvorbereitung-Service', link: `${PATH_SERVICES_BACKEND}wahlvorbereitungs-service/`},
                        {text: 'Wahlvorstand-Service', link: `${PATH_SERVICES_BACKEND}wahlvorstand-service/`},
                    ]
                },
                {text: 'Frontend Projekte', link: `${PATH_SERVICES_FRONTEND}`, collapsed: false, items:
                    [
                        {text: 'Wahllokalsystem', link: `${PATH_SERVICES_FRONTEND}wahllokalsystem/`},
                    ]
                },
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
