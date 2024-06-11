let app = angular.module("hey",
    [
        'ngRoute',
        'ngResource',
        'ui.bootstrap',
        'ui.bootstrap.datetimepicker',
        'ngFileUpload'
    ]);

app.config(["$routeProvider", "$locationProvider", "$httpProvider",
    function ($routeProvider, $locationProvider, $httpProvider) {
        $routeProvider
            .when('/login', {
                template: '<login></login>'
            })
            .when('/profile', {
                template: '<profile></profile>'
            })
            .when('/register', {
                template: '<register></register>'
            })
            .when('/new-group', {
                template: '<new-group></new-group>'
            })
            .when('/home', {
                template: '<home></home>'
            })
            .when('/update-group/:id', {
                template: '<update-group></update-group>'
            })
            .when('/features', {
                template: '<features></features>'
            })
            .otherwise("/login");

        $locationProvider.html5Mode(true);

        $httpProvider.interceptors.push(function ($q, $location) {
            return {
                'responseError': function (response) {
                    if (response.status == 401) {
                        $location.path("/login");
                    }
                    return $q.reject(response);
                }
            };
        });
    }]);

app.factory('AccountApi', ['$resource', function ($resource) {
    let baseUrl = "/account";
    return $resource('', {}, {
        register: {
            method: "POST",
            url: baseUrl + "/register",
            transformRequest: function (data) {
                let form = new FormData();
                form.append("username", data.username);
                form.append("password", data.password);
                form.append("file", data.file);
                return form;
            },
            headers: {"Content-Type": undefined}
        },
        login: {
            method: 'POST',
            url: baseUrl + "/login"
        },
        logout: {
            method: 'GET',
            url: baseUrl + "/logout"
        },
        whoami: {
            method: "GET",
            url: baseUrl + "/me"
        },
        list: {
            method: "GET",
            url: baseUrl + "/list",
            isArray: true,
        },
        createGroup: {
            method: "POST",
            url: baseUrl + "/create-group",
            transformRequest: function (data) {
                let form = new FormData();
                form.append("name", data.name);
                form.append("description", data.description);
                form.append("participants", data.participants);
                form.append("file", data.file);
                return form;
            },
            headers: {'Content-Type': undefined}

        },
        search: {
            method: "GET",
            url: baseUrl + "/search",
            isArray: true,
        },
        stateQuery: {
            method: "GET",
            url: baseUrl + "/state"
        },
        saveProfile: {
            method: 'POST',
            url: baseUrl + "/me",
            transformRequest: function (data) {
                let form = new FormData();
                form.append("nickname", data.nickname);
                form.append("about", data.about);
                form.append("file", data.file);
                return form;
            },
            headers: {'Content-Type': undefined}
        },
        createNewChat: {
            method: "POST",
            url: baseUrl + "/new-chat",
            transformRequest: function (data) {
                var str = [];
                for (let d in data)
                    str.push(encodeURIComponent(d) + "=" + encodeURIComponent(data[d]));
                return str.join("&");
            },
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}

        },
        addToGroup: {
            method: "POST",
            url: baseUrl + "/add-to-group",
            transformRequest: function (data) {
                var str = [];
                for (let d in data)
                    str.push(encodeURIComponent(d) + "=" + encodeURIComponent(data[d]));
                return str.join("&");
            },
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        },
        avatar: {
            method: "GET",
            url: baseUrl + "/avatar/:path"
        },
    });

}])
app.factory('ChatApi', ['$resource', function ($resource) {
    let baseUrl = "/chat";
    return $resource('', {}, {
        showAllConversations: {
            method: "GET",
            url: baseUrl + "/all-conversations",
            isArray: true
        },
        readChat: {
            method: "GET",
            url: baseUrl + "/read-chat/:id"
        },
        leaveGroup: {
            method: "GET",
            url: baseUrl + "/leave-group"
        },
        group: {
            method: "GET",
            url: baseUrl + "/group",
        },
        updateGroup: {
            method: "POST",
            url: baseUrl + "/update-group",
            transformRequest: function (data) {
                let form = new FormData();
                form.append("id", data.id);
                form.append("name", data.name);
                form.append("description", data.description);
                form.append("file", data.file);
                return form;
            },
            headers: {'Content-Type': undefined}
        },

        removeFromGroup: {
            method: "POST",
            url: baseUrl + "/remove-from-group",
            transformRequest: function (data) {
                var str = [];
                for (let d in data)
                    str.push(encodeURIComponent(d) + "=" + encodeURIComponent(data[d]));
                return str.join("&");
            },
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        },

        typing: {
            method: "GET",
            url: baseUrl + "/typing"
        },
        addParticipant: {
            method: "POST",
            url: baseUrl + "/add-participant"
        },
        sendMessage: {
            method: "POST",
            url: baseUrl + "/send-message",
            transformRequest: function (data) {
                var str = [];
                for (let d in data)
                    str.push(encodeURIComponent(d) + "=" + encodeURIComponent(data[d]));
                return str.join("&");
            },
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}

        },

        groupAvatar: {
            method: "GET",
            url: baseUrl + "/group-avatar/:path"
        },
        seeMessages: {
            method: "GET",
            url: baseUrl + "/see-messages"
        },
        deneme: {
            method: "POST",
            url: baseUrl + "/"
        }

    });
}])
app.factory('ContactApi', ['$resource', function ($resource) {
    let baseUrl = "/contacts";
    return $resource('', {}, {
        addToContacts: {
            method: "POST",
            url: baseUrl + "/add-to-contacts"
        }
    })
}])
app.factory('AuthenticationService', function (AccountApi) {

    let loggedIn = false;
    let profile = {};
    let observers = [];
    let username = "";

    if (current_user) {
        setLoggedIn(true);
        setProfile(current_user.profile);
        setUsername(current_user.username)
    }

    function setLoggedIn(state) {
        loggedIn = state;
        if (!state) {
            profile = {};
        }
        observers.forEach(f => f());
    }

    function setUsername(u) {
        username = u;
        observers.forEach(f => f());
    }

    function addObserver(f) {
        observers.push(f);
    }

    function setProfile(p) {
        profile = p;
        observers.forEach(f => f());
    }

    return {
        getLoggedIn: function () {
            return loggedIn;
        },

        setLoggedIn: setLoggedIn,
        addObserver: addObserver,

        getProfile: function () {
            return profile;
        },

        getUsername: function () {
            return username
        },

        setUsername: setUsername,

        setProfile: setProfile,

    }

})

