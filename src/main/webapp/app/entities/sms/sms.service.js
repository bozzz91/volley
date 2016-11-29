(function() {
    'use strict';
    angular
        .module('volleyApp')
        .factory('Sms', Sms);

    Sms.$inject = ['$resource', 'DateUtils'];

    function Sms ($resource, DateUtils) {
        var resourceUrl =  'api/sms/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.sendDate = DateUtils.convertDateTimeFromServer(data.sendDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
