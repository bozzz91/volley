(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('UserManagementDialogController',UserManagementDialogController);

    UserManagementDialogController.$inject = ['$uibModalInstance', 'entity', 'User', 'Role', 'JhiLanguageService', 'City', 'Organization'];

    function UserManagementDialogController ($uibModalInstance, entity, User, Role, JhiLanguageService, City, Organization) {
        var vm = this;

        vm.authorities = [];
        vm.clear = clear;
        vm.languages = null;
        vm.save = save;
        vm.user = entity;
        vm.cities = City.query();
        vm.organizations = Organization.query();
        vm.isUserOrganizer = isUserOrganizer;

        JhiLanguageService.getAll().then(function (languages) {
            vm.languages = languages;
        });

        function isUserOrganizer() {
            for (var i=0; i<vm.authorities.length; i++) {
                if (vm.authorities[i].selected && vm.authorities[i].name.indexOf('ROLE_ORGANIZER') >= 0) {
                    return true;
                }
            }
            return false;
        }

        Role.query(function (result) {
            for (var i=0; i<result.length; i++) {
                if (entity.authorities) {
                    result[i].selected = entity.authorities.indexOf(result[i].name) >= 0;
                }
                vm.authorities.push(result[i]);
            }
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function onSaveSuccess (result) {
            vm.isSaving = false;
            $uibModalInstance.close(result);
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function save () {
            vm.isSaving = true;
            convertRoles(vm.user);
            if (vm.user.id !== null) {
                User.update(vm.user, onSaveSuccess, onSaveError);
            } else {
                User.save(vm.user, onSaveSuccess, onSaveError);
            }
        }

        function convertRoles(user) {
            user.authorities = [];
            for (var i=0; i<vm.authorities.length; i++) {
                if (vm.authorities[i].selected) {
                    user.authorities.push(vm.authorities[i].name);
                }
            }
        }
    }
})();
