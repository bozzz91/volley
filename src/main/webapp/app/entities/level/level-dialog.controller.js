(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('LevelDialogController', LevelDialogController);

    LevelDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Level', 'Organization', 'Principal'];

    function LevelDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Level, Organization, Principal) {
        var vm = this;

        vm.level = entity;
        vm.account = null;
        vm.clear = clear;
        vm.save = save;
        vm.loadOrganizations = loadOrganizations;
        vm.organizations = [];
        vm.showAllOrganizations = true;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        Principal.identity().then(function(account) {
            vm.account = account;
            if (vm.account.organization) {
                vm.showAllOrganizations = false;
            }
            loadOrganizations();
        });

        function loadOrganizations() {
            vm.organizations = [];
            if (Principal.hasUserRole(vm.account, 'ROLE_ADMIN')) {
                if (vm.showAllOrganizations) {
                    vm.organizations = Organization.query();
                } else {
                    vm.organizations.push(vm.account.organization);
                }
            } else if (vm.account.organization) {
                vm.organizations.push(vm.account.organization);
            }
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
