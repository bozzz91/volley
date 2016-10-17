(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('TrainingDetailController', TrainingDetailController);

    TrainingDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Principal'];

    function TrainingDetailController($scope, $rootScope, $stateParams, entity, Principal) {
        var vm = this;

        vm.training = entity;
        vm.account = null;

        Principal.identity().then(function(account) {
            vm.account = account;
        });

        var unsubscribe = $rootScope.$on('volleyApp:trainingUpdate', function(event, result) {
            vm.training = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
