(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('TrainingUserController', TrainingUserController);

    TrainingUserController.$inject = ['$scope', '$state', 'TrainingUser', '$stateParams', 'training'];

    function TrainingUserController ($scope, $state, TrainingUser, $stateParams, training) {
        var vm = this;
        vm.training = training;

        vm.trainingUsers = [];
        vm.predicate = 'registerDate';
        vm.reverse = false;
        vm.reset = reset;

        loadAll();

        function loadAll() {
            TrainingUser.query({
                trainingId:  $stateParams.trainingId,
                sort: sort()
            }, function(result) {
                vm.trainingUsers = result;
            });
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
        }

        function reset () {
            vm.trainingUsers = [];
            loadAll();
        }
    }
})();
