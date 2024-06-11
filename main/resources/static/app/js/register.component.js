angular.module('hey')
    .component('register', {
        templateUrl: '/app/template/register.html',
        controller: function ($scope, AccountApi, $location, $routeParams) {

            $scope.register = function () {
                AccountApi.register({
                    username: $scope.regist.username,
                    password: $scope.regist.password,
                    file: $scope.file
                }, function (response) {
                    $scope.a = response;
                    if (response.success) {
                        toastr.success("Your profile successfully created!")
                        $location.path("/home");
                    } else {
                        toastr.error("Something went wrong please try again!")
                    }
                });
            }
            $scope.init = function () {
                console.log($scope.a)
            };

            $scope.init();
        }
    });