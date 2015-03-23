define(['jquery','jqm'], 
		
function($,$$){

	var supportTouch = $.mobile.support.touch,
	touchStartEvent = supportTouch ? "touchstart" : "mousedown",
	touchStopEvent = supportTouch ? "touchend touchcancel" : "mouseup",
	touchMoveEvent = supportTouch ? "touchmove" : "mousemove";
	
	/*
	if(window.navigator.msPointerEnabled){
		touchStartEvent = touchStartEvent+" MSPointerDown" ;
		touchStopEvent = touchStopEvent+ " MSPointerUp";
		touchMoveEvent = touchMoveEvent+" MSPointerMove";
	}
	*/
	/*
	touchStartEvent = (window.PointerEvent)? "pointerdown mousedown" : "touchstart mousedown";
	touchStopEvent = (window.PointerEvent) ? "pointerup mouseup" :"touchend touchcancel mouseup";
	touchMoveEvent = (window.PointerEvent) ? "pointermove mousemove" : "touchmove mousemove";
	*/
	touchStartEvent = "touchstart mousedown";
	touchStopEvent = "touchend touchcancel mouseup";
	touchMoveEvent = "touchmove mousemove";

	
	
	
	var PLUGIN_NS = 'aSwipe';
	var defaults = {
		startPreventDefault:true,
		movePreventDefault:true,
		excludedElements : "button, input, select, textarea, a, .noSwipe",
		checkExcludedElements:false,
		fireTapEvent:true,
		//
		//scrollSupressionThreshold: 30, 
		//durationThreshold: 1000, 
		xDistanceThreshold: 8,  
		yDistanceThreshold: 8,
		swipeStatus:null
	};
	$.fn.aSwipe = function (method) {
		var $this = $(this),plugin = $this.data(PLUGIN_NS);
		if (plugin && typeof method === 'string') {
			if (plugin[method]) {
				 return plugin[method].apply(plugin, Array.prototype.slice.call(arguments, 1));
			}else $.error('Method ' + method + ' does not exist on jQuery.aSwipe');
		}else if (!plugin && (typeof method === 'object' || !method)) {
			return init.apply(this, arguments);
		}
		return $this;
	};
	
	$.fn.aSwipe.defaults = defaults;
	function init(options) {
		if (!options) options = {};
		options = $.extend(true,{}, $.fn.aSwipe.defaults, options);
		return this.each(function () {
			var $this = $(this);
			var plugin = $this.data(PLUGIN_NS);
			if (!plugin) {
				plugin = new aSwipe(this,options);
				$this.data(PLUGIN_NS, plugin);
			};
		});
	}
	function aSwipe(element, options) {
		this.initial(element, options);
	};	
	aSwipe.prototype = {
		initial:function(element, options){
			this.options=options;
			var $element = $(element),start=undefined,stop=undefined,started=false,end_time = 0,moved=false;
			this.$element=$element;
			$element.on(touchStartEvent, this.touchStartHandle=function(event) {
				if(options.checkExcludedElements && $(event.target).closest(options.excludedElements,$element).length>0) return;
				if(start && stop && options.swipeStatus && started !== false) {
					options.swipeStatus(event,"end",$.extend(true,{},start),$.extend(true,{},stop));
					start = stop = undefined;
					started = false;
				}
				var data = event.originalEvent.touches ? event.originalEvent.touches[0] : event;
				var loc = $.event.special.swipe.getLocation(data);
				start = {
						time: (new Date()).getTime(),
						x:loc.x,y:loc.y,
						coords: [loc.x,loc.y],
						origin: $(event.target)
				};
				if(options.swipeStatus) options.swipeStatus(event,"touch-down",start,(stop||start));
				moved=false;
				started=false;
				stop=undefined;
				function moveHandler(event) {
					if(!start) return;
					var data = event.originalEvent.touches ? event.originalEvent.touches[0] : event;
					var loc = $.event.special.swipe.getLocation(data);
					if((new Date().getTime() - end_time > 300) &&  Math.abs(loc.x-start.x) < options.xDistanceThreshold && Math.abs(loc.y-start.y) < options.yDistanceThreshold){
						return; 
					}
					moved=true;
					if(!started){
						var start2 = {
								time: (new Date()).getTime(),
								x:loc.x,y:loc.y,
								coords:[loc.x, loc.y]
							};
						if(options.swipeStatus) started=options.swipeStatus(event,"start",start,start2);
						if(started===false) return;
					}
					started=true;
					stop = {
						time: (new Date()).getTime(),
						x:loc.x,y:loc.y,
						coords:[loc.x, loc.y]
					};
					options.swipeStatus(event,"move",start,stop);
					if(options.movePreventDefault){
						event.preventDefault();
						event.stopPropagation();
						return false;	
					}
				}
				$element.unbind(touchMoveEvent, moveHandler).bind(touchMoveEvent, moveHandler).one(touchStopEvent, function(event) {
					$element.unbind(touchMoveEvent, moveHandler);
					if(start && stop) {
						if(options.swipeStatus) options.swipeStatus(event,"end",$.extend(true,{},start),$.extend(true,{},stop));
					}
					if(start && !stop){
						if(options.swipeStatus) options.swipeStatus(event,"cancel",$.extend(true,{},start),$.extend(true,{},start));
						if(!moved && options.fireTapEvent && options.startPreventDefault) start.origin.trigger("tap",event);//start.origin.trigger("swipeclick",event);
					}
					start = stop = undefined;
					end_time=new Date().getTime();
				});
				if(options.startPreventDefault){
					event.preventDefault();
					event.stopPropagation();
					return false;
				}
			});
		},
		destroy:function(){
			this.$element.off(touchStartEvent,this.touchStartHandle);
			this.$element.data(PLUGIN_NS,null);
		},
		option:function(key,value){
			this.options[key]=value;
		}
	};
	
});