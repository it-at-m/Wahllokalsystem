var fs = require('fs'),
    vm = require('vm');

module.exports = function (path) {
    'use strict';
    var code = '';

    if (!Array.isArray(path)) {
        path = [path];
    }

    path.forEach(function (pathElem) {
        code += fs.readFileSync(pathElem);
    });

    vm.runInThisContext(code, path);
};