(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('LevelDialogController', LevelDialogController);

    LevelDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Level', 'Organization', 'Principal', 'OrganizationUtil'];

    function LevelDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Level, Organization, Principal, OrganizationUtil) {
        var vm = this;

        vm.level = entity;
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
            if (vm.level.id !== null) {
                Level.update(vm.level, onSaveSuccess, onSaveError);
            } else {
                Level.save(vm.level, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('volleyApp:levelUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }
    }
})();
