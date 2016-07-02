(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('TrainingDialogController', TrainingDialogController);

    TrainingDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Training', 'User', 'Level', 'Gym'];

    function TrainingDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Training, User, Level, Gym) {
        var vm = this;

        vm.training = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.users = User.query();
        vm.levels = Level.query();
        vm.gyms = Gym.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.training.id !== null) {
                Training.update(vm.training, onSaveSuccess, onSaveError);
            } else {
                Training.save(vm.training, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('volleyApp:trainingUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.startAt = false;
        vm.datePickerOpenStatus.endAt = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
