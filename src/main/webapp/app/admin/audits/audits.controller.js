(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('AuditsController', AuditsController);

    AuditsController.$inject = ['$filter', 'AuditsService', 'ParseLinks'];

    function AuditsController ($filter, AuditsService, ParseLinks) {
        var vm = this;

        vm.audits = null;
        vm.fromDate = null;
        vm.links = null;
        vm.loadPage = loadPage;
        vm.onChangeDate = onChangeDate;
        vm.page = 1;
        vm.previousMonth = previousMonth;
        vm.toDate = null;
        vm.today = today;
        vm.totalItems = null;
        vm.isTypeSuccess = function (type) {
            return type.toLowerCase().includes('success');
        };
        vm.isTypeFailure = function (type) {
            return type.toLowerCase().includes('fail');
        };

        vm.today();
        vm.previousMonth();
        vm.onChangeDate();

        function onChangeDate () {
            var dateFormat = 'yyyy-MM-dd';
            var fromDate = $filter('date')(vm.fromDate, dateFormat);
            var toDate = $filter('date')(vm.toDate, dateFormat);

            AuditsService.query({page: vm.page -1, size: 20, fromDate: fromDate, toDate: toDate, sort: sort()},
                function(result, headers){
                    vm.audits = result;
                    vm.links = ParseLinks.parse(headers('link'));
                    vm.totalItems = headers('X-Total-Count');
                }
            );

            function sort() {
                return ['auditEventDate,desc'];
            }
        }

        // Date picker configuration
        function today () {
            // Today + 1 day - needed if the current day must be included
            var today = new Date();
            vm.toDate = new Date(today.getFullYear(), today.getMonth(), today.getDate() + 1);
        }

        function previousMonth () {
            var fromDate = new Date();
            if (fromDate.getMonth() === 0) {
                fromDate = new Date(fromDate.getFullYear() - 1, 11, fromDate.getDate());
            } else {
                fromDate = new Date(fromDate.getFullYear(), fromDate.getMonth() - 1, fromDate.getDate());
            }

            vm.fromDate = fromDate;
        }

        function loadPage (page) {
            vm.page = page;
            vm.onChangeDate();
        }
    }
})();
