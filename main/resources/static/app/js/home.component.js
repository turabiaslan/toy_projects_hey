angular.module('hey')
    .component('home', {
        templateUrl: '/app/template/home.html',
        controller: function ($scope, AuthenticationService, ChatApi, AccountApi, ContactApi, Upload, $q, SocketService, $routeParams, $location, $timeout, $interval) {

            let n = 0;
            $scope.messages = [];



            $scope.showAllConversations = function () {
                ChatApi.showAllConversations(function (response) {
                    $scope.setConversations(response);
                    n = 0;
                    for (let y of response) {
                        console.log($scope.conversations);
                        SocketService.subscribe('/hey/chat/update' + $scope.username + y.chatId, function (message) {
                            $scope.messages.push(message);
                            console.log($scope.messages);
                            $scope.$apply();
                        });
                    };
                });
            };

            $scope.updateGroup = function () {
                $location.path("/update-group/" + c_id);
            }

            $scope.logout = function () {
                AccountApi.logout();
                $location.path("/login");
            };

            $scope.setTyp = function () {
                $scope.typ = "";
            };

            $scope.pageController = function () {
                switch (n) {
                    case 0:
                        return 0;
                        break;
                    case 1:
                        return 1;
                        break;
                    case 2:
                        return 2;
                        break;
                    case 3:
                        return 3;
                        break;

                }
            }


            $scope.searchUserOrChat = function () {
                AccountApi.search({s: $scope.sParam}, function (response) {
                    $scope.result = response;
                    n = 2;
                });
            };

            $scope.readChat = function (x) {
                ChatApi.readChat({id: x.chatId}, function (response) {
                    $scope.c_name = x.chatName;
                    $scope.c_avatar = x.avatar;
                    $scope.messages = response.messages;
                    n = 1;
                    $scope.c_g = x.g;
                    if (!x.g) {
                        $scope.state();
                    }
                    c_id = response.id;


                    SocketService.subscribe('/hey/chat/messages/' + c_id + $scope.username, function (response) {
                        $scope.messages = response;
                        $scope.$apply();

                    });


                    SocketService.subscribe('/hey/chat/typing/' + c_id + $scope.username, function (response) {
                        $scope.typ = response;
                        $scope.$apply();

                    });
                    $interval(function () {
                        $scope.setTyp();
                    }, 500);

                });
            };


            $scope.leaveGroup = function () {
                ChatApi.leaveGroup({id: c_id}, function () {

                });
            };

            $scope.state = function () {
                AccountApi.stateQuery({username: $scope.c_name}, function (response) {
                    $scope.current_state = response;
                });
            };

            $scope.send = function () {
                ChatApi.sendMessage({id: c_id, content: $scope.message.content}, function (response) {
                    let message = {id: c_id, content: $scope.message.content};
                    $scope.message = [];
                    let index = $scope.conversations.map(function (e) {
                        return e.chatId
                    }).indexOf(c_id);
                    let xx = $scope.conversations[index];
                    $scope.conversations.splice(index, 1);
                    $scope.conversations.splice(0, 0, xx);
                });
            };

            $scope.newChatPage = function (x) {
                $scope.nCAccount = x.account;
                n = 3;


            };

            $scope.newChat = function () {
                AccountApi.createNewChat({
                    name: $scope.nCAccount.username,
                    content: $scope.message.content1
                }, function (response) {
                    $scope.showAllConversations();
                    $scope.message = [];
                });
            }

            $scope.showProfile = function () {
                $location.path("/profile");
            };

            $scope.isSender = function (m) {
                return (m.sender == $scope.username)
            };

            $scope.typing = function () {
                ChatApi.typing({id: c_id}, function () {

                });
            };
            $scope.seeMessages = function () {
                ChatApi.seeMessages({id: c_id}, function () {

                });
            };
            $scope.setConversations = function (x) {
              $scope.conversations = $scope.conversations.concat(x);
            };

            $scope.getConversation = function () {
                return $scope.conversations;
            };


            $scope.init = function () {
                $scope.loggedIn = AuthenticationService.getLoggedIn();
                $scope.profile = AuthenticationService.getProfile();
                $scope.username = AuthenticationService.getUsername();
                $scope.pageController();
                $scope.message = [];
                let c_id = "";
                $scope.conversations = [];
                $scope.showAllConversations();



                SocketService.subscribe('/hey/chat/conversation' + $scope.username, function (chat) {
                    let index = $scope.conversations.map(function (e) {
                        return e.chatId
                    }).indexOf(chat.chatId);
                    $scope.conversations.splice(index, 1);
                    $scope.conversations.splice(0, 0, chat);
                    $scope.$apply();
                });
                SocketService.subscribe('/hey/chat/state/' + $scope.username, function (response) {
                    $scope.current_state = response;
                    $scope.$apply();

                });







            };

            $scope.init();

        }
    })
