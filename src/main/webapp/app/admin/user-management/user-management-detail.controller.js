(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('UserManagementDetailController', UserManagementDetailController);

    UserManagementDetailController.$inject = ['$stateParams', 'User', 'Principal'];

    function UserManagementDetailController ($stateParams, User, Principal) {
        var vm = this;

        vm.load = load;
        vm.user = {};
        vm.userRole = '';

        vm.load($stateParams.login);

        function load (login) {
            User.get({login: login}, function(result) {
                vm.user = result;
                vm.userRole = detectUserRole(vm.user);
            });
        }

        function detectUserRole(user) {
            var profiles = [];
            if (Principal.hasUserRole(user, 'ROLE_SUPERADMIN')) {
                profiles.push('Суперадмин');
            }
            if (Principal.hasUserRole(user, 'ROLE_ADMIN')) {
                profiles.push('Админ');
            }
            if (Principal.hasUserRole(user, 'ROLE_ORGANIZER')) {
                profiles.push('Организатор');
            }
            if (profiles.length === 0 && Principal.hasUserRole(user, 'ROLE_USER')) {
                profiles.push('Юзер');
            }
            return profiles.toString();
        }
    }
})();
