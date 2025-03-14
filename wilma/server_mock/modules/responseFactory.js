'use strict';
module.exports = {

    /**
     * @param {String} message Error message
     * @param {String} service Service name
     * @param {String} [category='F'] Error category
     * @param {Number} [code=500] Error Code
     * @constructor
     */
    ErrorResponse: function (message, service, category, code) {
        this.message = message;
        this.service = service;
        this.category = category || 'F';
        this.code = '' + (code || 500);
    },

    /**
     * @param {String} message Error message
     * @param {String} service Service name
     * @param {String} [category='F'] Error category
     * @param {Number} [code=500] Error Code
     * @returns {module.exports.ErrorResponse}
     */
    getErrorResponse: function (message, service, category, code) {
        return new this.ErrorResponse(message, service, category, code);
    },

    /**
     * @param {Response} response
     * @param {string} service
     */
    notFound: function (response, service) {
        response.statusCode = 404;
        response.json(new this.ErrorResponse('Not Found', service, 'F', 404));

        return response;
    }
};