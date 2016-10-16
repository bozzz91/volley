(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('TrainingDetailController', TrainingDetailController);

    TrainingDetailController.$inject = ['$scope', 'ngDialog', '$rootScope', '$stateParams', 'entity', 'Training', 'User', 'Level', 'Gym', 'Principal'];

    function TrainingDetailController($scope, ngDialog, $rootScope, $stateParams, entity, Training, User, Level, Gym, Principal) {
        var vm = this;

        vm.training = entity;
        vm.register = register;
        vm.unregister = unregister;
        vm.account = null;
        vm.alreadyRegister = alreadyRegister;

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

        function register() {
            vm.training.users.push(vm.account);
            Training.update(vm.training, onSaveSuccess, onSaveError);
            $scope.open('Hello, Reg success!');
        }

        function unregister() {
            var users = vm.training.users;
            var curUser = vm.account.login;
            var index = -1;
            for (var i=0; i<users.length; i++) {
                if (users[i].login == curUser) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                users.splice(index, 1);
                Training.update(vm.training, onSaveSuccess, onSaveError);
                $scope.open('Hello, Unreg success!');
            }
        }

        function alreadyRegister() {
            var trainingUsers = vm.training.trainingUsers;
            var curUser = vm.account.login;
            for (var i=0; i<trainingUsers.length; i++) {
                if (trainingUsers[i].user.login == curUser) {
                    return true;
                }
            }
            return false;
        }

        function onSaveSuccess (result) {
            $scope.$emit('volleyApp:trainingUpdate', result);
        }

        function onSaveError () {
            vm.training.users.pop();
        }
    }
})();
