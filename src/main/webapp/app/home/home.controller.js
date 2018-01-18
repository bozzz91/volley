(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('HomeController', HomeController)
        .filter('unique', function() {
            return function(collection, keyname) {
                var output = [],
                    keys = [];

                angular.forEach(collection, function(item) {
                    var key = item[keyname];
                    if(keys.indexOf(key) === -1) {
                        keys.push(key);
                        output.push(item);
                    }
                });
                return output;
            };
        });

    HomeController.$inject = ['$scope', 'TrainingUser', 'Principal', 'LoginService', 'ParseLinks', 'Training', 'Auth', 'AlertService', 'City', '$mdDialog', '$translate'];

    function HomeController ($scope, TrainingUser, Principal, LoginService, ParseLinks, Training, Auth, AlertService, City, $mdDialog, $translate) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = Principal.isAuthenticated;
        vm.loadTrainings = loadTrainings;
        vm.login = LoginService.open;
        vm.subscribe = subscribe;
        vm.unsubscribe = unsubscribe;
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
        vm.cities = [];
        vm.levels = [];
        vm.saveAccount = saveAccount;
        vm.loadCities = loadCities;
        vm.isCurrentUserAdmin = isCurrentUserAdmin;
        vm.isRefreshBtn = isRefreshBtn;

        $scope.onLevelSelected = function (level) {
            if (level.id !== $scope.currentLevel.id) {
                $scope.currentLevel = level;
            }
        };

        $scope.showConfirm = function(ev, trainingId) {
            // Appending dialog to document.body to cover sidenav in docs app
            var confirm = $mdDialog.confirm()
                .title($translate.instant('home.logged.training.unsubscribe'))
                .textContent($translate.instant('home.question.unsubscribe'))
                .ariaLabel('Lucky day')
                .targetEvent(ev)
                .ok($translate.instant('home.answer.yes'))
                .cancel($translate.instant('home.answer.no'));

            $mdDialog.show(confirm).then(function() {
                vm.unsubscribe(trainingId);
            }, function() {
                //nothing
            });
        };

        $scope.isTrainingCancelled = function (training) {
            return training.state === 'CANCELLED';
        };

        $scope.isTrainingInProcess = function (training) {
            return training.state === 'PROCESS';
        };

        $scope.isTrainingInRegistration = function (training) {
            return training.state === 'REGISTRATION';
        };

        vm.detectBlur = function () {
            var blurClassName = 'bc-avatar-blur';
            if (navigator.userAgent.toLowerCase().match(/android/i)) {
                blurClassName = '';
            }
            return blurClassName;
        };

        vm.calcUserClass = function (index, limit, user) {
            if (isRefreshBtn(user)) {
                return 'refresh';
            }
            if (index >= limit) {
                return 'red';
            }
            return '';
        };

        $scope.filterByLevel = function (level) {
            return function (training) {
                return training.level.id === level.id;
            }
        };

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
            });
        }

        function isCurrentUserAdmin() {
            return vm.account.authorities.indexOf('ROLE_ADMIN') > 0;
        }

        function isRefreshBtn(trainingUser) {
            return trainingUser.user.login === '__refresh';
        }

        function loadCities() {
            vm.cities = [];
            City.query({
                    page: vm.page,
                    size: 20
                },
                function (data, headers){
                    for (var i = 0; i < data.length; i++) {
                        vm.cities.push(data[i]);
                    }
                }
            );
        }

        function loadTrainings () {
            vm.trainings = [];
            vm.levels = [];

            Training.query({
                city: vm.account.city.id,
                state: 'REGISTRATION,CANCELLED,PROCESS',
                page: vm.page,
                size: 40,
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
                var tempLevels = [];
                for (var i = 0; i < data.length; i++) {
                    addLevel(tempLevels, data[i].level);

                    Training.get({id: data[i]['id']}, function(result) {
                        var refreshButtonAsUser = {
                            'id': -1,
                            'user': {
                                'id' : -1,
                                'login': '__refresh'
                            },
                            'registerDate': '2050-01-01T00:00:00.0000'
                        };
                        result.trainingUsers.push(refreshButtonAsUser);
                        vm.trainings.push(result);
                    });
                }
                tempLevels.sort(function(a, b) {
                    return a.order - b.order;
                });
                vm.levels = tempLevels;
                $scope.currentLevel = vm.levels[0];
            }
            function addLevel(collection, level) {
                if (collection.map(function(e) { return e.id; }).indexOf(level.id) < 0) {
                    collection.push(level);
                }
            }
            function onError(error) {
                //just show error alert, data provided by response headers
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

        function saveAccount() {
            Auth.updateAccount(vm.account);
        }

        function subscribe(id) {
            if (vm.account.readOnly) {
                AlertService.error($translate.instant('volleyApp.trainingUser.accessdenied'));
                return;
            }
            var trainings = vm.trainings;
            for(var i = 0; i<trainings.length; i++) {
                if(trainings[i].id === id) {
                    var training = trainings[i];
                    if (!training.trainingUsers) {
                        training.trainingUsers = [];
                    }

                    var reg = {
                        'user': vm.account,
                        'training': {
                            id: training.id
                        }
                    };

                    TrainingUser.save(reg, function(savedReg) {
                        savedReg.user = vm.account;
                        training.trainingUsers.push(savedReg);
                    }, function (error) {
                        //just show error alert, data provided by response headers
                    });
                }
            }
            return false;
        }

        function unsubscribe(id) {
            var trainings = vm.trainings;
            for(var i = 0; i<trainings.length; i++) {
                if(trainings[i].id === id) {
                    var trainingUsers = trainings[i].trainingUsers;
                    var index = findUserInTraining(trainingUsers, vm.account);
                    var regId = trainingUsers[index].id;
                    if (index > -1) {
                        TrainingUser.delete({id: regId}, function () {
                            trainingUsers.splice(index, 1);
                        }, function (error) {
                            //just show error alert, data provided by response headers
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
                if (trainingUsers[i].user.login === user.login) {
                    return i;
                }
            }
            return -1;
        }

        function alreadyRegister(id) {
            var trainings = vm.trainings;
            for(var j = 0; j<trainings.length; j++) {
                if(trainings[j].id === id) {
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
