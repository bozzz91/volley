(function() {
    'use strict';

    angular
        .module('volleyApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('faq', {
            parent: 'entity',
            url: '/faq',
            data: {
                //allow all
                //authorities: ['ROLE_USER'],
                pageTitle: 'volleyApp.faq.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/faq/faqs.html',
                    controller: 'FaqController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('faq');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('faq-detail', {
            parent: 'entity',
            url: '/faq/{id}',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'volleyApp.faq.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/faq/faq-detail.html',
                    controller: 'FaqDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('faq');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Faq', function($stateParams, Faq) {
                    return Faq.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('faq.new', {
            parent: 'faq',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/faq/faq-dialog.html',
                    controller: 'FaqDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                index: null,
                                question: null,
                                answer: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('faq', null, { reload: true });
                }, function() {
                    $state.go('faq');
                });
            }]
        })
        .state('faq.edit', {
            parent: 'faq',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/faq/faq-dialog.html',
                    controller: 'FaqDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Faq', function(Faq) {
                            return Faq.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('faq', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('faq.delete', {
            parent: 'faq',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/faq/faq-delete-dialog.html',
                    controller: 'FaqDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Faq', function(Faq) {
                            return Faq.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('faq', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
