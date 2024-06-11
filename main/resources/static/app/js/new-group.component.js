angular.module('hey')
    .component('newGroup', {
        templateUrl: '/app/template/new-group.html',
        controller: function ($scope, ChatApi, AccountApi, $location) {

            $scope.save = function () {

                AccountApi.createGroup({
                    name: $scope.group.name,
                    description: $scope.group.description,
                    participants: $scope.group.participants,
                    file: $scope.file
                }, function (response) {
                    console.log(response);
                    $location.path("/home");
                });
            };


            $scope.init = function () {
                $scope.group = {};
                $scope.publicSelected = function (r) {
                    $scope.group.participants = [];
                    $scope.group.participants.push(r);
                }

                $scope.accounts = AccountApi.list();
            };

            $scope.init();
        }
    });