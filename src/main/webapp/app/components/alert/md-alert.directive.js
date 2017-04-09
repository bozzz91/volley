(function() {
    'use strict';

    var mdAlert = {
        template:
                    '<div class="" ng-cloak="">' +
                        '<div ng-repeat="alert in $ctrl.alerts" ng-class="">' +
                            '<md-toast class="ng-scope _md md-top md-right">' +
                                '<div class="md-toast-content">' +
                                    '<span class="md-toast-text flex" flex="">{{alert.msg}}</span>' +
                                    '<button class="md-button md-ink-ripple" type="button" ng-click="alert.close($ctrl.alerts)">' +
                                        '<span class="ng-scope">Close</span>' +
                                    '</button>' +
                                '</div>' +
                            '</md-toast>' +
                        '</div>' +
                    '</div>',
        controller: mdAlertController
    };

    angular
        .module('volleyApp')
        .component('mdAlert', mdAlert);

    mdAlertController.$inject = ['$scope', 'AlertService'];

    function mdAlertController($scope, AlertService) {
        var vm = this;

        vm.alerts = AlertService.get();
        $scope.$on('$destroy', function () {
            vm.alerts = [];
        });
    }
})();
