(function() {
    'use strict';

    angular
        .module('volleyApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('training', {
            parent: 'entity',
            url: '/training',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'volleyApp.training.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/training/trainings.html',
                    controller: 'TrainingController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('training');
                    $translatePartialLoader.addPart('trainingState');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('training-detail', {
            parent: 'entity',
            url: '/training/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'volleyApp.training.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/training/training-detail.html',
                    controller: 'TrainingDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('training');
                    $translatePartialLoader.addPart('trainingState');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Training', function($stateParams, Training) {
                    return Training.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('training.new', {
            parent: 'training',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/training/training-dialog.html',
                    controller: 'TrainingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                startAt: null,
                                endAt: null,
                                price: null,
                                state: null,
                                description: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('training', null, { reload: true });
                }, function() {
                    $state.go('training');
                });
            }]
        })
        .state('training.edit', {
            parent: 'training',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/training/training-dialog.html',
                    controller: 'TrainingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Training', function(Training) {
                            return Training.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('training', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('training.delete', {
            parent: 'training',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/training/training-delete-dialog.html',
                    controller: 'TrainingDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Training', function(Training) {
                            return Training.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('training', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
