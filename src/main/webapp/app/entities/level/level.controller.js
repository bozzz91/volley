(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('LevelController', LevelController);

    LevelController.$inject = ['$scope', '$state', 'Level'];

    function LevelController ($scope, $state, Level) {
        var vm = this;
        
        vm.levels = [];

        loadAll();

        function loadAll() {
            Level.query(function(result) {
                vm.levels = result;
            });
        }
    }
})();
