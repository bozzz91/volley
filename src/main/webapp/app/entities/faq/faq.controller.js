(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('FaqController', FaqController);

    FaqController.$inject = ['$scope', '$state', 'Faq', '$sce'];

    function FaqController ($scope, $state, Faq, $sce) {
        var vm = this;

        vm.faqs = [];
        vm.asHtml = asHtml;

        loadAll();

        function asHtml(text) {
            return $sce.trustAsHtml(text);
        }

        function loadAll() {
            Faq.query(function(result) {
                vm.faqs = result;
            });
        }
    }
})();
