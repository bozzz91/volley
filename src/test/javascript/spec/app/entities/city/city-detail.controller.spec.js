'use strict';

describe('Controller Tests', function() {

    describe('City Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockCity, MockGym;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockCity = jasmine.createSpy('MockCity');
            MockGym = jasmine.createSpy('MockGym');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'City': MockCity,
                'Gym': MockGym
            };
            createController = function() {
                $injector.get('$controller')("CityDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'volleyApp:cityUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
