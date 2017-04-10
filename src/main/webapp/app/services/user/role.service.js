(function () {
    'use strict';

    angular
        .module('volleyApp')
        .factory('Role', Role);

    Role.$inject = ['$resource'];

    function Role ($resource) {
        var service = $resource('api/roles/', {}, {
            'query': {method: 'GET', isArray: true}
        });

        return service;
    }
})();
