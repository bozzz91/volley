(function() {
    'use strict';

    angular
        .module('volleyApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('register', {
            parent: 'account',
            url: '/register',
            data: {
                authorities: [],
                pageTitle: 'register.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/account/register/register.html',
                    controller: 'RegisterController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('register');
                    $translatePartialLoader.addPart('city');
                    return $translate.refresh();
                }]
            }
        });
    }
})();
