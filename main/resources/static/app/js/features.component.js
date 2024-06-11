angular.module('hey')
    .component('features', {
        templateUrl: '/app/template/features.html',
        controller: function ($scope, AccountApi, ChatApi) {

            $scope.save = function () {
                ChatApi.deneme({name: $scope.name}, $scope.account, function (response) {
                    console.log(response);
                })
            };
            $scope.selected =function (r) {
                $scope.account = r;
            }


            $scope.init = function () {
                $scope.x = {};

                $scope.accounts = AccountApi.list();
                console.log($scope.accounts);

            };

            $scope.init();
        }
    });