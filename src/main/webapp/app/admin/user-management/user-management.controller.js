(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('UserManagementController', UserManagementController);

    UserManagementController.$inject = ['Principal', 'User', 'ParseLinks', '$state', 'pagingParams', 'paginationConstants', 'JhiLanguageService', '$stateParams'];

    function UserManagementController(Principal, User, ParseLinks, $state, pagingParams, paginationConstants, JhiLanguageService, $stateParams) {
        var vm = this;

        vm.showOnline = JSON.parse($stateParams.showOnline || false);
        vm.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        vm.currentAccount = null;
        vm.languages = null;
        vm.loadAll = loadAll;
        vm.setActive = setActive;
        vm.setReadOnly = setReadOnly;
        vm.users = [];
        vm.page = 1;
        vm.totalItems = null;
        vm.clear = clear;
        vm.links = null;
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.transition = transition;
        vm.isUserInRole = isUserInRole;
        vm.titleSuffix = function () {
            return vm.showOnline ? 'Online' : 'Все';
        };

        vm.loadAll();

        JhiLanguageService.getAll().then(function (languages) {
            vm.languages = languages;
        });
        Principal.identity().then(function(account) {
            vm.currentAccount = account;
        });

        function setActive (user, isActivated) {
            user.activated = isActivated;
            User.update(user, function () {
                vm.loadAll();
                vm.clear();
            });
        }

        function setReadOnly (user, isReadonly) {
            user.readOnly = isReadonly;
            User.update(user, function () {
                vm.loadAll();
                vm.clear();
            });
        }

        function isUserInRole(user, role) {
            return user.authorities.indexOf(role) > 0;
        }

        function loadAll () {
            User.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                showOnline: vm.showOnline,
                sort: sort()
            }, onSuccess, onError);
        }
        function onSuccess (data, headers) {
            //hide anonymous user from user management: it's a required user for Spring Security
            for (var i in data) {
                if (data[i]['login'] === 'anonymoususer') {
                    data.splice(i, 1);
                }
            }
            vm.links = ParseLinks.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count');
            vm.queryCount = vm.totalItems;
            vm.page = pagingParams.page;
            vm.users = data;
            vm.showOnline = JSON.parse(pagingParams.showOnline || vm.showOnline);
        }
        function onError (error) {
            AlertService.error(error.data.message);
        }
        function clear () {
            vm.user = {
                id: null, login: null, firstName: null, lastName: null, email: null,
                activated: null, langKey: null, createdBy: null, createdDate: null,
                lastModifiedBy: null, lastModifiedDate: null, resetDate: null,
                resetKey: null, authorities: null, readOnly: null
            };
        }
        function sort () {
            var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
            if (vm.predicate !== 'id') {
                result.push('id');
            }
            return result;
        }

        function loadPage (page) {
            vm.page = page;
            vm.transition();
        }

        function transition () {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                showOnline: vm.showOnline,
                search: vm.currentSearch
            });
        }
    }
})();
