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
        vm.search = null;
        vm.showMine = false;
        vm.account = null;
        vm.isUserInRole = isUserInRole;

        /* sate operations */
        vm.isCancelled = isCancelled;
        vm.isRegistration = isRegistration;
        vm.isOrganizerCurrentUser = isOrganizerCurrentUser;
        vm.setState = setState;

        Principal.identity().then(function(account) {
            vm.account = account;
            loadAll();
        });

        function loadAll () {
            Training.query({
                city: vm.account.city.id,
                page: vm.page,
                search: vm.search,
                showMine: vm.showMine,
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

        function isOrganizerCurrentUser(training) {
            return isUserInRole(vm.account, 'ROLE_SUPERADMIN') || training.organizer.id === vm.account.id;
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

        function isUserInRole(user, role) {
            return user.authorities.indexOf(role) > 0;
        }
    }
})();
