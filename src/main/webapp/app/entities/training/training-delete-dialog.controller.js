(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('TrainingDeleteController',TrainingDeleteController);

    TrainingDeleteController.$inject = ['$uibModalInstance', 'entity', 'Training'];

    function TrainingDeleteController($uibModalInstance, entity, Training) {
        var vm = this;

        vm.training = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        vm.confirmCancel = confirmCancel;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Training.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }

        function confirmCancel () {
            vm.training.state = 'CANCELLED';
            Training.update(vm.training, function (result) {
                $scope.$emit('volleyApp:trainingUpdate', result);
                $uibModalInstance.close(result);
            });
        }
    }
})();
