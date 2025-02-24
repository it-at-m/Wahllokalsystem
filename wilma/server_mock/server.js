'use strict';

let express = require('express');
let logger = require('morgan');
let cookieParser = require('cookie-parser');
let fs = require('fs');
let rootArgument = process.argv[3]
let rootUrl = process.argv[4] || '';
let staticDocumentRoot = fs.realpathSync(__dirname + rootArgument);
let app = express();
let SERVER = require('./modules/server');
let bodyParser = require('body-parser');
let debug = require('debug')('wls:server');

process.title = rootUrl || 'wls-mock-server';

app.set('port', process.env.PORT || process.argv[5] || 8083);

app.use(logger('dev'));

app.use(bodyParser.json({ limit: '1000kb' }));
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());

// serve static content
//app.use(rootUrl, express.static(staticDocumentRoot));

// set cors headers for all requests
app.all('*', function (req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, X-XSRF-TOKEN");
    res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    next();
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
        '/api/:service-service/:type(rest|businessActions)/:entity',
        '/api/:service-service/:type(rest|businessActions)/:entity/*'
    ],
    require('./modules/mockDispatcher')(SERVER)
);


// if HTTPS is needed, use the following
// const https = require('https');
// const keys = {
//     key: fs.readFileSync('./../../../cert/localhost.key'),
//     cert: fs.readFileSync('./../../../cert/localhost.crt')
//   };

// start listening
const http = require('http');

const server = http.createServer({}, app);

const port = 8083;
server.listen(port, () => {
    console.log(`Server is listening on https://localhost:${port}`);
  });
