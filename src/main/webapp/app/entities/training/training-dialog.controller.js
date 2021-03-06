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
        vm.durationInit = durationInit;
        vm.updateTrainingPeriod = updateTrainingPeriod;
        vm.save = save;
        vm.levels = [];
        vm.gyms = [];
        vm.duration = null;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        Principal.identity().then(function(account) {
            vm.account = account;
            loadGyms();
            loadLevels()
        });

        function loadGyms() {
            Gym.query({
                organizationId: vm.account.organization.id,
                sort: ['name']
            }, function(result) {
                vm.gyms = result;
            });
        }

        function loadLevels() {
            Level.query({
                organizationId: vm.account.organization.id,
                sort: ['order', 'name']
            }, function(result) {
                vm.levels = result;
            });
        }

        function durationInit() {
            vm.duration = new Date();
            vm.duration.setHours(2, 0, 0 ,0);
        }

        function updateTrainingPeriod() {
            var newAndAt = new Date(vm.training.startAt);
            newAndAt.setHours(
                newAndAt.getHours() + vm.duration.getHours(),
                newAndAt.getMinutes() + vm.duration.getMinutes()
            );
            vm.training.endAt = newAndAt;
        }

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
        vm.datePickerOpenStatus.duration = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
