(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('TrainingUserDeleteController',TrainingUserDeleteController);

    TrainingUserDeleteController.$inject = ['$uibModalInstance', 'entity', 'TrainingUser'];

    function TrainingUserDeleteController($uibModalInstance, entity, TrainingUser) {
        var vm = this;

        vm.trainingUser = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            TrainingUser.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
