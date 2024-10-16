// Konfiguration für das Popover-Element
const popoverConfig = {
                        content: "Sie tippen momentan mit aktivierter Umschalttaste! Bitte prüfen Sie die Eingaben",
                        placement: "right",
                        trigger: "manual"
                      };
// Konfiguriert zwei Popover-Elemente für jedes der Eingabefelder
$('#username').popover(popoverConfig);
$('#password').popover(popoverConfig);

// Konstante für den CAPS-LOCK Keycode
const KEY_CODE_CAPSLOCK = 20;

// Event-Listener für beide Eingabefelder
// Wenn der Input den Fokus verliert, wird das Popover ausgeblendet
document.querySelector('#username').addEventListener('keyup', (event) => isCaps(event));
document.querySelector('#username').addEventListener('blur', (event) => $('#username').popover('hide'));

document.querySelector('#password').addEventListener('keyup', (event) => isCaps(event));
document.querySelector('#password').addEventListener('blur', (event) => $('#password').popover('hide'));

// Prüft ob Caps-Lock aktiviert ist und zeigt, wenn noch nicht geschehen, das Popover an
// bzw. blendet es aus, wenn Capslock wieder deaktiviert wird.
function isCaps(event) {
    // Das Input-Element in dem aktuell getippt wird.
    const element = $('#' + event.target.id);
    // Ist aktuell das Popover aktiv (war bisher Capslock aktiv gemeldet)?
    const isPopupActive = element.data('bs.popover').tip().hasClass('in');
    // Ist bei diesem Event die Caps-Lock Taste aktiv?
    var capsActive = event.getModifierState && event.getModifierState( 'CapsLock' );

    // Wenn Capslock selbst gedrückt wird muss geprüft werden, wie der vorherige Zustand war, denn
    // wenn capsActive == true, kann dies sowohl bedeuten, dass Caps aktiviert und deaktiviert wurde,
    // weiö der Event-Modifiere "CapsLock" zwar beim aktivieren bereits gesetzt ist, beim deaktivieren allerdings erst ab dem nächsten
    // Event nicht mehr aktiv ist! Danke Javascript!
    // Daher muss der vorherige Zustand geprüft werden: Wenn vorher isPopupActive == true,
    // wurde Capslock deaktiviert, ansonsten wurde es aktiviert.
    if((event.keyCode === KEY_CODE_CAPSLOCK)) {
        capsActive = !isPopupActive;
    }

    // Wenn das Popover bereits aktiv ist, zeige es nicht erneut an. Dies führt nämlich zu einem unschönen Flackern!
    if(capsActive && !isPopupActive) {
        element.popover('show');
    } else if (!capsActive) {
        element.popover('hide');
    }
}