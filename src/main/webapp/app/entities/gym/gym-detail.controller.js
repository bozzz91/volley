(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('GymDetailController', GymDetailController);

    GymDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Gym', 'Training', 'City'];

    function GymDetailController($scope, $rootScope, $stateParams, entity, Gym, Training, City) {
        var vm = this;

        vm.gym = entity;

        var unsubscribe = $rootScope.$on('volleyApp:gymUpdate', function(event, result) {
            vm.gym = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
