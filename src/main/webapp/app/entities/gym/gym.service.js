(function() {
    'use strict';
    angular
        .module('volleyApp')
        .factory('Gym', Gym);

    Gym.$inject = ['$resource'];

    function Gym ($resource) {
        var resourceUrl =  'api/gyms/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
