(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('GymController', GymController);

    GymController.$inject = ['$scope', '$state', 'Gym', 'Principal'];

    function GymController ($scope, $state, Gym, Principal) {
        var vm = this;
        
        vm.gyms = [];
        vm.account = null;
        vm.showAll = false;
        vm.predicate = 'name';
        vm.reverse = true;
        vm.reset = reset;

        Principal.identity().then(function(account) {
            vm.account = account;
            if (Principal.hasUserRole(vm.account, 'ROLE_ADMIN') && vm.account.organization == null) {
                vm.showAll = true;
            }
            loadAll();
        });

        function loadAll() {
            if (!vm.showAll && vm.account.organization == null) {
                vm.gyms = [];
                return;
            }
            Gym.query({
                organizationId: vm.showAll ? null : vm.account.organization.id,
                sort: sort()
            }, function(result) {
                vm.gyms = result;
            });

            function sort() {
                return [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
            }
        }

        function reset() {
            vm.gyms = [];
            loadAll();
        }
    }
})();
