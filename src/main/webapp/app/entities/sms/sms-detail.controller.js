(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('SmsDetailController', SmsDetailController);

    SmsDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Sms', 'User'];

    function SmsDetailController($scope, $rootScope, $stateParams, entity, Sms, User) {
        var vm = this;

        vm.sms = entity;

        var unsubscribe = $rootScope.$on('volleyApp:smsUpdate', function(event, result) {
            vm.sms = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
