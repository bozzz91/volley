(function() {
    'use strict';

    angular
        .module('volleyApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('level', {
            parent: 'entity',
            url: '/level',
            data: {
                authorities: ['ROLE_ADMIN', 'ROLE_ORGANIZER'],
                pageTitle: 'volleyApp.level.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/level/levels.html',
                    controller: 'LevelController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('level');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('level-detail', {
            parent: 'entity',
            url: '/level/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'volleyApp.level.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/level/level-detail.html',
                    controller: 'LevelDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('level');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Level', function($stateParams, Level) {
                    return Level.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('level.new', {
            parent: 'level',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN', 'ROLE_ORGANIZER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/level/level-dialog.html',
                    controller: 'LevelDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                description: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('level', null, { reload: true });
                }, function() {
                    $state.go('level');
                });
            }]
        })
        .state('level.edit', {
            parent: 'level',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN', 'ROLE_ORGANIZER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/level/level-dialog.html',
                    controller: 'LevelDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Level', function(Level) {
                            return Level.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('level', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('level.delete', {
            parent: 'level',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN', 'ROLE_ORGANIZER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/level/level-delete-dialog.html',
                    controller: 'LevelDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Level', function(Level) {
                            return Level.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('level', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
