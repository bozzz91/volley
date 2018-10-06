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
        vm.showByOrg = true;
        vm.predicate = 'order';
        vm.reverse = true;
        vm.reset = reset;

        Principal.identity().then(function(account) {
            vm.account = account;
            loadAll(vm.showByOrg);
        });

        function loadAll(byOrg) {
            Level.query({
                organizationId: byOrg ? vm.account.organization.id : null,
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
            loadAll(vm.showByOrg);
        }
    }
})();
