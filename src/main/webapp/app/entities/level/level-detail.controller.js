(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('LevelDetailController', LevelDetailController);

    LevelDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Level'];

    function LevelDetailController($scope, $rootScope, $stateParams, entity, Level) {
        var vm = this;

        vm.level = entity;

        var unsubscribe = $rootScope.$on('volleyApp:levelUpdate', function(event, result) {
            vm.level = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
