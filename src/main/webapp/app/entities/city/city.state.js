(function() {
    'use strict';

    angular
        .module('volleyApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('city', {
            parent: 'entity',
            url: '/city',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'volleyApp.city.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/city/cities.html',
                    controller: 'CityController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('city');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('city-detail', {
            parent: 'entity',
            url: '/city/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'volleyApp.city.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/city/city-detail.html',
                    controller: 'CityDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('city');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'City', function($stateParams, City) {
                    return City.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('city.new', {
            parent: 'city',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/city/city-dialog.html',
                    controller: 'CityDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('city', null, { reload: true });
                }, function() {
                    $state.go('city');
                });
            }]
        })
        .state('city.edit', {
            parent: 'city',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/city/city-dialog.html',
                    controller: 'CityDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['City', function(City) {
                            return City.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('city', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('city.delete', {
            parent: 'city',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/city/city-delete-dialog.html',
                    controller: 'CityDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['City', function(City) {
                            return City.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('city', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
