'use strict';

describe('Controller Tests', function() {

    describe('Training Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockTraining, MockUser, MockLevel, MockGym;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockTraining = jasmine.createSpy('MockTraining');
            MockUser = jasmine.createSpy('MockUser');
            MockLevel = jasmine.createSpy('MockLevel');
            MockGym = jasmine.createSpy('MockGym');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Training': MockTraining,
                'User': MockUser,
                'Level': MockLevel,
                'Gym': MockGym
            };
            createController = function() {
                $injector.get('$controller')("TrainingDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'volleyApp:trainingUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
