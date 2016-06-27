(function() {
    'use strict';

    angular
        .module('volleyApp')
        .factory('translationStorageProvider', translationStorageProvider);

    translationStorageProvider.$inject = ['$cookies', '$log', 'LANGUAGES'];

    function translationStorageProvider($cookies, $log, LANGUAGES) {
        return {
            get: get,
            put: put
        };

        function get(name) {
            if (LANGUAGES.indexOf($cookies.getObject(name)) === -1) {
                $log.info('Resetting invalid cookie language "' + $cookies.getObject(name) + '" to prefered language "ru"');
                $cookies.putObject(name, 'ru');
            }
            return $cookies.getObject(name);
        }

        function put(name, value) {
            $cookies.putObject(name, value);
        }
    }
})();
