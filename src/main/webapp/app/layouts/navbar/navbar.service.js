(function() {
    'use strict';
    angular
        .module('volleyApp')
        .factory('Navbar', Navbar);

    Navbar.$inject = [];

    function Navbar () {
        var isNavbarCollapsed = true;

        return {
            collapse: collapse,
            toggle: toggle,
            state: state
        };

        function collapse() {
            isNavbarCollapsed = true;
        }

        function toggle() {
            isNavbarCollapsed = !isNavbarCollapsed;
        }

        function state() {
            return isNavbarCollapsed;
        }
    }
})();
