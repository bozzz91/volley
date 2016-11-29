(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('SmsDeleteController',SmsDeleteController);

    SmsDeleteController.$inject = ['$uibModalInstance', 'entity', 'Sms'];

    function SmsDeleteController($uibModalInstance, entity, Sms) {
        var vm = this;

        vm.sms = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Sms.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
