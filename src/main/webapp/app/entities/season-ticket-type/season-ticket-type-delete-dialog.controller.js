(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('SeasonTicketTypeDeleteController',SeasonTicketTypeDeleteController);

    SeasonTicketTypeDeleteController.$inject = ['$uibModalInstance', 'entity', 'SeasonTicketType'];

    function SeasonTicketTypeDeleteController($uibModalInstance, entity, SeasonTicketType) {
        var vm = this;

        vm.seasonTicketType = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            SeasonTicketType.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
