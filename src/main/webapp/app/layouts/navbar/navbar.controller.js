(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('NavbarController', NavbarController);

    NavbarController.$inject = ['$state', '$location', 'Auth', 'Principal', 'ProfileService', 'LoginService'];

    function NavbarController ($state, $location, Auth, Principal, ProfileService, LoginService) {
        var vm = this;

        vm.isNavbarCollapsed = true;
        vm.currentAccount = null;
        vm.isAuthenticated = Principal.isAuthenticated;

        ProfileService.getProfileInfo().then(function(response) {
            vm.inProduction = response.inProduction;
            vm.swaggerDisabled = response.swaggerDisabled;
        });

        vm.login = login;
        vm.logout = logout;
        vm.toggleNavbar = toggleNavbar;
        vm.collapseNavbar = collapseNavbar;
        vm.$state = $state;
        vm.showMenu = showMenu;

        Principal.identity().then(function (result) {
            vm.currentAccount = result;
        });

        function showMenu() {
            var isAdmin = false;
            var isAuthenticated = Principal.isAuthenticated();
            if (isAuthenticated) {
                isAdmin = vm.currentAccount.authorities.indexOf('ROLE_ADMIN') > 0;
            }
            var hasNoParam = $location.search().hideMenu != "true";
            return isAdmin || (isAuthenticated && hasNoParam);
        }

        function login() {
            collapseNavbar();
            LoginService.open();
        }

        function logout() {
            collapseNavbar();
            Auth.logout();
            $state.go('home');
        }

        function toggleNavbar() {
            vm.isNavbarCollapsed = !vm.isNavbarCollapsed;
        }

        function collapseNavbar() {
            vm.isNavbarCollapsed = true;
        }
    }
})();
