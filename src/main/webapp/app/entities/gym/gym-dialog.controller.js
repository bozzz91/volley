(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('GymDialogController', GymDialogController);

    GymDialogController.$inject = ['$timeout', '$scope', '$uibModalInstance', 'entity', 'Gym', 'City', 'Organization', 'Principal', 'OrganizationUtil'];

    function GymDialogController ($timeout, $scope, $uibModalInstance, entity, Gym, City, Organization, Principal, OrganizationUtil) {
        var vm = this;

        vm.gym = entity;
        vm.account = null;
        vm.clear = clear;
        vm.save = save;
        vm.updateOrganizations = updateOrganizations;
        vm.cities = City.query();
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
            if (vm.gym.id !== null) {
                Gym.update(vm.gym, onSaveSuccess, onSaveError);
            } else {
                Gym.save(vm.gym, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('volleyApp:gymUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }
    }
})();
