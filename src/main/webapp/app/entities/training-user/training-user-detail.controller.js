(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('TrainingUserDetailController', TrainingUserDetailController);

    TrainingUserDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'TrainingUser', 'Training', 'User'];

    function TrainingUserDetailController($scope, $rootScope, $stateParams, entity, TrainingUser, Training, User) {
        var vm = this;

        vm.trainingUser = entity;

        var unsubscribe = $rootScope.$on('volleyApp:trainingUserUpdate', function(event, result) {
            vm.trainingUser = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
