module.exports = function () {
    "use strict";

    /** @this MockModuleContext */
    return function (req) {
        let newCounter = 0; // reset counter
        this.storage.write('voidCounter', {counter: newCounter});
        this.response = req.body;
    };
};