angular.module('hey')
    .component('updateGroup', {
        templateUrl: '/app/template/update-group.html',
        controller: function ($scope, ChatApi, AccountApi, $location) {


            $scope.addParticipant = function () {
                ChatApi.addParticipant($scope.group, $scope.account, function (response) {
                });
            };

            let g_id = location.pathname.split('/').slice(-1)[0]

            $scope.pullGroup = function () {
                ChatApi.group({id: g_id}, function (response){
                    $scope.group = response;
                    console.log(response);
                });
            };

            $scope.save = function () {
                ChatApi.updateGroup({
                    id: g_id,
                    name: $scope.group.name,
                    description: $scope.group.description,
                    file: $scope.file
                }, function (response) {
                    console.log(response);
                    $location.path("/home");
                });
            };

            $scope.add = function (x) {
                AccountApi.addToGroup({id: g_id, participantNew: $scope.participantNew});
            }

            $scope.remove = function (x) {
                ChatApi.removeFromGroup({id: g_id, pRemoval: $scope.pRemoval});
            }


            $scope.init = function () {

                $scope.participantNew= "";
                $scope.pRemoval = "";





                $scope.pullGroup();

                $scope.accounts = AccountApi.list();
            };

            $scope.init();
        }
    });