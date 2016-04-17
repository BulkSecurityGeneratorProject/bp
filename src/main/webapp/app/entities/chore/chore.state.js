(function() {
    'use strict';

    angular
        .module('bp250App')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('chore', {
            parent: 'entity',
            url: '/chore',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Chores'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/chore/chores.html',
                    controller: 'ChoreController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('chore-detail', {
            parent: 'entity',
            url: '/chore/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Chore'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/chore/chore-detail.html',
                    controller: 'ChoreDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Chore', function($stateParams, Chore) {
                    return Chore.get({id : $stateParams.id});
                }]
            }
        })
        .state('chore.new', {
            parent: 'chore',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/chore/chore-dialog.html',
                    controller: 'ChoreDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                date: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('chore', null, { reload: true });
                }, function() {
                    $state.go('chore');
                });
            }]
        })
        .state('chore.edit', {
            parent: 'chore',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/chore/chore-dialog.html',
                    controller: 'ChoreDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Chore', function(Chore) {
                            return Chore.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('chore', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('chore.delete', {
            parent: 'chore',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/chore/chore-delete-dialog.html',
                    controller: 'ChoreDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Chore', function(Chore) {
                            return Chore.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('chore', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
