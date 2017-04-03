(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('TrainingCancelController',TrainingCancelController);

    TrainingCancelController.$inject = ['$uibModalInstance', 'entity', 'Training', '$scope'];

    function TrainingCancelController($uibModalInstance, entity, Training, $scope) {
        var vm = this;

        vm.training = entity;
        vm.clear = clear;
        vm.confirmCancel = confirmCancel;

        function clear () {
            $uibModalInstance.dismiss('cancel');
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
