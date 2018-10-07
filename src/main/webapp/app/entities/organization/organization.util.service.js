(function() {
    'use strict';
    angular
        .module('volleyApp')
        .factory('OrganizationUtil', OrganizationUtil);

    OrganizationUtil.$inject = ['Principal', 'Organization'];

    function OrganizationUtil (Principal, Organization) {
        return {
            loadNecessaryOrganizations: loadNecessaryOrganizations
        };

        function loadNecessaryOrganizations(user, showAll) {
            var organizations = [];
            if (Principal.hasUserRole(user, 'ROLE_ADMIN')) {
                if (showAll) {
                    organizations = Organization.query();
                } else {
                    organizations.push(user.organization);
                }
            } else if (Principal.hasUserRole(user, 'ROLE_ORGANIZER')) {
                organizations.push(user.organization);
            }
            return organizations;
        }
    }
})();
