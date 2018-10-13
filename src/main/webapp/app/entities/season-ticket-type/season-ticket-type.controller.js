(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('SeasonTicketTypeController', SeasonTicketTypeController);

    SeasonTicketTypeController.$inject = ['$scope', '$state', 'SeasonTicketType', 'ParseLinks', 'AlertService', 'Principal'];

    function SeasonTicketTypeController ($scope, $state, SeasonTicketType, ParseLinks, AlertService, Principal) {
        var vm = this;

        vm.account = null;
        vm.showAll = false;
        vm.seasonTicketTypes = [];
        vm.loadPage = loadPage;
        vm.page = 0;
        vm.links = {
            last: 0
        };
        vm.predicate = 'index';
        vm.reset = reset;
        vm.reverse = true;

        Principal.identity().then(function(account) {
            vm.account = account;
            if (Principal.hasUserRole(vm.account, 'ROLE_ADMIN') && vm.account.organization == null) {
                vm.showAll = true;
            }
            loadAll();
        });

        function loadAll () {
            if (!vm.showAll && vm.account.organization == null) {
                vm.seasonTicketTypes = [];
                return;
            }
            SeasonTicketType.query({
                page: vm.page,
                size: 20,
                organizationId: vm.showAll ? null : vm.account.organization.id,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                for (var i = 0; i < data.length; i++) {
                    vm.seasonTicketTypes.push(data[i]);
                }
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function reset () {
            vm.page = 0;
            vm.seasonTicketTypes = [];
            loadAll();
        }

        function loadPage(page) {
            vm.page = page;
            loadAll();
        }
    }
})();
