(function() {
    'use strict';

    angular
        .module('volleyApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('training-user', {
            parent: 'entity',
            url: '/training-user?trainingId',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'volleyApp.trainingUser.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/training-user/training-users.html',
                    controller: 'TrainingUserController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('trainingUser');
                    $translatePartialLoader.addPart('sms');
                    $translatePartialLoader.addPart('user-management');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                training: ['$stateParams', 'Training', function($stateParams, Training) {
                    return Training.get({id : $stateParams.trainingId}).$promise;
                }]
            }
        })
        .state('training-user-detail', {
            parent: 'entity',
            url: '/training-user/{id}',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'volleyApp.trainingUser.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/training-user/training-user-detail.html',
                    controller: 'TrainingUserDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('trainingUser');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'TrainingUser', function($stateParams, TrainingUser) {
                    return TrainingUser.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('training-user.new', {
            parent: 'training-user',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/training-user/training-user-dialog.html',
                    controller: 'TrainingUserDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                registerDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('training-user', null, { reload: true });
                }, function() {
                    $state.go('training-user');
                });
            }]
        })
        .state('training-user.edit', {
            parent: 'training-user',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/training-user/training-user-dialog.html',
                    controller: 'TrainingUserDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['TrainingUser', function(TrainingUser) {
                            return TrainingUser.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('training-user', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('training-user.delete', {
            parent: 'training-user',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/training-user/training-user-delete-dialog.html',
                    controller: 'TrainingUserDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['TrainingUser', function(TrainingUser) {
                            return TrainingUser.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('training-user', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
