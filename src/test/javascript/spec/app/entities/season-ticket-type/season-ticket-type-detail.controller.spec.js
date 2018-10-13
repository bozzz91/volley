'use strict';

describe('Controller Tests', function() {

    describe('SeasonTicketType Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockSeasonTicketType, MockOrganization;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockSeasonTicketType = jasmine.createSpy('MockSeasonTicketType');
            MockOrganization = jasmine.createSpy('MockOrganization');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'SeasonTicketType': MockSeasonTicketType,
                'Organization': MockOrganization
            };
            createController = function() {
                $injector.get('$controller')("SeasonTicketTypeDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'volleyApp:seasonTicketTypeUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
