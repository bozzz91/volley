(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('TrainingUserDialogController', TrainingUserDialogController);

    TrainingUserDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'TrainingUser', 'Training', 'User'];

    function TrainingUserDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, TrainingUser, Training, User) {
        var vm = this;

        vm.trainingUser = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.trainings = Training.query();
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.trainingUser.id !== null) {
                TrainingUser.update(vm.trainingUser, onSaveSuccess, onSaveError);
            } else {
                TrainingUser.save(vm.trainingUser, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('volleyApp:trainingUserUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.registerDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
