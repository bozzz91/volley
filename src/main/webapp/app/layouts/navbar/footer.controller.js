(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('FooterController', FooterController);

    FooterController.$inject = ['Navbar'];

    function FooterController (Navbar) {
        var vm = this;

        vm.collapseNavbar = collapseNavbar;

        function collapseNavbar() {
            Navbar.collapse();
        }
    }
})();
