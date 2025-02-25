module.exports = {
    load: function (model) {
        "use strict";

        try {
            let fs = require('fs');
            let includeScript = require('./includeScript');
            let modelBasePath = fs.realpathSync(__dirname + '/../../public/app/core/model/') + '/';

            includeScript(modelBasePath + model + '.js');
        } catch (error) {
            console.error(error);
        }
    }
};