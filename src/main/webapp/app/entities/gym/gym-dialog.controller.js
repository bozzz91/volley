(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('GymDialogController', GymDialogController);

    GymDialogController.$inject = ['$timeout', '$scope', '$uibModalInstance', 'entity', 'Gym', 'City', 'Organization', 'Principal'];

    function GymDialogController ($timeout, $scope, $uibModalInstance, entity, Gym, City, Organization, Principal) {
        var vm = this;

        vm.gym = entity;
        vm.account = null;
        vm.clear = clear;
        vm.save = save;
        vm.loadOrganizations = loadOrganizations;
        vm.cities = City.query();
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
