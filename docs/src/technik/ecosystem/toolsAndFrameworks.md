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

Mit Zenhub können organisieren wir unsere Entwicklung. Es setzt auf die Github-Issues auf. Die Pipelines von Zenhub
unterstützen hervorragend unseren Entwicklungsprozess nach Scrum. Um diesen Prozess auch in Gitlab leichter zu sehen
verwenden wir Labels. Diese werden automatisch beim Verschieben eines Issues von einer Pipeline zur nächsten aktualisiert.

Alle Labels die Darstellen, in welchem Schritt im Entwicklungsprozess das Issue sich befindet, beginnt mit `step_`.

> [!CAUTION] manuelle Synchronisierung der Labels bei der Zenhub Automatisierung erforderlich
> Automatisierungsregeln in Zenhub, welche mit Labels arbeiten, müssen von Hand angepasst werden, wenn man den Namen
> eines Labels ändert.

Noch nicht eingeordnete Issues haben kein [`step_`-Label](https://github.com/it-at-m/Wahllokalsystem/labels?q=step). Im Laufe der Entwicklung durchlaufen die Issues folgende Schritte:

- [`step_po-review`](https://github.com/it-at-m/Wahllokalsystem/issues?q=state%3Aopen%20label%3Astep_po-review) ... Das Issue wartet auf die Abnahme durch den Product Owner
- [`step_refinement`](https://github.com/it-at-m/Wahllokalsystem/issues?q=state%3Aopen%20label%3Astep_refinement) ... Die fachlichen Anforderungen sind definiert und sind bereit durch das Entwicklungsteam abgenommen zu werden
- [`step_product-backlog`](https://github.com/it-at-m/Wahllokalsystem/issues?q=state%3Aopen%20label%3Astep_product-backlog) ... Das Issue ist für die Umsetzung vorgesehen
- [`step_sprint-backlog`](https://github.com/it-at-m/Wahllokalsystem/issues?q=state%3Aopen%20label%3Astep_sprint-backlog) ... Das Issue ist zur Umsetzung des aktuellen Sprints vorgesehen.
- [`step_in-progress`](https://github.com/it-at-m/Wahllokalsystem/issues?q=state%3Aopen%20label%3Astep_in-progress) ... Das Issue wird durch eine Person (siehe Assignee) aus dem Entwicklungsteam umgesetzt
- [`step_code-review`](https://github.com/it-at-m/Wahllokalsystem/issues?q=state%3Aopen%20label%3Astep_code-review) ... Die Implementierung des Issues ist abgeschlossen und es erfolgt aktuell ein Code review.

Ist ein Issue umgesetzt ist es geschlossen und wieder ohne ein `step_`-Label.

## Frameworks

- Spring-Boot
- Vue.js
- Vuetify
- VitePress
