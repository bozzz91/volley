(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('TrainingDetailController', TrainingDetailController);

    TrainingDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Training', 'User', 'Level', 'Gym'];

    function TrainingDetailController($scope, $rootScope, $stateParams, entity, Training, User, Level, Gym) {
        var vm = this;

        vm.training = entity;

        var unsubscribe = $rootScope.$on('volleyApp:trainingUpdate', function(event, result) {
            vm.training = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
