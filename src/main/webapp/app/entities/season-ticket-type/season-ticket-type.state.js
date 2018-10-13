(function() {
    'use strict';

    angular
        .module('volleyApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('season-ticket-type', {
            parent: 'entity',
            url: '/season-ticket-type',
            data: {
                authorities: ['ROLE_ADMIN', 'ROLE_ORGANIZER'],
                pageTitle: 'volleyApp.seasonTicketType.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/season-ticket-type/season-ticket-types.html',
                    controller: 'SeasonTicketTypeController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('seasonTicketType');
                    $translatePartialLoader.addPart('attendingType');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('season-ticket-type-detail', {
            parent: 'entity',
            url: '/season-ticket-type/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'volleyApp.seasonTicketType.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/season-ticket-type/season-ticket-type-detail.html',
                    controller: 'SeasonTicketTypeDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('seasonTicketType');
                    $translatePartialLoader.addPart('attendingType');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'SeasonTicketType', function($stateParams, SeasonTicketType) {
                    return SeasonTicketType.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('season-ticket-type.new', {
            parent: 'season-ticket-type',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN', 'ROLE_ORGANIZER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/season-ticket-type/season-ticket-type-dialog.html',
                    controller: 'SeasonTicketTypeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                price: null,
                                duration: null,
                                capacity: null,
                                index: 1,
                                attendingType: 'BOTH',
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('season-ticket-type', null, { reload: true });
                }, function() {
                    $state.go('season-ticket-type');
                });
            }]
        })
        .state('season-ticket-type.edit', {
            parent: 'season-ticket-type',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN', 'ROLE_ORGANIZER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/season-ticket-type/season-ticket-type-dialog.html',
                    controller: 'SeasonTicketTypeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['SeasonTicketType', function(SeasonTicketType) {
                            return SeasonTicketType.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('season-ticket-type', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('season-ticket-type.delete', {
            parent: 'season-ticket-type',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN', 'ROLE_ORGANIZER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/season-ticket-type/season-ticket-type-delete-dialog.html',
                    controller: 'SeasonTicketTypeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['SeasonTicketType', function(SeasonTicketType) {
                            return SeasonTicketType.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('season-ticket-type', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
