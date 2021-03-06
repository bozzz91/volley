(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('SettingsController', SettingsController);

    SettingsController.$inject = ['Principal', 'Auth', '$state', 'JhiLanguageService', '$translate', 'City', 'Organization'];

    function SettingsController (Principal, Auth,  $state, JhiLanguageService, $translate, City, Organization) {
        var vm = this;

        vm.error = null;
        vm.save = save;
        vm.settingsAccount = null;
        vm.success = null;
        vm.cities = City.query();
        vm.organizations = Organization.query();
        vm.logout = logout;
        vm.isCurrentUserAdmin = isCurrentUserAdmin;

        /**
         * Store the "settings account" in a separate variable, and not in the shared "account" variable.
         */
        var copyAccount = function (account) {
            return {
                activated: account.activated,
                email: account.email,
                firstName: account.firstName,
                langKey: account.langKey,
                lastName: account.lastName,
                login: account.login,
                phone: account.phone,
                authorities: account.authorities,
                city: account.city,
                organization: account.organization
            };
        };

        Principal.identity().then(function(account) {
            vm.settingsAccount = copyAccount(account);
        });

        function isCurrentUserAdmin() {
            return Principal.hasUserRole(vm.settingsAccount, 'ROLE_ADMIN');
        }

        function save () {
            Auth.updateAccount(vm.settingsAccount).then(function() {
                vm.error = null;
                vm.success = 'OK';
                Principal.identity(true).then(function(account) {
                    vm.settingsAccount = copyAccount(account);
                });
                JhiLanguageService.getCurrent().then(function(current) {
                    if (vm.settingsAccount.langKey !== current) {
                        $translate.use(vm.settingsAccount.langKey);
                    }
                });
            }).catch(function() {
                vm.success = null;
                vm.error = 'ERROR';
            });
        }

        function logout() {
            Auth.logout();
            $state.go('home');
        }
    }
})();
