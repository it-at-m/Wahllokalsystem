/**
 * Um die Verarbeitungsgeschwindigkeit künstlich zu verlangsamen, kann hier
 * ein Wert in ms Eingetragen werden, der alle Requests künstlich verlangsamt
 */
let FAKE_REQUEST_SLOWDOWN_TIME = 0;

let debug = require('debug')('wls:dispatcher'),
    fs = require('fs'),
    responseFactory = require('./responseFactory'),
    SERVER = require('./server');

function getServerConfig() {
    'use strict';

    delete require.cache[require.resolve('../settings')];
    return require('../settings');
}

function MockModuleContext(req) {
    'use strict';

    let self = this;

    /**
     * HTTP Status Code
     * @type {number}
     */
    this.statusCode = 200;

    /**
     * HTTP Status Message
     * @type {string}
     */
    this.statusMessage = undefined;

    /**
     * HTTP Body
     * @type {{}}
     */
    this.response = undefined;

    /**
     * @type {Function}
     */
    this.debug = require('debug')('wls:service:' + req.params.service + '/' + req.params.entity);

    /**
     * @type {JsonFileStorage}
     */
    this.storage = require('../modules/storage');

    /**
     * @type {string}
     */
    this.service = req.params.service;

    this.notFound = function (body) {
        self.statusCode = 404;
        self.statusMessage = 'Not Found';
        self.response = body || responseFactory.getErrorResponse('Not found', req.params.service, 'F', 404);
    };

    this.forbidden = function (body) {
        self.statusCode = 403;
        self.statusMessage = 'Forbidden';
        self.response = body || responseFactory.getErrorResponse('Forbidden', req.params.service, 'F', 403);
    };

    this.fail = function (body) {
        self.statusCode = 500;
        self.statusMessage = 'Internal Server Error';
        self.response = body || responseFactory.getErrorResponse('Internal Server Error', req.params.service, 'T', 500);
    };

    this.unavailable = function (body) {
        self.statusCode = 503;
        self.statusMessage = 'Oje, Service unavailable';
        self.response = body || responseFactory.getErrorResponse('Oje, Service unavailable', req.params.service, 'T', 500);
    };

    this.teapot = function (body) {
        self.statusCode = 418;
        self.statusMessage = 'I*m a teapot';
        self.response = body || responseFactory.getErrorResponse('Teapot exception', req.params.service, 'F', 418);
    };

    this.keineNachichten = function () {
        self.statusCode = 204;
        self.statusMessage = 'Keine Nachrichten für dich heute :)';
    };


    this.faildeluxe = function (body) {
        self.statusCode = 400;
        self.statusMessage = 'Deluxe Error';
    };

    this.redirected = function (body) {
        self.statusCode = 302;
        self.statusMessage = 'Bruder muss los';
        self.header = self.header ? self.header : {};
        self.header["location"] = "woanders.jpg";
    }


    this.includeScript = require('./includeScript');
}

/**
 * @param {Request} req
 * @constructor
 */
class MockModuleDispatcher {
    'use strict';

    constructor(req){
        this.req = req;
        this.method = req.method.toLowerCase();
        this.modulePath = [
            req.params.service,
            req.params.type,
            req.params.entity,
            this.method
        ].join('/') + '.js';
    }

    getModulePath() {
        console.log(this.modulePath)
        return this.modulePath;
    };

    moduleExists() {
        try {
            fs.accessSync(fs.realpathSync(__dirname + '/../service') + '/' + this.modulePath, fs.F_OK);
            return true;
        } catch (error) {
            return false;
        }
    };

    async dispatch() {
        let settings = getServerConfig(),
            module = require('../service/' + this.getModulePath()),
            moduleResult,
            moduleParams = [this.req].concat((this.req.params['0'] || '').split('/')),
            context = new MockModuleContext(this.req);

        debug('Executing "' + this.modulePath + '"');

        moduleResult = await module.apply(context, [SERVER, settings].concat(moduleParams));

        if (typeof moduleResult === 'function') {
            await moduleResult.apply(context, moduleParams);
        } else if (moduleResult instanceof MockModuleContext) {
            context = moduleResult;
        } else if (moduleResult) {
            context.response = moduleResult;
        }

        return context;
    };
}

module.exports = function () {
    'use strict';

    /**
     * @param {Request} httpRequest
     * @param {Response} httpResponse
     */
    return async function (httpRequest, httpResponse) {
        debug(httpRequest.params);

        let dispatcher = new MockModuleDispatcher(httpRequest);

        if (dispatcher.moduleExists()) {          
            setTimeout(async function() {
                try {
                    var context = await dispatcher.dispatch();

                    console.log("es geht weiter nach warten async " + httpRequest.url)

                    context.debug('responding ' + JSON.stringify({
                        status: context.statusCode,
                        body: context.response
                    }));

                    if (context.statusMessage) {
                        httpResponse.statusMessage = context.statusMessage;
                    }

                    if (context.header) {
                        Object.keys(context.header).forEach(function (key, index) {
                            httpResponse.header(key, context.header[key])
                            if(httpRequest.url === "/basisdaten/businessActions/ungueltigews/heute/UWB"){
                                console.log("key:" + key + " context.header[key]" + context.header[key])
                            }
                            
                        });
                    }

                    if (typeof context.response === 'string') {
                        
                        if(httpRequest.url === "/basisdaten/businessActions/ungueltigews/heute/UWB"){
                            httpResponse.type('csv');
                        } else {
                            httpResponse.type('html');
                        }
                        httpResponse
                            .status(context.statusCode)                           
                            .send(context.response);
                    } else {
                        httpResponse
                            .status(context.statusCode)
                            .json(context.response);
                    }
                } catch (error) {
                    debug(error);

                    httpResponse
                        .status(500)
                        .json({
                            error: error.message,
                            trace: error.stack.split('\n').slice(1).map(
                                /**
                                 * @param {String} line
                                 * @returns {String}
                                 */
                                function (line) {
                                    return line.trim();
                                }
                            )
                        });
                }
            }, FAKE_REQUEST_SLOWDOWN_TIME);
        } else {
            // It isn't accessible
            debug('Not found: ' + httpRequest.path);
            httpResponse.sendStatus(404);
        }
    };
};