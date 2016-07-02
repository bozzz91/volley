(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('GymController', GymController);

    GymController.$inject = ['$scope', '$state', 'Gym'];

    function GymController ($scope, $state, Gym) {
        var vm = this;
        
        vm.gyms = [];

        loadAll();

        function loadAll() {
            Gym.query(function(result) {
                vm.gyms = result;
            });
        }
    }
})();
