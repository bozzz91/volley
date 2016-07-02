(function() {
    'use strict';
    angular
        .module('volleyApp')
        .factory('Training', Training);

    Training.$inject = ['$resource', 'DateUtils'];

    function Training ($resource, DateUtils) {
        var resourceUrl =  'api/trainings/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.startAt = DateUtils.convertDateTimeFromServer(data.startAt);
                        data.endAt = DateUtils.convertDateTimeFromServer(data.endAt);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
