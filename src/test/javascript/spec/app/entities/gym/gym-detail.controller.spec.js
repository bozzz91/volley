'use strict';

describe('Controller Tests', function() {

    describe('Gym Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockGym, MockTraining, MockCity;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockGym = jasmine.createSpy('MockGym');
            MockTraining = jasmine.createSpy('MockTraining');
            MockCity = jasmine.createSpy('MockCity');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Gym': MockGym,
                'Training': MockTraining,
                'City': MockCity
            };
            createController = function() {
                $injector.get('$controller')("GymDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'volleyApp:gymUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
