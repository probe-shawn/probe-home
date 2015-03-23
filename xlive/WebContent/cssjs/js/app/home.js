define(['iscroll','app/common'],function(ISCROLL,COMMON){
	return {	
		start:function(){	
			this.$page = $.mobile.pageContainer.pagecontainer('getActivePage');
			COMMON.plug(this.$page);
			COMMON.on(this.$page);
			COMMON.resize(this.$page);
			COMMON.scrollToHash(this.$page,window.location.hash);
		}
	};
});