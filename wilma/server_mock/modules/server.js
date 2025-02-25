'use strict';

let debug = require('debug')('wls:server:deprecated');

function Server() {
    this.storage = require('./storage');
    this.modelLoader = require('./modelLoader');
    this.responseFactory = require('./responseFactory');
}

module.exports = (function (SERVER) {
    // für jede Wahl am Tag muss einen Eintrag im wbidwahlnumer geben
    // wbid_wahlnummer: '{ "wbid_wahlnummer":[{ "wahlbezirkID":"eineGehashteIdDieImKLartextAndersAussiehtAlsWahlbezirk.wahlnummer2","wahlnummer":"2", "wahlID":"BEB-WAHL-ID-10"},{ "wahlbezirkID":"eineGehashteIdDieImKLartextAndersAussiehtAlsWahlbezirk.wahlnummer1","wahlnummer":"1","wahlID":"BEB-WAHL-ID-9"}, { "wahlbezirkID":"eineGehashteIdDieImKLartextAndersAussiehtAlsWahlbezirk.wahlnummer1","wahlnummer":"3","wahlID":"EUW-WAHL-ID"}]}'
    //define some default users
    SERVER.__initUsers = [
        {
            username: 'admin',
            forname: 'Addi',
            surname: 'Admin',
            authorities: ['ADMIN', 'Wahl_ADMIN', 'Anwesenheit_ADMIN', 'PERM_WAHLAMT_ADMIN', 'ROLE_LOGIN_Wls_Wahllokal'],
            wahlbezirkID: 'eineGehashteIdDieImKLartextAndersAussiehtAlsWahlbezirk.wahlnummer0',
	    wahlbezirksArt: 'UWB',
	    pin:  '1111-2222-3333-4444',
	    wahltag: '2022-09-24',
	    wahlbezirkNummer: '1337',
	    wahltagID: 'heute',
        wbid_wahlnummer: '{ "wbid_wahlnummer":[{ "wahlbezirkID":"eineGehashteIdDieImKLartextAndersAussiehtAlsWahlbezirk.wahlnummer1","wahlnummer":"0","wahlID":"EUW-WAHL-ID"}]}'
        },
        {
            username: 'briefwahl',
            forname: 'Brief',
            surname: 'Wahl',
            authorities: ['ADMIN', 'Wahl_ADMIN', 'Anwesenheit_ADMIN', 'ROLE_LOGIN_Wls_Wahllokal'],
            wahlbezirkID: '1eineGehashteIdDieImKLartextAndersAussiehtAlsWahlbezirk.wahlnummerbw0',
	    wahlbezirksArt: 'BWB',
	    pin:  '5555-2222-3333-4444',
	    wahltag: '2017-09-24',
	    wahlbezirkNummer: '1373',
	    wahltagID: 'heute',
        wbid_wahlnummer: '{ "wbid_wahlnummer":[{ "wahlbezirkID":"1eineGehashteIdDieImKLartextAndersAussiehtAlsWahlbezirk.wahlnummerbw1","wahlnummer":"0","wahlID":"EUW-WAHL-ID"}]}'
        },
        {
            username: 'max',
            forname: 'Maximillian',
            surname: 'Ludwig',
            authorities: ['ADMIN', 'Wahl_ADMIN', 'Anwesenheit_ADMIN'],
            wahlbezirkID: '123',
            wahlbezirksArt: 'irgendwasFalsches',
	    pin:  '6666-2222-3333-4444',
	    wahltag: '2017-09-24',
	    wahlbezirkNummer: '0000',
	    wahltagID: 'heute'
        },
        {
            username: 'read',
            forname: 'Reinhard',
            surname: 'Ready',
            authorities: ['READ_ONLY_USER', 'Wahl_READ_ONLY_USER', 'Anwesenheit_READ_ONLY_USER'],
            wahlbezirkID: '456',
	    pin:  '7777-2222-3333-4444',
	    wahltag: '2017-09-24',
	    wahlbezirkNummer: '1111',
	    wahltagID: 'heute'
        },
        {
            username: 'test',
            forname: 'Thomas',
            surname: 'Test',
            authorities: ['ADMIN', 'Wahl_ADMIN', 'Anwesenheit_ADMIN'],
            wahlbezirkID: '789',
	    pin:  '8888-2222-3333-4444',
	    wahltag: '2017-09-24',
	    wahlbezirkNummer: '2222',
	    wahltagID: 'heute'
        },
        {
            username: 'fabian.holtkoetter',
            forname: 'Fabian',
            surname: 'Holtkoetter',
            authorities: ['ADMIN', 'Wahl_ADMIN', 'Anwesenheit_ADMIN'],
            wahlbezirkID: '1011',
	    pin:  '9999-2222-3333-4444',
	    wahltag: '2017-09-24',
	    wahlbezirkNummer: '3333',
	    wahltagID: 'heute'
        },
        {
            username: 'keinlokal',
            forname: 'Kein',
            surname: 'Lokal',
            authorities: ['ADMIN', 'Wahl_ADMIN', 'Anwesenheit_ADMIN'],
            wahlbezirkID: '1213',
	    pin:  '1010-2222-3333-4444',
	    wahltag: '2017-09-24',
	    wahlbezirkNummer: '4444',
	    wahltagID: 'heute'
        },
        {
            username: 'wvaserverfault',
            forname: 'WahlvorstandsAnwesenheit',
            surname: 'Serverfehler',
            authorities: ['ADMIN', 'Wahl_ADMIN', 'Anwesenheit_ADMIN'],
            wahlbezirkID: '1415',
            wahlbezirksArt: 'UWB',
	    pin:  '0001-2222-3333-4444',
	    wahltag: '2017-09-24',
	    wahlbezirkNummer: '5555',
	    wahltagID: 'heute'
        },
        {
            username: 'keinebezirkid',
            forname: 'Keine',
            surname: 'Bezirkid',
            authorities: ['ADMIN', 'Wahl_ADMIN', 'Anwesenheit_ADMIN'],
	    pin:  '0120-2222-3333-4444',
	    wahltag: '2017-09-24',
	    wahlbezirkNummer: '6666',
	    wahltagID: 'heute'
        }
    ];

    SERVER.__initUsernameToBezirk = [
        {
            username: 'admin',
            wahlbezirkID: 'eineGehashteIdDieImKLartextAndersAussiehtAlsWahlbezirk.wahlnummer0'
        },
        {
            username: 'briefwahl',
            wahlbezirkID: '1eineGehashteIdDieImKLartextAndersAussiehtAlsWahlbezirk.wahlnummer1'
        },
        {
            username: 'read',
            wahlbezirkID: '456'
        },
        {
            username: 'max',
            wahlbezirkID: '123'
        },
        {
            username: 'test',
            wahlbezirkID: '789'
        },
        {
            username: 'fabian.holtkoetter',
            wahlbezirkID: '1011'
        },
        {
            username: 'keinlokal',
            wahlbezirkID: '1213'
        },
        {
            username: 'wvaserverfault',
            wahlbezirkID: '1415'
        }
    ];

    var urnenWahl = 0,
        briefWahl = 1;

    SERVER.__initWahlbezirke = {
        wahlbezirke: [
            {
                id: 'eineGehashteIdDieImKLartextAndersAussiehtAlsWahlbezirk.wahlnummer0',
                wahltag: '2017-09-24',
                nummer: '01104',
                wahlbezirksart: 'UWB'
            },
            {
                id: '1eineGehashteIdDieImKLartextAndersAussiehtAlsWahlbezirk.wahlnummer1',
                wahltag: '2017-09-24',
                nummer: '01105',
                wahlbezirksart: 'BWB'
            },
            {
                id: '123',
                wahltag: '2017-09-24',
                nummer: '1',
                wahlbezirksart: 'irgendwasFalsches'
            },
            {
                id: '1415',
                wahltag: '2017-09-24',
                nummer: '2',
                wahlbezirksart: 'UWB'
            },
            {
                id: '1004',
                wahltag: '2017-09-24',
                nummer: '3',
                wahlbezirksart: 'chaotisches'
            },
        ]
    };

    SERVER._initKontrolle = {
        gruende: "einAbweichungsgrund",
        unstimmigkeiten: true
    };

    SERVER._initWahlvorbereitungen = {
        wahlvorbereitungen: [
            {
                urneVersiegelt: false,
                wahlbezirkID: "102",
                wahlID: "BTW",
                wahlvorbereitungUW: {
                    anzahlWahlkabinen: 0,
                    anzahlWahltische: 0,
                    anzahlNebenraeume: 0,
                    weitereUrnen: false,
                    anzahlWeitereWahlurnen: 0
                }

            },
            {
                urneVersiegelt: false,
                wahlbezirkID: "102",
                wahlID: "BZW",
                wahlvorbereitungUW: {
                    anzahlWahlkabinen: 0,
                    anzahlWahltische: 0,
                    anzahlNebenraeume: 0,
                    weitereUrnen: false,
                    anzahlWeitereWahlurnen: 0
                }

            },
            {
                urneVersiegelt: false,
                wahlbezirkID: "103",
                wahlID: "BTW",
                wahlvorbereitungUW: {
                    anzahlWahlkabinen: 0,
                    anzahlWahltische: 0,
                    anzahlNebenraeume: 0,
                    weitereUrnen: false,
                    anzahlWeitereWahlurnen: 0
                }

            },
            {
                urneVersiegelt: true,
                wahlbezirkID: "eineGehashteIdDieImKLartextAndersAussiehtAlsWahlbezirk.wahlnummer1",
                wahlID: "BTWaGehashteWahlId",
                wahlvorbereitungUW: {
                    anzahlWahlkabinen: 8,
                    anzahlWahltische: 9,
                    anzahlNebenraeume: 10,
                    weitereUrnen: false,
                    anzahlWeitereWahlurnen: 11
                }

            },
            {
                urneVersiegelt: true,
                wahlbezirkID: "eineGehashteIdDieImKLartextAndersAussiehtAlsWahlbezirk.wahlnummer1",
                wahlID: "BEaGehashteWahlId",
                wahlvorbereitungUW: {
                    anzahlWahlkabinen: 8,
                    anzahlWahltische: 9,
                    anzahlNebenraeume: 10,
                    weitereUrnen: false,
                    anzahlWeitereWahlurnen: 11
                }

            }
        ]
    };

    SERVER._initAnwesenheiten = {};

    // some helper functions
    SERVER.deepCopy = function (obj) {
        // quick and dirty deep copy
        //debug('depp kopieren von: '+ JSON.stringify(obj));
        return JSON.parse(JSON.stringify(obj));
    };

    SERVER._initWahllokalBenutzer = {
        wahllokalbenutzer: []
    };

    SERVER._wahllokalbenutzer = SERVER.deepCopy(SERVER._initWahllokalBenutzer);

    SERVER.getWahlvorstandsAnwesenheitByBezirkId = function (bezirkId) {
        debug('SERVER.getWahlvorstandsAnwesenheitByBezirkId(' + bezirkId + ')');
        for (let i = 0, n = SERVER._wahlvorstandsAnwesenheiten.length; i < n; i++) {
            if (bezirkId == SERVER._wahlvorstandsAnwesenheiten[i].wahlvorstandsAnwesenheit.bezirkID) {
                return SERVER._wahlvorstandsAnwesenheiten[i].wahlvorstandsAnwesenheit;
            }
        }
        return null;
    };

    SERVER.loadWahlvorbereitung = function (wahlbezirkID, wahlID) {
        debug('SERVER.loadWahlvorbereitung(' + wahlbezirkID + ',' + wahlID + ')');
        debug(' laenge ' + SERVER._wahlvorbereitungen.wahlvorbereitungen.length);
        for (let i = 0, n = SERVER._wahlvorbereitungen.wahlvorbereitungen.length; i < n; i++) {
            debug(i + ' laenge ' + n);
            if (wahlbezirkID == SERVER._wahlvorbereitungen.wahlvorbereitungen[i].wahlbezirkID && wahlID == SERVER._wahlvorbereitungen.wahlvorbereitungen[i].wahlID) { // == weil es egal ist ob es "1" oder 1 ist
                debug('gefunden');
                return SERVER._wahlvorbereitungen.wahlvorbereitungen[i];
            }
        }
        return null;
    };

    SERVER.getWahlbezirk = function (bezirkID) {
        debug('SERVER.getWahlbezirk(' + bezirkID + ')');
        debug(' laenge ' + SERVER._wahlbezirke.wahlbezirke.length);
        for (let i = 0, n = SERVER._wahlbezirke.wahlbezirke.length; i < n; i++) {
            debug(i + ' laenge ' + n);
            if (bezirkID == SERVER._wahlbezirke.wahlbezirke[i].id) {
                debug('gefunden');
                debug('Response', SERVER._wahlbezirke.wahlbezirke[i]);
                return SERVER._wahlbezirke.wahlbezirke[i];
            }
        }
        return null;
    };
    SERVER.getWahlen = function () {
        debug('SERVER.getWahlen()');
        debug(' laenge ' + SERVER._wahlen.wahlen.length);
        return SERVER._wahlen;
    };

    SERVER.getThrowErrorsBooleanValue = function (req) {
        let currentSaveErrorValue = req.cookies.throwSaveErrors;
        let currentSaveErrorBooleanValue = currentSaveErrorValue === 'true';
        return currentSaveErrorBooleanValue;
    };

    SERVER._users = SERVER.deepCopy(SERVER.__initUsers);
    SERVER._usernameToBezirk = SERVER.deepCopy(SERVER.__initUsernameToBezirk);
    SERVER._anwesenheiten = SERVER.deepCopy(SERVER._initAnwesenheiten);

    SERVER._wahlvorbereitungen = SERVER.deepCopy(SERVER._initWahlvorbereitungen);
    SERVER._wahlbezirke = SERVER.deepCopy(SERVER.__initWahlbezirke);

    return SERVER;
})(new Server());
