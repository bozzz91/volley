'use strict';

describe('Controller Tests', function() {

    describe('TrainingUser Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockTrainingUser, MockTraining, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockTrainingUser = jasmine.createSpy('MockTrainingUser');
            MockTraining = jasmine.createSpy('MockTraining');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'TrainingUser': MockTrainingUser,
                'Training': MockTraining,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("TrainingUserDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'volleyApp:trainingUserUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
