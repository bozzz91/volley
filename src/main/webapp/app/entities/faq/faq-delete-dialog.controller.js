(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('FaqDeleteController',FaqDeleteController);

    FaqDeleteController.$inject = ['$uibModalInstance', 'entity', 'Faq'];

    function FaqDeleteController($uibModalInstance, entity, Faq) {
        var vm = this;

        vm.faq = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Faq.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
