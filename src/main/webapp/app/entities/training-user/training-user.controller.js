(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('TrainingUserController', TrainingUserController);

    TrainingUserController.$inject = ['$scope', '$state', 'TrainingUser'];

    function TrainingUserController ($scope, $state, TrainingUser) {
        var vm = this;

        vm.trainingUsers = [];

        loadAll();

        function loadAll() {
            TrainingUser.query(function(result) {
                vm.trainingUsers = result;
            });
        }
    }
})();
