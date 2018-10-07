(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('TrainingController', TrainingController);

    TrainingController.$inject = ['Principal', 'Training', 'ParseLinks', 'AlertService'];

    function TrainingController (Principal, Training, ParseLinks, AlertService) {
        var vm = this;

        vm.trainings = [];
        vm.loadPage = loadPage;
        vm.page = 0;
        vm.links = {
            last: 0
        };
        vm.predicate = 'startAt';
        vm.reset = reset;
        vm.reverse = false;
        vm.showAll = false;
        vm.account = null;

        /* sate operations */
        vm.isCancelled = isCancelled;
        vm.isRegistration = isRegistration;
        vm.hasPermissionToEdit = hasPermissionToEdit;
        vm.setState = setState;

        Principal.identity().then(function(account) {
            vm.account = account;
            if (Principal.hasUserRole(vm.account, 'ROLE_ADMIN') && vm.account.organization == null) {
                vm.showAll = true;
            }
            loadAll();
        });

        function loadAll () {
            if (!vm.showAll && vm.account.organization == null) {
                vm.trainings = [];
                return;
            }
            Training.query({
                organizationId: vm.showAll ? null : vm.account.organization.id,
                page: vm.page,
                size: 20,
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
                    vm.trainings.push(data[i]);
                }
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function reset() {
            vm.page = 0;
            vm.trainings = [];
            loadAll();
        }

        function loadPage(page) {
            vm.page = page;
            loadAll();
        }

        function isRegistration(state) {
            return state === 'REGISTRATION';
        }

        function isCancelled(state) {
            return state === 'CANCELLED';
        }

        function hasPermissionToEdit(training) {
            return Principal.hasUserRole(vm.account, 'ROLE_ADMIN') || training.organizer.organization.id === vm.account.organization.id;
        }

        function setState(training, state) {
            var oldState = training.state;
            training.state = state;
            Training.update(training, onSaveSuccess, onSaveError);

            function onSaveSuccess(result) {
            }

            function onSaveError() {
                training.state = oldState;
            }
        }
    }
})();
