(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('TrainingUserController', TrainingUserController);

    TrainingUserController.$inject = ['$scope', '$state', 'TrainingUser', '$stateParams', 'training', 'ngDialog', 'Sms', 'Principal'];

    function TrainingUserController ($scope, $state, TrainingUser, $stateParams, training, ngDialog, Sms, Principal) {
        var vm = this;
        vm.training = training;
        vm.account = null;

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

        Principal.identity().then(function(account) {
            vm.account = account;
        });

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
            var sms = {};
            sms.text = text;
            sms.sendDate = new Date();
            sms.sender = vm.account;
            var recipients = [];
            for (var i=0; i<vm.trainingUsers.length; i++) {
                var user = vm.trainingUsers[i].user;
                recipients.push(user);
            }
            if (recipients.length > 0) {
                sms.recipients = recipients;
                Sms.save(sms, function onSaveSuccess (result) {
                    console.log('Sms saved. Sms id: ' + result.id);
                }, function onSaveError () {
                    console.log('Failed to save Sms')
                });
            }
        }
    }
})();
