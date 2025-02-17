'use strict';

let express = require('express');
let logger = require('morgan');
let cookieParser = require('cookie-parser');
let fs = require('fs');
let rootArgument = process.argv[3] || '/../../gui-monitoringtool-polymer/build/es5-bundled/';
let rootUrl = process.argv[4] || '';
let staticDocumentRoot = fs.realpathSync(__dirname + rootArgument);
let app = express();
let SERVER = require('./modules/server');
let bodyParser = require('body-parser');
let debug = require('debug')('wls:server');

process.title = process.argv[4] || 'wls-mock-server';

app.set('port', process.env.PORT || process.argv[5] || 4730);

app.use(logger('dev'));

app.use(bodyParser.json({ limit: '1000kb' }));
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());

// serve static content
app.use(rootUrl, express.static(staticDocumentRoot));

// set cors headers for all requests
app.all('*', function (req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, X-XSRF-TOKEN");
    res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    next();
});

app.get('/public/login', function (req, res) {
    debug();
    res.redirect('/public/app');
});

app.get('/public/authserver/logouturl', function (req, res) {
    debug();
    res.json({ url: '/public/logout-server/uaa/logout' });
});

app.options('/**', function (req, res) {
    debug();
    res.statusCode = 200;
    res.send();
});

app.post('/public/logout-server/uaa/logout', function (req, res) {
    debug();
    res.statusCode = 200;
    res.send();
});

app.post('/public/logout', function (req, res) {
    debug();
    res.statusCode = 200;
    res.send();
});

// per GET-Request kann der User eingestellt werden mit dem man sich einloggen will
app.get('/setuser', function (req, res) {
    debug();
    let usernamefromrequest = req.param('username');
    debug('set username = ' + usernamefromrequest);
    res.cookie('username', usernamefromrequest);
    res.redirect('/public/app');
});

// Toggle to enable/ disable return of save errors
app.get('/throwSaveErrors', function (req, res) {
    debug();
    let currentSaveErrorBooleanValue = SERVER.getThrowErrorsBooleanValue(req);
    debug("currentSaveErrorValue: " + currentSaveErrorBooleanValue);
    let newSaveErrorValue = true;
    if (currentSaveErrorBooleanValue) {
        newSaveErrorValue = false;
    }
    debug("throwSaveErrors: " + newSaveErrorValue);
    res.cookie('throwSaveErrors', newSaveErrorValue);
    res.send();
});

// Löschen des gesamten cookies
app.get('/deleteCookie', function (req, res) {
    debug();
    res.cookie('username', null);
    res.send();
});

// simulate http://localhost:4730/auth/uaa/user
app.get('/auth/uaa/user', function (req, res) {
    debug();
    let username = null;
    if (!req.cookies || !req.cookies.username) { // Wenn man den username noch nicht über /setuser gesetzt hat wird automatisch admin angenommen.
        debug('No cookies available or username is undefined. Setting cookie with username = admin.');
        res.cookie('username', 'admin');
        username = 'admin';
    } else {
        debug('Cookies found! Searching username');
        username = req.cookies.username;
        debug('Username "' + username + '" found');
    }
    let foundUser = null;
    for (let currentUserIndex in SERVER._users) {
        if (SERVER._users[currentUserIndex].username == username) {
            foundUser = SERVER._users[currentUserIndex];
        }
    }
    if (foundUser) {
        let foundUserCopy = SERVER.deepCopy(foundUser);

        res.json(foundUserCopy);

        debug('Found user', { user: foundUser });
    } else {
        debug('No user found');
        res.statusCode = 404;
        res.statusMessage = 'Not found.';
        res.send();
    }
});

// simulate http://localhost:4730/auth/uaa/user
app.get('/authserver/logouturl', function (req, res) {
    debug("Getting Logout-URL");
    res.statusCode = 200;
    res.json({
        url: "/wilma/logout"
    })
    res.send();
});

// simulate http://localhost:4730/auth/uaa/user
app.get('/wilma/logout', function (req, res) {
    debug("Posting to Logout-URL");
    res.statusCode = 200;
    res.send();
});

// simulate http://localhost:4730/auth/uaa/user
app.post('/logout', function (req, res) {
    debug("Posting to Logout");
    res.statusCode = 200;
    res.send();
});

/**
 * The fallback route.
 *
 * The callback will be executed when no other static route configured above matches.
 *
 * See README.md for further details
 */

app.all(
    [
        '/:service/:type(rest|businessActions)/:entity',
        '/:service/:type(rest|businessActions)/:entity/*'
    ],
    require('./modules/mockDispatcher')(SERVER)
);

// start listening
const https = require('https');
const keys = {
    key: fs.readFileSync('./../../../cert/localhost.key'),
    cert: fs.readFileSync('./../../../cert/localhost.crt')
  };
const server = https.createServer(keys, app);

const port = 4730;
server.listen(port, () => {
    console.log(`Server is listening on https://localhost:${port}`);
  });
