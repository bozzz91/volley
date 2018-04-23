(function() {
    'use strict';

    angular
        .module('volleyApp')
        .controller('TrainingBookingController', TrainingBookingController);

    TrainingBookingController.$inject = ['$timeout', '$scope', '$uibModalInstance', 'entity', 'Training'];

    function TrainingBookingController ($timeout, $scope, $uibModalInstance, entity, Training) {
        var vm = this;

        vm.training = entity;
        vm.bookedUsers = [];

        vm.init = init;
        vm.clear = clear;
        vm.isClear = isClear;
        vm.remove = remove;
        vm.save = save;
        vm.add = add;
        vm.isLimitExceed = isLimitExceed;
        vm.bookingCount = bookingCount;

        function init() {
            var initialBookedUser = createInitBookedUser(0);
            if (vm.training.booking) {
                var names = vm.training.booking.split(',');
                for (var i=0; i < names.length; i++) {
                    var user = JSON.parse(JSON.stringify(initialBookedUser));
                    user.id = i;
                    user.name = names[i].trim();
                    vm.bookedUsers.push(user);
                }
            } else {
                vm.bookedUsers.push(initialBookedUser);
            }
        }

        function isLimitExceed() {
            return vm.bookedUsers.length >= vm.training.limit;
        }

        function bookingCount() {
            return vm.isClear() ? 0 : vm.bookedUsers.length;
        }

        function clear () {
            clearBookedUsers();
            $uibModalInstance.dismiss('cancel');
        }

        function remove(index) {
            if (vm.bookedUsers.length > 1) {
                vm.bookedUsers.splice(index, 1);
            } else {
                clearBookedUsers();
            }
        }

        function createInitBookedUser(index) {
            return {
                'id': index,
                'name': null
            };
        }

        function clearBookedUsers() {
            vm.bookedUsers = [];
            vm.bookedUsers.push(createInitBookedUser(0));
        }

        function save () {
            vm.isSaving = true;
            var userNamesToSave = [];
            for (var i=0; i<vm.bookedUsers.length; i++) {
                if (vm.bookedUsers[i].name) {
                    userNamesToSave.push(vm.bookedUsers[i].name.trim());
                }
            }
            if (userNamesToSave.length > 0) {
                vm.training.booking = userNamesToSave.join(', ');
            } else {
                vm.training.booking = null;
            }
            Training.update(vm.training, onSaveSuccess, onSaveError);
        }

        function add() {
            vm.bookedUsers.push(createInitBookedUser(vm.bookedUsers[vm.bookedUsers.length-1].id + 1));
            $timeout(function (){
                angular.element('.form-group:last>input').focus();
            });
        }

        function onSaveSuccess (result) {
            $scope.$emit('volleyApp:trainingUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function isClear() {
            return vm.bookedUsers.length === 1 && vm.bookedUsers[0].name == null;
        }
    }
})();
