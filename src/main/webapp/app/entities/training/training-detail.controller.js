(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('TrainingDetailController', TrainingDetailController);

    TrainingDetailController.$inject = ['$scope', 'ngDialog', '$rootScope', '$stateParams', 'entity', 'Principal'];

    function TrainingDetailController($scope, ngDialog, $rootScope, $stateParams, entity, Principal) {
        var vm = this;

        vm.training = entity;
        vm.account = null;

        Principal.identity().then(function(account) {
            vm.account = account;
        });

        $scope.open = function(text) {
            vm.modalText = text;
            ngDialog.open({
                template: 'popupTmpl.html',
                className: 'ngdialog-theme-default',
                scope: $scope
            });
        };

        var unsubscribe = $rootScope.$on('volleyApp:trainingUpdate', function(event, result) {
            vm.training = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
