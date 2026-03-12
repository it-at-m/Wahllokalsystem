# Tools & Frameworks

## Tools

- Github
  - Sourcecodeverwaltung
  - Continuous Integration
  - Imageregistry
- Zenhub
  - Projektmanagement
- ~~Docker~~ (deprecated) -> Podman
- SoapUI
  - Beispielrequests

### Zenhub

Mit Zenhub organisieren wir unsere Entwicklung. Es basiert auf den GitHub-Issues. Die Pipelines von Zenhub unterstützen
unseren Entwicklungsprozess nach Scrum hervorragend. Um diesen Prozess auch in GitHub leichter sichtbar zu machen,
verwenden wir Labels. Diese werden automatisch beim Verschieben eines Issues von einer Pipeline zur nächsten aktualisiert.

Alle Labels, die darstellen, in welchem Schritt des Entwicklungsprozesses sich das Issue befindet, beginnen mit
[`step_`-Label](https://github.com/it-at-m/Wahllokalsystem/labels?q=step).

> [!CAUTION] Manuelle Synchronisierung der Labels bei der Zenhub-Automatisierung erforderlich  
> Automatisierungsregeln in Zenhub, die mit Labels arbeiten, müssen von Hand angepasst werden,
> wenn der Name eines Labels geändert wird.

Noch nicht eingeordnete Issues haben kein `step_`-Label. Im Laufe der Entwicklung durchlaufen die Issues folgende Schritte:

- [`step_po-review`](https://github.com/it-at-m/Wahllokalsystem/issues?q=state%3Aopen%20label%3Astep_po-review) ... Das Issue wartet auf die Abnahme durch den Product Owner.
- [`step_refinement`](https://github.com/it-at-m/Wahllokalsystem/issues?q=state%3Aopen%20label%3Astep_refinement) ... Die fachlichen Anforderungen sind definiert und bereit, vom Entwicklungsteam abgenommen zu werden.
- [`step_product-backlog`](https://github.com/it-at-m/Wahllokalsystem/issues?q=state%3Aopen%20label%3Astep_product-backlog) ... Das Issue ist für die Umsetzung vorgesehen.
- [`step_sprint-backlog`](https://github.com/it-at-m/Wahllokalsystem/issues?q=state%3Aopen%20label%3Astep_sprint-backlog) ... Das Issue ist zur Umsetzung im aktuellen Sprint vorgesehen.
- [`step_in-progress`](https://github.com/it-at-m/Wahllokalsystem/issues?q=state%3Aopen%20label%3Astep_in-progress) ... Das Issue wird von einer Person (siehe Assignee) aus dem Entwicklungsteam umgesetzt.
- [`step_code-review`](https://github.com/it-at-m/Wahllokalsystem/issues?q=state%3Aopen%20label%3Astep_code-review) ... Die Implementierung des Issues ist abgeschlossen und es erfolgt aktuell ein Code Review.

Ist ein Issue umgesetzt, wird es geschlossen und hat wieder kein `step_`-Label.

## Frameworks

- Spring-Boot
- Vue.js
- Vuetify
- VitePress
