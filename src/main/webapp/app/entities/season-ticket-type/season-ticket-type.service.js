(function() {
    'use strict';
    angular
        .module('volleyApp')
        .factory('SeasonTicketType', SeasonTicketType);

    SeasonTicketType.$inject = ['$resource'];

    function SeasonTicketType ($resource) {
        var resourceUrl =  'api/season-ticket-types/:id';

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
