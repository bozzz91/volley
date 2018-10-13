(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('SeasonTicketTypeDetailController', SeasonTicketTypeDetailController);

    SeasonTicketTypeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity'];

    function SeasonTicketTypeDetailController($scope, $rootScope, $stateParams, entity) {
        var vm = this;

        vm.seasonTicketType = entity;

        var unsubscribe = $rootScope.$on('volleyApp:seasonTicketTypeUpdate', function(event, result) {
            vm.seasonTicketType = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
