(function() {
    'use strict';

    angular
        .module('volleyApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('sms', {
            parent: 'entity',
            url: '/sms?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'volleyApp.sms.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/sms/sms.html',
                    controller: 'SmsController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'sendDate,desc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('sms');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('sms-detail', {
            parent: 'entity',
            url: '/sms/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'volleyApp.sms.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/sms/sms-detail.html',
                    controller: 'SmsDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('sms');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Sms', function($stateParams, Sms) {
                    return Sms.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('sms.new', {
            parent: 'sms',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sms/sms-dialog.html',
                    controller: 'SmsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                text: null,
                                sendDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('sms', null, { reload: true });
                }, function() {
                    $state.go('sms');
                });
            }]
        })
        .state('sms.edit', {
            parent: 'sms',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sms/sms-dialog.html',
                    controller: 'SmsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Sms', function(Sms) {
                            return Sms.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('sms', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('sms.delete', {
            parent: 'sms',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sms/sms-delete-dialog.html',
                    controller: 'SmsDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Sms', function(Sms) {
                            return Sms.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('sms', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
