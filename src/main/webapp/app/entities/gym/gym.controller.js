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
        vm.showByOrg = true;
        vm.predicate = 'name';
        vm.reverse = true;
        vm.reset = reset;

        Principal.identity().then(function(account) {
            vm.account = account;
            loadAll(vm.showByOrg);
        });

        function loadAll(byOrg) {
            Gym.query({
                organizationId: byOrg ? vm.account.organization.id : null,
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
            loadAll(vm.showByOrg);
        }
    }
})();
