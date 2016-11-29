(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('TrainingUserController', TrainingUserController);

    TrainingUserController.$inject = ['$scope', '$state', 'TrainingUser', '$stateParams', 'training', 'ngDialog'];

    function TrainingUserController ($scope, $state, TrainingUser, $stateParams, training, ngDialog) {
        var vm = this;
        vm.training = training;

        vm.trainingUsers = [];
        vm.predicate = 'registerDate';
        vm.reverse = false;
        vm.reset = reset;
        vm.sendSms = sendSms;
        vm.popupOpen = function() {
            ngDialog.open({
                template: 'popupTmpl.html',
                className: 'ngdialog-theme-default',
                scope: $scope
            });
        };

        loadAll();

        function loadAll() {
            TrainingUser.query({
                trainingId:  $stateParams.trainingId,
                sort: sort()
            }, function(result) {
                vm.trainingUsers = result;
            });
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
        }

        function reset () {
            vm.trainingUsers = [];
            loadAll();
        }

        function sendSms(text) {
            console.log("SMS text: " + text);
            var phones = [];
            for (var i=0; i<vm.trainingUsers.length; i++) {
                var phone = vm.trainingUsers[i].user.phone;
                if (phone) {
                    phones.push(phone);
                }
            }
            if (phones.length > 0) {
                console.log('Sends SMS to: ' + phones);
                //todo
            }
        }
    }
})();
