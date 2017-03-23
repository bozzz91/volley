(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('HomeController', HomeController)
        .directive('closeDialog', CloseDialog)
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

    CloseDialog.$inject = ['$timeout', 'ngDialog'];

    function CloseDialog($timeout, ngDialog) {
        return {
            link: function(scope, element, attrs) {
                if(attrs.closeDialog) {
                    $timeout(function(){ngDialog.close()}, attrs.closeDialog * 1000);
                }
                element.bind('click', function(element) {
                    ngDialog.close();
                })
            }
        }
    }

    HomeController.$inject = ['$scope', 'TrainingUser', 'ngDialog', 'Principal', 'LoginService', 'ParseLinks', 'Training', 'Auth', 'AlertService', 'City', '$mdDialog', '$translate'];

    function HomeController ($scope, TrainingUser, ngDialog, Principal, LoginService, ParseLinks, Training, Auth, AlertService, City, $mdDialog, $translate) {
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
        vm.cities = [];
        vm.levels = [];
        vm.saveAccount = saveAccount;
        vm.loadCities = loadCities;
        vm.isNewYearHolidays = isNewYearHolidays;

        $scope.onLevelSelected = function (level) {
            if (level.id != $scope.currentLevel.id) {
                $scope.currentLevel = level;
            }
        };

        $scope.showConfirm = function(ev, trainingId) {
            // Appending dialog to document.body to cover sidenav in docs app
            var confirm = $mdDialog.confirm()
                .title('Отписаться')
                .textContent('Вы уверены что хотите отписаться от тренировки?')
                .ariaLabel('Lucky day')
                .targetEvent(ev)
                .ok('Да')
                .cancel('Нет');

            $mdDialog.show(confirm).then(function() {
                vm.unroll(trainingId);
            }, function() {
                //nothing
            });
        };

        vm.detectBlur = function () {
            var blurClassName = 'bc-avatar-blur';
            if (navigator.userAgent.toLowerCase().match(/android/i)) {
                blurClassName = '';
            }
            return blurClassName;
        };

        $scope.filterByLevel = function (level) {
            return function (training) {
                return training.level.id == level.id;
            }
        };

        vm.popupOpen = function(text) {
            vm.modalText = text;
            ngDialog.open({
                template: 'popupTmpl.html',
                className: 'ngdialog-theme-default',
                scope: $scope
            });
        };

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
            });
        }

        function isNewYearHolidays() {
            var now = new Date();
            var holidaysEnd = new Date("2017-01-09");
            return now < holidaysEnd;
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
                state: 'REGISTRATION,CANCELLED',
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

        function enroll(id) {
            if (vm.account.readOnly) {
                AlertService.error($translate.instant('volleyApp.trainingUser.accessdenied'));
                return;
            }
            var trainings = vm.trainings;
            for(var i = 0; i<trainings.length; i++) {
                if(trainings[i].id == id) {
                    var training = trainings[i];
                    if (training.trainingUsers === null) {
                        training.trainingUsers = [];
                    }
                    //todo remove after separate phone changer is implemented
                    //update phone
                    //Auth.updateAccount(vm.account);

                    var reg = {
                        'user': vm.account,
                        'training': training
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
