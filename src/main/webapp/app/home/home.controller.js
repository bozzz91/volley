(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'TrainingUser', 'Principal', 'LoginService', 'ParseLinks', 'Training', 'User', 'Auth', 'AlertService', '$state', 'SocialService', 'Account', 'City', 'Gym'];

    function HomeController ($scope, TrainingUser, Principal, LoginService, ParseLinks, Training, User, Auth, AlertService, $state, SocialService, Account, City, Gym) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = Principal.isAuthenticated;
        vm.loadTrainings = loadTrainings;
        vm.login = LoginService.open;
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
        vm.cities = [];
        vm.saveCity = saveCity;

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
            });
        }

        function loadTrainings () {
            vm.cities = [];

            City.query({
                page: vm.page,
                size: 20
            },
            function (data, headers){
                for (var i = 0; i < data.length; i++) {
                    vm.cities.push(data[i]);
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
                    var training = trainings[i];
                    if (training.trainingUsers === null) {
                        training.trainingUsers = [];
                    }
                    //update phone
                    Auth.updateAccount(vm.account);

                    var reg = {
                        'user': vm.account,
                        'training': training
                    };

                    TrainingUser.save(reg, function(savedReg) {
                        training.trainingUsers.push(savedReg);
                        vm.allert = 'Вы успешно зарегистрированы!';
                    }, function (error) {
                        vm.allert = error.data.message;
                        AlertService.error(error.data.message);
                    });
                }
            }
            return false;
        }

        function unroll(id) {
            var trainings = vm.trainings;
            for(var i = 0; i<trainings.length; i++) {
                if(trainings[i].id == id) {
                    var trainingUsers = trainings[i].trainingUsers;
                    var index = findUserInTraining(trainingUsers, vm.account);
                    var regId = trainingUsers[index].id;
                    if (index > -1) {
                        TrainingUser.delete({id: regId}, function () {
                            trainingUsers.splice(index, 1);
                            vm.allert = 'Вы изменили свое решение, но мы ждем Вас на одну из ближайших тренировок.';
                        }, function (error) {
                            vm.allert = error.data.message;
                            AlertService.error(error.data.message);
                        });
                    }
                }
            }
            return false;
        }

        function findUserInTraining(trainingUsers, user) {
            if (!trainingUsers) {
                return -1;
            }
            for (var i=0; i<trainingUsers.length; i++) {
                if (trainingUsers[i].user.login == user.login) {
                    return i;
                }
            }
            return -1;
        }

        function alreadyRegister(id) {
            var trainings = vm.trainings;
            for(var j = 0; j<trainings.length; j++) {
                if(trainings[j].id == id) {
                    var index = findUserInTraining(trainings[j].trainingUsers, vm.account);
                    if (index > -1) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
})();
