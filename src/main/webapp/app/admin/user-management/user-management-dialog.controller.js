(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('UserManagementDialogController',UserManagementDialogController);

    UserManagementDialogController.$inject = ['$uibModalInstance', 'entity', 'User', 'Role', 'JhiLanguageService', 'City'];

    function UserManagementDialogController ($uibModalInstance, entity, User, Role, JhiLanguageService, City) {
        var vm = this;

        vm.authorities = [];
        vm.clear = clear;
        vm.languages = null;
        vm.save = save;
        vm.user = entity;
        vm.cities = City.query();

        JhiLanguageService.getAll().then(function (languages) {
            vm.languages = languages;
        });

        Role.query(function (result) {
            for (var i=0; i<result.length; i++) {
                result[i].selected = entity.authorities.indexOf(result[i].name) >= 0;
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
