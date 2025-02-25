'use strict';

function JsonFileStorage() {
    let fs = require('fs'),
        basePath = fs.realpathSync(__dirname + '/../data'),
        fsOptions = {encoding: 'UTF-8'},
        debug = require('debug')('wls:storage'),
        absPath = function (key) {
            return basePath + '/' + process.title + '-' + key + '.json';
        };

    this.read = function (key) {
        try {
            let data = fs.readFileSync(absPath(key), fsOptions);

            debug('read ' + key, data.length || null);

            if (data) {
                return JSON.parse(data);
            }
        } catch (error) {
            debug('error: ' + error.message);
            return false;
        }
    };

    this.write = function (key, data) {
        let status = fs.writeFileSync(absPath(key), JSON.stringify(data, null, 4), fsOptions);

        debug('write ' + key, status);

        return status;
    };

    this.clear = function (key) {
        this.write(key, {});
    };
}

module.exports = new JsonFileStorage();