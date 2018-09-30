(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('UserManagementDetailController', UserManagementDetailController);

    UserManagementDetailController.$inject = ['$stateParams', 'User'];

    function UserManagementDetailController ($stateParams, User) {
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
            if (user.authorities.indexOf('ROLE_SUPERADMIN') >= 0) {
                return 'Суперадмин';
            } else if (user.authorities.indexOf('ROLE_ADMIN') >= 0) {
                return 'Админ';
            } else {
                return 'Юзер';
            }
        }
    }
})();
