(function() {
    'use strict';
    angular
        .module('bp250App')
        .factory('Badge', Badge);

    Badge.$inject = ['$resource', 'DateUtils'];

    function Badge ($resource, DateUtils) {
        var resourceUrl =  'api/badges/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.earnedAt = DateUtils.convertDateTimeFromServer(data.earnedAt);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
