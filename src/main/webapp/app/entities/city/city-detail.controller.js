(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('CityDetailController', CityDetailController);

    CityDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'City', 'Gym'];

    function CityDetailController($scope, $rootScope, $stateParams, entity, City, Gym) {
        var vm = this;

        vm.city = entity;

        var unsubscribe = $rootScope.$on('volleyApp:cityUpdate', function(event, result) {
            vm.city = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
