angular.module('hey')
    .component('profile', {
        templateUrl: '/app/template/profile.html',
        controller: function ($scope, AccountApi, $location) {

            $scope.save = function () {
                AccountApi.saveProfile({nickname: $scope.me.profile.nickname, about: $scope.me.profile.about, file: $scope.file},  function (response) {
                    if(response.success){
                        toastr.success("Profile successfully updated");
                        $location.path("/home");
                    }else {
                        toastr.error("Something went wrong. Please try again.")
                    } 
                    
                });
            };

         
        
            $scope.init = function () {
                
                $scope.me = AccountApi.whoami();
            };

            $scope.init();
        }
    });


    


    