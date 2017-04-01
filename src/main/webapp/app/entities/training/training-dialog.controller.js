(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('TrainingDialogController', TrainingDialogController);

    TrainingDialogController.$inject = ['$timeout', '$scope', '$uibModalInstance', 'entity', 'Training', 'Principal', 'Level', 'Gym'];

    function TrainingDialogController ($timeout, $scope, $uibModalInstance, entity, Training, Principal, Level, Gym) {
        var vm = this;

        vm.training = entity;
        vm.account = null;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.levels = Level.query();
        vm.gyms = [];

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        Principal.identity().then(function(account) {
            vm.account = account;
        });

        Gym.query(function(result) {
            var allGyms = result;
            for (var i=0; i<allGyms.length; i++) {
                if (allGyms[i].city && allGyms[i].city.id === vm.account.city.id) {
                    vm.gyms.push(allGyms[i]);
                }
            }
        });

        function save () {
            vm.isSaving = true;
            if (vm.training.id !== null) {
                Training.update(vm.training, onSaveSuccess, onSaveError);
            } else {
                vm.training.organizer = vm.account;
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
