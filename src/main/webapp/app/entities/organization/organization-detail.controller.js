(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('OrganizationDetailController', OrganizationDetailController);

    OrganizationDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity'];

    function OrganizationDetailController($scope, $rootScope, $stateParams, entity) {
        var vm = this;

        vm.organization = entity;

        var unsubscribe = $rootScope.$on('volleyApp:organizationUpdate', function(event, result) {
            vm.organization = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
