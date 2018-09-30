(function() {
    'use strict';

    angular
        .module('volleyApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('organization', {
            parent: 'entity',
            url: '/organization',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'volleyApp.organization.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/organization/organizations.html',
                    controller: 'OrganizationController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('organization');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('organization-detail', {
            parent: 'entity',
            url: '/organization/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'volleyApp.organization.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/organization/organization-detail.html',
                    controller: 'OrganizationDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('organization');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Organization', function($stateParams, Organization) {
                    return Organization.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('organization.new', {
            parent: 'organization',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/organization/organization-dialog.html',
                    controller: 'OrganizationDialogController',
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
                    $state.go('organization', null, { reload: true });
                }, function() {
                    $state.go('organization');
                });
            }]
        })
        .state('organization.edit', {
            parent: 'organization',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/organization/organization-dialog.html',
                    controller: 'OrganizationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Organization', function(Organization) {
                            return Organization.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('organization', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('organization.delete', {
            parent: 'organization',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/organization/organization-delete-dialog.html',
                    controller: 'OrganizationDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Organization', function(Organization) {
                            return Organization.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('organization', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
