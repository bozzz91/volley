(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('NavbarController', NavbarController);

    NavbarController.$inject = ['$scope', '$state', '$location', 'Auth', 'Principal', 'ProfileService', 'LoginService', 'Navbar'];

    function NavbarController ($scope, $state, $location, Auth, Principal, ProfileService, LoginService, Navbar) {
        var vm = this;

        vm.currentAccount = null;
        vm.isAuthenticated = Principal.isAuthenticated;

        ProfileService.getProfileInfo().then(function(response) {
            vm.inProduction = response.inProduction;
            vm.swaggerDisabled = response.swaggerDisabled;
        });

        vm.login = login;
        vm.logout = logout;
        vm.toggleNavbar = Navbar.toggle;
        vm.collapseNavbar = Navbar.collapse;
        vm.navbarState = Navbar.state;
        vm.$state = $state;
        vm.showMenu = showMenu;

        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        function getAccount() {
            Principal.identity().then(function (result) {
                vm.currentAccount = result;
            });
        }

        getAccount();

        function showMenu() {
            var isAdmin = false;
            var isOrganizer = false;
            var isAuthenticated = Principal.isAuthenticated();
            if (isAuthenticated) {
                isAdmin = Principal.hasUserRole(vm.currentAccount, 'ROLE_ADMIN');
                isOrganizer = Principal.hasUserRole(vm.currentAccount, 'ROLE_ORGANIZER');
            }
            // var hasNoParam = $location.search().hideMenu != "true";
            // return isAdmin || (isAuthenticated && hasNoParam);

            //now we want to show it for admins and organizers only
            return isAdmin || isOrganizer;
        }

        function login() {
            vm.collapseNavbar();
            LoginService.open();
        }

        function logout() {
            vm.collapseNavbar();
            Auth.logout();
            $state.go('home');
        }
    }
})();
