(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('TrainingUserController', TrainingUserController);

    TrainingUserController.$inject = ['$scope', '$state', 'TrainingUser', '$stateParams', 'training', 'ngDialog', 'Sms', 'Principal', '$mdDialog', '$translate'];

    function TrainingUserController ($scope, $state, TrainingUser, $stateParams, training, ngDialog, Sms, Principal, $mdDialog, $translate) {
        var vm = this;
        vm.training = training;
        vm.account = null;

        vm.trainingUsers = [];
        vm.predicate = 'registerDate';
        vm.reverse = false;
        vm.reset = reset;
        vm.sendSms = sendSms;

        //todo replace by $mdDialog, find '$mdDialog.confirm() in home.controller.js'
        vm.popupOpen = function() {
            var recipients = [];
            for (var i=0; i<vm.trainingUsers.length; i++) {
                var user = vm.trainingUsers[i].user;
                if (user.phone) {
                    recipients.push(user);
                }
            }
            if (recipients.length > 0) {
                $scope.recipients = recipients;
                ngDialog.open({
                    template: 'popupTmpl.html',
                    className: 'ngdialog-theme-default',
                    scope: $scope
                });
            } else {
                var alert = $mdDialog.alert()
                    .title($translate.instant('volleyApp.trainingUser.smsPopup.title'))
                    .textContent($translate.instant('volleyApp.trainingUser.smsPopup.content'))
                    .ok('OK');

                $mdDialog.show(alert);
            }
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
                if (vm.training.booking) {
                    var splittedBookedNames = vm.training.booking.split(',');
                    var bookingCount = splittedBookedNames.length;
                    for (var i = bookingCount - 1; i >= 0; i--) {
                        var bookingUser = {
                            'id': -1 - i,
                            'user': {
                                'id' : -1,
                                'login': '__booking',
                                'imageUrl': '/content/images/locked-icon.png',
                                'firstName': splittedBookedNames[i].trim()
                            },
                            'registerDate': '2000-01-01T00:00:00.0000'
                        };
                        vm.trainingUsers.push(bookingUser);
                    }
                }
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
            sms.recipients = $scope.recipients;
            Sms.save(sms, function onSaveSuccess (result) {
                console.log('Sms saved. Sms id: ' + result.id);
            }, function onSaveError () {
                console.log('Failed to save Sms')
            });
        }
    }
})();
