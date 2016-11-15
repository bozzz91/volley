(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('TrainingController', TrainingController);

    TrainingController.$inject = ['$scope', '$state', 'Training', 'ParseLinks', 'AlertService', 'City'];

    function TrainingController ($scope, $state, Training, ParseLinks, AlertService, City) {
        var vm = this;

        vm.trainings = [];
        vm.loadPage = loadPage;
        vm.page = 0;
        vm.links = {
            last: 0
        };
        vm.predicate = 'startAt';
        vm.reset = reset;
        vm.setSearch = setSearch;
        vm.reverse = false;
        vm.search = 'mine';
        vm.cities = [];

        loadAll();
        loadCities();

        function loadAll () {
            Training.query({
                page: vm.page,
                search: vm.search,
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

        function loadCities() {
            vm.cities = [];
            City.query({
                    page: vm.page,
                    size: 20
                },
                function (data, headers){
                    for (var i = 0; i < data.length; i++) {
                        vm.cities.push(data[i]);
                    }
                }
            );
        }

        function reset() {
            vm.page = 0;
            vm.trainings = [];
            loadAll();
        }

        function setSearch(search) {
            if (vm.search == search) {
                return;
            }
            vm.search = search;
            reset();
        }

        function loadPage(page) {
            vm.page = page;
            loadAll();
        }
    }
})();
