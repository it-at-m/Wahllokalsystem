module.exports = {
    wahlergebnis: {
        ergebnismeldungen: {
            isValid: false,   // senden valide
	    nichtValidiert: true, // senden aber validierung nicht erfolgt(von WAS), also potenziell invalid 
        },
        print: {
            isValid: true   // drucken valide
        },
    },
    broadcast: {
        istDieMachtMitUns: true  //sollen broadcast nachrichten geschickt werden
    }
};
