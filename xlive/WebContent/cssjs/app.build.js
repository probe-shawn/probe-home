({
	appDir:"./",
	baseUrl:"js",
	dir:"../cssjs-build",
	optimize:"uglify",
	paths:{
		jquery:"empty:",
		jqm:"empty:",
		alive:"lib/alive",
	},
	modules:[
	    {
	    	name:"app/system"
	    }
	]
})