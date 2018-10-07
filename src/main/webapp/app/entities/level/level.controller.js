(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('LevelController', LevelController);

    LevelController.$inject = ['$scope', '$state', 'Level', 'Principal'];

    function LevelController ($scope, $state, Level, Principal) {
        var vm = this;
        
        vm.levels = [];
        vm.account = null;
        vm.showAll = false;
        vm.predicate = 'order';
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
                vm.levels = [];
                return;
            }
            Level.query({
                organizationId: vm.showAll ? null : vm.account.organization.id,
                sort: sort()
            }, function(result) {
                vm.levels = result;
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
