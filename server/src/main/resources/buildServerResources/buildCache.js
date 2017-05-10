
BS.GradleBuildCache = {

    actionsUrl: window['base_uri'] + "/admin/buildCache.html",

    clearCache: function() {
        BS.ajaxRequest(this.actionsUrl, {
            parameters: Object.toQueryString({action: 'clear'}),
            onComplete: function(transport) {
                window.location.reload();
            }
        });
    }
};