app.factory("SocketService", function ($q) {
    let connect = $q.defer();

    let stompClient = null;
    let socket = new SockJS('/chat');

    stompClient = Stomp.over(socket);
    stompClient.debug = false;
    stompClient.connect({}, function (frame) {
        connect.resolve();
    });
    return {
        subscribe: function (path, cb) {
            connect.promise.then(function () {
                stompClient.subscribe(path, function (msg) {
                    cb(JSON.parse(msg.body));
                }, {id: path});
            });
        },
        unsubscribe: function (path) {
            connect.promise.then(function () {
                stompClient.unsubscribe(path);
            });
        }
    }
});

app.directive('ngEnter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if (event.which === 13) {
                scope.$apply(function () {
                    scope.$eval(attrs.ngEnter);
                });

                event.preventDefault();
            }
        });
    };
});

app.filter("timeago", function () {
    //time: the time
    //local: compared to what time? default: now
    //raw: wheter you want in a format of "5 minutes ago", or "5 minutes"
    return function (time, local, raw) {
        if (!time) return "never";

        if (!local) {
            (local = Date.now())
        }

        if (angular.isDate(time)) {
            time = time.getTime();
        } else if (typeof time === "string") {
            time = new Date(time).getTime();
        }

        if (angular.isDate(local)) {
            local = local.getTime();
        } else if (typeof local === "string") {
            local = new Date(local).getTime();
        }

        if (typeof time !== 'number' || typeof local !== 'number') {
            return;
        }

        var
            offset = Math.abs((local - time) / 1000),
            span = [],
            MINUTE = 60,
            HOUR = 3600,
            DAY = 86400,
            WEEK = 604800,
            MONTH = 2629744,
            YEAR = 31556926,
            DECADE = 315569260;

        if (offset <= MINUTE) span = ['', raw ? 'now' : 'less than a minute'];
        else if (offset < (MINUTE * 60)) span = [Math.round(Math.abs(offset / MINUTE)), 'min'];
        else if (offset < (HOUR * 24)) span = [Math.round(Math.abs(offset / HOUR)), 'hr'];
        else if (offset < (DAY * 7)) span = [Math.round(Math.abs(offset / DAY)), 'day'];
        else if (offset < (WEEK * 52)) span = [Math.round(Math.abs(offset / WEEK)), 'week'];
        else if (offset < (YEAR * 10)) span = [Math.round(Math.abs(offset / YEAR)), 'year'];
        else if (offset < (DECADE * 100)) span = [Math.round(Math.abs(offset / DECADE)), 'decade'];
        else span = ['', 'a long time'];

        span[1] += (span[0] === 0 || span[0] > 1) ? 's' : '';
        span = span.join(' ');

        if (raw === true) {
            return span;
        }
        return (time <= local) ? span + ' ago' : 'in ' + span;
    }
});
