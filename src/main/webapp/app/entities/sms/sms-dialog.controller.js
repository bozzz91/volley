(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('SmsDialogController', SmsDialogController);

    SmsDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Sms', 'User'];

    function SmsDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Sms, User) {
        var vm = this;

        vm.sms = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.sms.id !== null) {
                Sms.update(vm.sms, onSaveSuccess, onSaveError);
            } else {
                Sms.save(vm.sms, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('volleyApp:smsUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.sendDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
