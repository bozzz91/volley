(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', 'ParseLinks', 'Training', 'User', 'Auth', 'AlertService', '$state', 'SocialService', 'Account', 'City', 'Gym'];

    function HomeController ($scope, Principal, LoginService, ParseLinks, Training, User, Auth, AlertService, $state, SocialService, Account, City, Gym) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.loadTrainings = loadTrainings;
        vm.login = LoginService.open;
        vm.social = Account.service;
        vm.register = register;
        vm.enroll = enroll;
        vm.unroll = unroll;
        vm.trainings = [];
        vm.thumbs = [];
        vm.loadPage = loadPage;
        vm.page = 0;
        vm.links = {
            last: 0
        };
        vm.predicate = 'id';
        vm.reset = reset;
        vm.reverse = true;
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });
        vm.alreadyRegister = alreadyRegister;
        vm.allert = '';
        vm.city = [];
        vm.gym = [];
        vm.saveCity = saveCity;

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }

        function register () {
            $state.go('register');
        }

        function loadTrainings () {

            vm.city = [];

            City.query({
                page: vm.page,
                size: 20
            },
            function (data, headers){
                for (var i = 0; i < data.length; i++) {
                    vm.city.push(data[i]);
                }
            });

            vm.trainings = [];

            Training.query({
                page: vm.page,
                size: 20,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                for (var i = 0; i < data.length; i++) {
                    if(Date.now() < Date.parse(data[i].startAt)) {
                        Training.get({id: data[i]['id']}, function(result) {
                            vm.trainings.push(result);
                        });
                    }
                }
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }

        }

        function loadPage(page) {
            vm.page = page;
            loadTrainings();
        }

        function reset () {
            vm.page = 0;
            vm.trainings = [];
        }

        function saveCity() {
            Auth.updateAccount(vm.account);
        }

        function enroll(id) {
            var trainings = vm.trainings;
            for(var i = 0; i<trainings.length; i++) {
                if(trainings[i].id == id) {
                    if(trainings[i].users === null) {
                        vm.trainings[i].users = [];
                    }
                    Auth.updateAccount(vm.account);
                    vm.trainings[i].users.push(vm.account);
                    Training.update(vm.trainings[i], onSaveSuccess, onSaveError);
                    vm.allert = 'Вы успешно зарегистрированы!';
                }
            }
            return false;
        }

        function unroll(id) {
            var trainings = vm.trainings;
            for(var i = 0; i<trainings.length; i++) {
                if(trainings[i].id == id) {
                    var users = vm.trainings[i].users;
                    var index = findUserInTraining(users, vm.account);
                    if (index > -1) {
                        users.splice(index, 1);
                    }
                    Training.update(vm.trainings[i], onSaveSuccess, onSaveError);
                    vm.allert = 'Вы изменили свое решение, но мы ждем вас на одну из ближайших тренировок.';
                }
            }
            return false;
        }

        function findUserInTraining(trainingUsers, user) {
            if (!trainingUsers) {
                return -1;
            }
            for (var i=0; i<trainingUsers.length; i++) {
                if (trainingUsers[i].login == user.login) {
                    return i;
                }
            }
            return -1;
        }

        function alreadyRegister(id) {
            var trainings = vm.trainings;
            for(var j = 0; j<trainings.length; j++) {
                if(trainings[j].id == id) {
                    var index = findUserInTraining(trainings[j].users, vm.account);
                    if (index > -1) {
                        return true;
                    }
                }
            }
            return false;
        }

        function onSaveSuccessAccount(result) {
            $scope.$emit('volleyApp:userUpdate', result);
        }

        function onSaveSuccess (result) {
            $scope.$emit('volleyApp:trainingUpdate', result);
        }

        function onSaveError () {
            //vm.training.users.pop();
        }

        function onSaveErrorAccount () {
            //vm.training.users.pop();
        }

    }
})();
