(function() {
    'use strict';

    angular
        .module('volleyApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('home', {
            parent: 'app',
            url: '/',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/home/home.html',
                    controller: 'HomeController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                    $translatePartialLoader.addPart('home');
                    $translatePartialLoader.addPart('training');
                    $translatePartialLoader.addPart('trainingUser');
                    return $translate.refresh();
                }]
            }
        })
        .state('home.booking', {
            parent: 'home',
            url: '/{id}/booking',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/training/training-booking.html',
                    controller: 'TrainingBookingController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'md',
                    resolve: {
                        entity: ['Training', function(Training) {
                            return Training.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('home', null, { reload: true });
                }, function() {
                    $state.go('home');
                });
            }]
        });
    }
})();
