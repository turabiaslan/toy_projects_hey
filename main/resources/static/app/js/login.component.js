angular.module('hey')
    .component('login', {
        templateUrl: '/app/template/login.html',
        controller: function ($scope, AccountApi, $location, AuthenticationService) {

            $scope.login = function () {
                AccountApi.login($scope.credential, function (response) {
                    if (response.success) {
                        AuthenticationService.setLoggedIn(true);
                        AuthenticationService.setProfile(response.profile);
                        AuthenticationService.setUsername(response.username);
                        console.log(response);

                        $location.path("/home")
                    } else {
                        toastr.error("Username or password is wrong. Please try again")
                    }
                });
            }

            $scope.init = function () {

                $scope.credential = {};
            };

            $scope.init();
        }
    });


