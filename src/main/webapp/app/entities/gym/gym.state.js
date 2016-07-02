(function() {
    'use strict';

    angular
        .module('volleyApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('gym', {
            parent: 'entity',
            url: '/gym',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'volleyApp.gym.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/gym/gyms.html',
                    controller: 'GymController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('gym');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('gym-detail', {
            parent: 'entity',
            url: '/gym/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'volleyApp.gym.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/gym/gym-detail.html',
                    controller: 'GymDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('gym');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Gym', function($stateParams, Gym) {
                    return Gym.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('gym.new', {
            parent: 'gym',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/gym/gym-dialog.html',
                    controller: 'GymDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                location: null,
                                description: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('gym', null, { reload: true });
                }, function() {
                    $state.go('gym');
                });
            }]
        })
        .state('gym.edit', {
            parent: 'gym',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/gym/gym-dialog.html',
                    controller: 'GymDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Gym', function(Gym) {
                            return Gym.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('gym', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('gym.delete', {
            parent: 'gym',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/gym/gym-delete-dialog.html',
                    controller: 'GymDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Gym', function(Gym) {
                            return Gym.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('gym', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
