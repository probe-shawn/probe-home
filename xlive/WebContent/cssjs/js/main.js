requirejs.config({
    baseUrl: (location.host.indexOf('localhost')>=0)?'/cssjs/js/lib':'/cssjs-build/js/lib',
    //baseUrl: (location.host.indexOf('localhost')>=0)?'/cssjs/js/lib':'/cssjs/js/lib',
    //urlArgs: 'bust='+(new Date().getTime()),
    paths: {
    	app:'../app',
        jquery:['jquery-1.11.2','http://code.jquery.com/jquery-1.11.2.min'],
    	jqm: ['jquery.mobile-1.4.5','http://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min'],
 		cookie:'jquery.cookie',
 	   	alive:'alive',
 	   	iscroll:'iscroll-probe',
 	   	masonry:'jquery.masonry'
    },
	shim: {
	    'jqm': ['jquery'],
	    'alive':['jqm'],
	    'cookie':['jquery'],
	    'masonry':['jquery']
	}
});
requirejs(['jquery','jqm','cookie','alive','masonry'],
function($,$$,c,ALIVE) {
	require(['app/system'],function(SYSTEM){
		//$.ajaxSetup({cache: false});
		SYSTEM.start().done(function(){
			var page = a$.qString()['page'];
			var hash = window.location.hash;
			page = (page) ? page+hash : 'home.html';
			$.mobile.pageContainer.pagecontainer('change',page, {transition:'pop',dataUrl:(hash!='')?page:''});
		});
	});
});
