(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('GymDeleteController',GymDeleteController);

    GymDeleteController.$inject = ['$uibModalInstance', 'entity', 'Gym'];

    function GymDeleteController($uibModalInstance, entity, Gym) {
        var vm = this;

        vm.gym = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Gym.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
