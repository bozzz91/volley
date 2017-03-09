(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('FaqDetailController', FaqDetailController);

    FaqDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Faq'];

    function FaqDetailController($scope, $rootScope, $stateParams, entity, Faq) {
        var vm = this;

        vm.faq = entity;

        var unsubscribe = $rootScope.$on('volleyApp:faqUpdate', function(event, result) {
            vm.faq = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
