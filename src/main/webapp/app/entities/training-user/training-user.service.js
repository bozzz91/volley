(function() {
    'use strict';
    angular
        .module('volleyApp')
        .factory('TrainingUser', TrainingUser);

    TrainingUser.$inject = ['$resource', 'DateUtils'];

    function TrainingUser ($resource, DateUtils) {
        var resourceUrl =  'api/training-users/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.registerDate = DateUtils.convertDateTimeFromServer(data.registerDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' },
            'delete': { method:'DELETE'}
        });
    }
})();
