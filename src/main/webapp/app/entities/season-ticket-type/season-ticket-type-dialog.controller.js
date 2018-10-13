(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('SeasonTicketTypeDialogController', SeasonTicketTypeDialogController);

    SeasonTicketTypeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'SeasonTicketType', 'Organization', 'Principal', 'OrganizationUtil'];

    function SeasonTicketTypeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, SeasonTicketType, Organization, Principal, OrganizationUtil) {
        var vm = this;

        vm.seasonTicketType = entity;
        vm.account = null;
        vm.clear = clear;
        vm.save = save;
        vm.updateOrganizations = updateOrganizations;
        vm.organizations = [];
        vm.showAllOrganizations = true;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        Principal.identity().then(function(account) {
            vm.account = account;
            vm.showAllOrganizations = vm.account.organization == null;
            updateOrganizations();
        });

        function updateOrganizations() {
            vm.organizations = OrganizationUtil.loadNecessaryOrganizations(vm.account, vm.showAllOrganizations);
        }

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.seasonTicketType.id !== null) {
                SeasonTicketType.update(vm.seasonTicketType, onSaveSuccess, onSaveError);
            } else {
                SeasonTicketType.save(vm.seasonTicketType, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('volleyApp:seasonTicketTypeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
