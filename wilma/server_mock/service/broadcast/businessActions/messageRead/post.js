module.exports = function () {
    "use strict";

    /** @this MockModuleContext */
    return function (req, nachrichtID) {
        let newCounter = parseInt(nachrichtID)+1;
        this.storage.write('voidCounter',  {counter: newCounter });
        this.response = req.body;
    };
};
