define(['jquery','jqm'], 
function($,$$){
	var PLUGIN_NS = 'aTurn';
	var defaults = {
		speed:450,
		overlay:true,
		fliptimingfunction: 'linear',
		horz:true,
		getPages:function(){},
		complete:function(){return true;}
	};
	$.fn.aTurn = function (method) {
		var $this = $(this),plugin = $this.data(PLUGIN_NS);
		if(plugin && typeof method === 'string') {
			if(plugin[method]) {
				return plugin[method].apply(plugin, Array.prototype.slice.call(arguments, 1));
			}else $.error('Method ' + method + ' does not exist on jQuery.aTurn');
		}else if(!plugin && (typeof method === 'object' || !method)) {
			return init.apply(this, arguments);
		}
		return $this;
	};
	$.fn.aTurn.defaults = defaults;
	function init(options) {
		if (!options) options = {};
		options = $.extend(true,{}, $.fn.aTurn.defaults, options);
		return this.each(function () {
			var $this = $(this);
			var plugin = $this.data(PLUGIN_NS);
			if (!plugin) {
				plugin = new aTurn(this,options);
				$this.data(PLUGIN_NS, plugin);
			};
		});
	};
	function aTurn(element, options) {
		this.initial(element, options);
	};	
	////////////////////////////////////////////////////////////////////////
	aTurn.prototype = {
		initial:function(element, options){
			this.$element = $(element);
			this.$element.addClass('aTurn');
			this.options=options;
			/**run time***/
			//this.speed=this.options.speed;
			//this.swipe='r2l';
			//this.angle=180;
			//this.track=0;
			//this.width;
			//this.height;
			//this.animating;
			/*****/
			this.reSize();
			var _self = this;
			this.$element.aSwipe({
				disabled:options.disabled,
				swipeStatus:function(event, phase, start, stop) {
					switch(phase) {
						case 'start' :
							if(_self.animating) return false;
							_self.swipe = (_self.options.horz) ? ((start.x > stop.x) ? 'r2l' : 'l2r') : (start.y > stop.y)? 'b2t' : 't2b';
							if(_self.swipe=='r2l'){
								_self.track = start.x >_self.width/2 ? 2*start.x-_self.width : start.x;
							}else if(_self.swipe=='l2r'){
								_self.track = start.x > _self.width/2 ? _self.width-start.x : _self.width - 2*start.x;
							}else if(_self.swipe=='b2t'){
								_self.track = start.y > _self.height/2 ? 2*start.y-_self.height : start.y;
							}else if(_self.swipe=='t2b'){
								_self.track = start.y > _self.height/2 ? _self.height-start.y : _self.height - 2*start.y;
							}
							_self.makePages(_self.options.getPages(_self.swipe));
							break;
						case 'move':
							if(_self.animating) return false;
							_self.angle = _self.calcAngle(start,stop,_self.track);
							if(!_self.pages.next && (_self.swipe=='r2l' || _self.swipe=='b2t') && (_self.angle > 80)) _self.angle = 80;
							if(!_self.pages.prev && (_self.swipe=='l2r' || _self.swipe=='t2b') && (_self.angle < 100)) _self.angle = 100;
							_self.angle = (_self.angle > 180)?180:(_self.angle < 0) ? 0 : _self.angle;
							_self.turnPage(_self.angle,true);
							break;
						case 'end' :
							if(_self.animating) return false;
							_self.animating = true;
							_self.noop = false;
							if(!_self.pages.next && (_self.swipe=='r2l' || _self.swipe=='b2t')) _self.noop = true;
							if(!_self.pages.prev && (_self.swipe=='l2r' || _self.swipe=='t2b')) _self.noop = true;
							if((_self.angle==0 && (_self.swipe=='r2l'|| _self.swipe=='b2t'))|| (_self.angle==180 &&(_self.swipe=='l2r'||_self.swipe=='t2b'))) {
								_self.noop = true;
							}
							if(_self.angle==0 || _self.angle==180) return _self.endFlip();
							_self.calcSpeed((_self.noop) ? 180 -_self.angle : _self.angle);
							_self.angle= (_self.noop)?((_self.swipe=='l2r' || _self.swipe =='t2b')?180:0) : (_self.swipe=='l2r' || _self.swipe =='t2b') ? 0:180;
							//
							setTimeout(function(){_self.turnPage(_self.angle,false);},1);
							break;
					}
				}
			});
		},
		nextPage:function(pages,cb,speed){
			var _self=this;
			_self.animating = true;
			if(this.options.horz) this.swipe='r2l';
			else this.swipe='b2t';
			_self.makePages((pages) ? pages : _self.options.getPages());
			_self.angle = 180;
			if(speed) this.speed=speed;
			else this.speed=this.options.speed;
			this.noop=false;
			var check = setTimeout(function(){
				check=null;
				_self.endFlip(cb);
			},this.speed*1.5);
			this.$page.off(a$.css.transitionend).on(a$.css.transitionend, function(){
				if(check){
					clearTimeout(check);
					_self.endFlip(cb);
				}
			});
			setTimeout(function(){
				_self.turnPage(_self.angle,false);
			},2);
		},
		prevPage:function(pages,cb,speed){
			var _self=this;
			_self.animating = true;
			if(this.options.horz) this.swipe='l2r';
			else this.swipe='t2b';
			_self.makePages((pages)?pages:_self.options.getPages(),cb);
			_self.angle = 1;
			if(speed) this.speed=speed;
			else this.speed=this.options.speed;
			this.noop=false;
			var check = setTimeout(function(){
				check=null;
				_self.endFlip(cb);
			},this.speed*1.5);
			this.$page.off(a$.css.transitionend).on(a$.css.transitionend, function(){
				if(check){
					clearTimeout(check);
					_self.endFlip(cb);
				}
			});
			setTimeout(function(){
				_self.turnPage(_self.angle,false);
			},2);
		},
		endFlip:function(cb){
			var data={'noop':this.noop,'swipe':this.swipe};
			data.page = (this.noop)? this.pages.page : (this.swipe=='r2l' || this.swipe=='b2t') ? this.pages.next : this.pages.prev;
			if(!data.page) {
				console.log('data page null');
				console.log(this);
			}
			this.options.complete(data);
			if(cb) setTimeout(function(){;cb(data);}, 1);
			this.$element.find('.turn-page-viewport').remove();
			this.animating = false;
		},
		makePages:function(pages){
			var _self = this;
			this.pages=pages;
			if(!pages.page2) pages.page2=pages.page.clone();
			if(this.swipe == 'r2l'){
				this.$flip = this.$pattern_r2l.clone();
				this.$prev = this.$flip.find('#prev .content').append(pages.page2).parent();
				this.$prev = this.$flip.find('#prev');
				this.$page = this.$flip.find('#page.turn-page');
				this.$page.find('#front .content').append(pages.page);
				this.$page.find('#back .content').append((pages.next)?pages.next.clone():$('<div></div>'));
				this.$next = this.$flip.find('#next .content').append((pages.next)?pages.next:$('<div></div>')).parent();
			}else if(this.swipe == 'l2r'){
				this.$flip = this.$pattern_l2r.clone();
				this.$prev = this.$flip.find('#prev .content').append((pages.prev) ? pages.prev:$('<div></div>')).parent();
				this.$page = this.$flip.find('#page.turn-page');
				this.$page.find('#front .content').append((pages.prev) ? pages.prev.clone():$('<div></div>'));
				this.$page.find('#back .content').append(pages.page);
				this.$next = this.$flip.find('#next .content').append(pages.page2).parent();//
				this.$next = this.$flip.find('#next');
			}else if(this.swipe == 'b2t'){
				this.$flip = this.$pattern_b2t.clone();
				this.$prev = this.$flip.find('#prev .content').append(pages.page2).parent();
				this.$prev = this.$flip.find('#prev');
				this.$page = this.$flip.find('#page.turn-page');
				this.$page.find('#front .content').append(pages.page);
				this.$page.find('#back .content').append((pages.next)?pages.next.clone():$('<div></div>'));
				this.$next = this.$flip.find('#next .content').append((pages.next)?pages.next:$('<div></div>')).parent();
			}else if(this.swipe == 't2b'){
				this.$flip = this.$pattern_t2b.clone();
				this.$prev = this.$flip.find('#prev .content').append((pages.prev) ? pages.prev:$('<div></div>')).parent();
				this.$page = this.$flip.find('#page.turn-page');
				this.$page.find('#front .content').append((pages.prev) ? pages.prev.clone():$('<div></div>'));
				this.$page.find('#back .content').append(pages.page);
				this.$next = this.$flip.find('#next .content').append(pages.page2).parent();
				this.$next = this.$flip.find('#next');
			}
			this.$flip.appendTo(this.$element);
			this.$page.off(a$.css.transitionend).on(a$.css.transitionend, function(event){
				if(event.target==_self.$page[0]){
					_self.$page.off(a$.css.transitionend);
					_self.endFlip();
				}
			});
		},
		reSize:function(){
			this.width=this.$element.outerWidth();
			this.height=this.$element.outerHeight();
		},
		calcAngle:function(start,stop,track){
			var angle = 0, dx=stop.x-start.x,dy=stop.y-start.y;
			if(this.swipe=='r2l') {
				angle = (dx > 0) ? 0 : (-dx)*180/track;
			}else if(this.swipe=='l2r') {
				angle = (dx< 0) ? 180 : 180-(dx*180/track);
			}else if(this.swipe=='b2t'){
				angle = (dy > 0) ? 0 : (-dy)*180/track;
			}else if(this.swipe=='t2b') {
				angle = (dy< 0) ? 180 : 180-(dy*180/track);
			}
			return angle;
		},
		calcSpeed:function(calc_angle) {
			var angle = (calc_angle||this.angle);
			if(this.swipe === 'l2r') this.speed = ( this.options.speed / 180 ) * angle;
			else if(this.swipe === 'r2l') this.speed = - ( this.options.speed / 180 ) * angle + this.options.speed;
			else if(this.swipe === 't2b') this.speed = ( this.options.speed / 180 ) * angle ;
			else if(this.swipe === 'b2t') this.speed = -( this.options.speed / 180 ) * angle + this.options.speed;
			this.speed  = (this.speed  < 10) ? 10 :this.speed ;
		},
		turnPage:function(angle,manual) {
			if(manual) {
				this.$page.css(a$.css.prefix('transition'),'');
			}else{
				this.$page.css(a$.css.prefix('transition'),a$.css.prefix('transform')+' '+this.speed + 'ms ' + this.options.fliptimingfunction);
			}
			if(this.options.horz) this.$page.css(a$.css.prefix('transform'), 'rotateY(-' + angle + 'deg) translate3d(0,0,0)');
			else this.$page.css(a$.css.prefix('transform'), 'rotateX(' + angle + 'deg) translate3d(0,0,0)');
			if(this.options.overlay)this.overlay(angle,manual);
		},
		overlay:function(angle,manual) {
			if(manual) {
				var after_opacity = - (1 / 90) * angle + 1, before_opacity = (1 / 90) * angle - 1;
				this.$next.find('.overlay').css('opacity',after_opacity);	
				this.$prev.find('.overlay').css('opacity',before_opacity);
				this.$page.find('.overlay').css('opacity', 0.1);
			} else {
				this.$next.find('.overlay').css('opacity',0);	
				this.$prev.find('.overlay').css('opacity',0);
				return;
				//////////////////////////////////////////////////
				var $after=this.$next.find('.overlay');
				var $before=this.$prev.find('.overlay');
				var _self = this;
				var afterspeed	= this.speed, beforespeed = this.speed, margin = 100,options_half_speed=this.options.speed/2; 
				if(this.noop &&(this.swipe === 'l2r' || this.swipe === 't2b')){
					$after.hide();
				} else {
					var afterdelay = 0;
					if(this.swipe === 'l2r' || this.swipe === 't2b') {
						if(this.speed > options_half_speed) afterdelay = this.speed-options_half_speed;
						afterspeed = this.speed-margin-afterdelay;
					}else {
						if(this.speed > options_half_speed)	afterspeed = this.speed-options_half_speed - margin;
						else $after.hide(); 
					}
					if(afterspeed <= 0) afterspeed = 1;
					$after.css(a$.css.prefix('transition'), 'all ' + afterspeed + 'ms ' + this.options.fliptimingfunction);
					$after.on(a$.css.transitionend, function(event){
						$after.hide();
					});
					setTimeout(function(){
						$after.css({'opacity' : (_self.swipe === 'r2l' ||_self.swipe === 'b2t') ? 0 : 0.9});
					}, (afterdelay||1));
					setTimeout(function(){$after.css('opacity',0);},afterspeed);
				}
				// before
				if(this.noop && (this.swipe === 'r2l' || this.swipe === 'b2t')){
					$before.hide();
				} else {
					var beforedelay = 0;
					if(this.swipe === 'r2l' || this.swipe === 'b2t')  {
						if(this.speed > options_half_speed) beforedelay = this.speed - options_half_speed;
						beforespeed = this.speed - beforedelay -margin;
					}else {
						if(this.speed > options_half_speed) beforespeed = this.speed-options_half_speed-margin;
						else $before.hide();
					}
					if(beforespeed <= 0) beforespeed = 1;
					$before.css(a$.css.prefix('transition'),'all ' + beforespeed + 'ms ' + this.options.fliptimingfunction);
					$before.one(a$.css.transitionend, function(event){
						$before.css('opacity',0);
					});
					setTimeout(function(){
						$before.css({'opacity' : (_self.swipe === 'r2l'||_self.swipe === 'b2t') ? 0.9 : 0});
					}, (beforedelay||1));
					setTimeout(function(){$before.css('opacity',0);},beforespeed);
				}
			}
		},
		destroy:function(){
			this.$element.aSwipe('destroy');
			this.$element.removeClass('aTurn');
			this.$element.data(PLUGIN_NS,null);
		},
		option:function(key,value){
			this.options[key]=value;
		}
	};
	
	aTurn.prototype.$pattern_r2l = $(
	'<div id="r2l" class="turn-page-viewport" style="position:absolute;left:0px;top:0px;width:100%;height:100%;-webkit-perspective:2200px;-moz-perspective:2200px;-o-perspective: 2200px;-ms-perspective: 2200px;perspective: 2200px;overflow:hidden;z-index:1001">'+
		'<div id="prev" class="turn-page" style="position:absolute;left:0px;top:0px;width:50%;height:100%;overflow:hidden;">'+
			'<div class="content" style="position:absolute;left:0px;top:0px;width:200%;height:100%;"></div>'+
			'<div class="overlay" style="position:absolute;left:0px;top:0px;width:100%;height:100%;background-color: rgba(0, 0, 0, 0.3);opacity:0;"></div>'+
		'</div>'+
		'<div id="page" class="turn-page" style="position:absolute;left:50%;top:0px;width:50%;height:100%;z-index:1;'+
			'transform-style: preserve-3d;-webkit-transform-style: preserve-3d;-moz-transform-style: preserve-3d;-o-transform-style: preserve-3d;-ms-transform-style: preserve-3d;'+
			'transform-origin: left center;-webkit-transform-origin: left center;-moz-transform-origin: left center;-o-transform-origin: left center;-ms-transform-origin: left center;'+
		    //'backface-visibility: hidden;-webkit-backface-visibility: hidden;-moz-backface-visibility: hidden;-ms-backface-visibility: hidden;-o-backface-visibility: hidden;'+
		    '-webkit-transform:rotateY(0deg) translate3d(0,0,0);-moz-transform:rotateY(0deg) translate3d(0,0,0);-ms-transform:rotateY(0deg) translate3d(0,0,0);-o-transform:rotateY(0deg) translate3d(0,0,0);'+
		   '">'+
			'<div id="front" style="position:absolute;left:0px;top:0px;width:100%;height:100%;overflow:hidden;background:#fff;'+
					'transform-style: preserve-3d;-webkit-transform-style: preserve-3d;-moz-transform-style: preserve-3d;-o-transform-style: preserve-3d;-ms-transform-style: preserve-3d;'+
					'-webkit-transform: rotateY(0deg);-moz-transform: rotateY(0deg);-o-transform: rotateY(0deg);-ms-transform: rotateY(0deg);'+
				    'backface-visibility: hidden;-webkit-backface-visibility: hidden;-moz-backface-visibility: hidden;-ms-backface-visibility: hidden;-o-backface-visibility: hidden;'+
			'">'+
				'<div class="content" style="position:absolute;left:-100%;top:0px;width:200%;height:100%;"></div>'+
				'<div class="overlay" style="position:absolute;left:0px;top:0px;width:100%;height:100%;background-color: rgba(0, 0, 0, 0.2);opacity:0;"></div>'+
			'</div>'+
			'<div id="back" style="position:absolute;left:0px;top:0px;width:100%;height:100%;overflow:hidden;background:#fff;'+
					'transform-style: preserve-3d;-webkit-transform-style: preserve-3d;-moz-transform-style: preserve-3d;-o-transform-style: preserve-3d;-ms-transform-style: preserve-3d;'+
					'-webkit-transform: rotateY(180deg);-moz-transform: rotateY(180deg);-o-transform: rotateY(180deg);-ms-transform: rotateY(180deg);'+
		   			'backface-visibility: hidden;-webkit-backface-visibility: hidden;-moz-backface-visibility: hidden;-ms-backface-visibility: hidden;-o-backface-visibility: hidden;'+
					'">'+
				'<div class="content" style="position:absolute;left:0%;top:0px;width:200%;height:100%;"></div>'+
				'<div class="overlay" style="position:absolute;left:0px;top:0px;width:100%;height:100%;background-color: rgba(0, 0, 0, 0.2);opacity:0;"></div>'+
			'</div>'+
		'</div>'+
		'<div id="next" class="turn-page" style="position:absolute;left:50%;top:0px;width:50%;height:100%;overflow:hidden;">'+
			'<div class="content" style="position:absolute;left:-100%;top:0px;width:200%;height:100%;"></div>'+
			'<div class="overlay" style="position:absolute;left:0px;top:0px;width:100%;height:100%;background-color: rgba(0, 0, 0, 0.3);opacity:0;"></div>'+
		'</div>'+
	'</div>');
	aTurn.prototype.$pattern_l2r = aTurn.prototype.$pattern_r2l.clone();
	aTurn.prototype.$pattern_l2r.find('#page.turn-page').css(a$.css.prefix('transform'),'rotateY(-180deg)');
	aTurn.prototype.$pattern_b2t = $(
	'<div id="b2t" class="turn-page-viewport" style="position:absolute;left:0px;top:0px;width:100%;height:100%;-webkit-perspective:2200px;-moz-perspective:2200px;-o-perspective: 2200px;-ms-perspective: 2200px;perspective: 2200px;overflow:hidden;z-index:1001">'+
		'<div id="prev" class="turn-page" style="position:absolute;left:0px;top:0px;width:100%;height:50%;overflow:hidden">'+
			'<div class="content" style="position:absolute;left:0px;top:0px;width:100%;height:200%;"></div>'+
			'<div class="overlay" style="position:absolute;left:0px;top:0px;width:100%;height:100%;background-color: rgba(0, 0, 0, 0.2);opacity:0;"></div>'+
		'</div>'+
		'<div id="page" class="turn-page" style="position:absolute;left:0px;top:50%;width:100%;height:50%;z-index:1;'+
			'transform-style: preserve-3d;-webkit-transform-style: preserve-3d;-moz-transform-style: preserve-3d;-o-transform-style: preserve-3d;-ms-transform-style: preserve-3d;'+
			'transform-origin: center top;-webkit-transform-origin: center top;-moz-transform-origin: center top;-o-transform-origin: center top;-ms-transform-origin: center top;'+
		    //'backface-visibility: hidden;-webkit-backface-visibility: hidden;-moz-backface-visibility: hidden;-ms-backface-visibility: hidden;-o-backface-visibility: hidden;'+
		    '-webkit-transform:rotateX(0deg) translate3d(0,0,0);-moz-transform:rotateX(0deg) translate3d(0,0,0);-ms-transform:rotateX(0deg) translate3d(0,0,0);-o-transform:rotateX(0deg) translate3d(0,0,0);'+
		   '">'+
			'<div id="front" style="position:absolute;left:0px;top:0px;width:100%;height:100%;overflow:hidden;background:#fff;z-index:1;'+
					'transform-style: preserve-3d;-webkit-transform-style: preserve-3d;-moz-transform-style: preserve-3d;-o-transform-style: preserve-3d;-ms-transform-style: preserve-3d;'+
					'transform: rotateX(0deg);-webkit-transform: rotateX(0deg);-moz-transform: rotateX(0deg);-o-transform: rotateX(0deg);-ms-transform: rotateX(0deg)'+
				    'backface-visibility: hidden;-webkit-backface-visibility: hidden;-moz-backface-visibility: hidden;-ms-backface-visibility: hidden;-o-backface-visibility: hidden;'+
			'">'+
				'<div class="content" style="position:absolute;left:0px;top:-100%;width:100%;height:200%;"></div>'+
				'<div class="overlay" style="position:absolute;left:0px;top:0px;width:100%;height:100%;background-color: rgba(0, 0, 0, 0.2);opacity:0;"></div>'+
			'</div>'+
			'<div id="back" style="position:absolute;left:0px;top:0px;width:100%;height:100%;overflow:hidden;background:#fff;z-index:1;'+
					'transform-style: preserve-3d;-webkit-transform-style: preserve-3d;-moz-transform-style: preserve-3d;-o-transform-style: preserve-3d;-ms-transform-style: preserve-3d;'+
					'-webkit-transform: rotateX(180deg);-moz-transform: rotateX(180deg);-o-transform: rotateX(180deg);-ms-transform: rotateX(180deg);'+
		   			'backface-visibility: hidden;-webkit-backface-visibility: hidden;-moz-backface-visibility: hidden;-ms-backface-visibility: hidden;-o-backface-visibility: hidden;'+
					'">'+
				'<div class="content" style="position:absolute;left:0%;top:0px;width:100%;height:200%;"></div>'+
				'<div class="overlay" style="position:absolute;left:0px;top:0px;width:100%;height:100%;background-color: rgba(0, 0, 0, 0.2);opacity:0;"></div>'+
			'</div>'+
		'</div>'+
		'<div id="next" class="turn-page" style="position:absolute;left:0px;top:50%;width:100%;height:50%;overflow:hidden;">'+
			'<div class="content" style="position:absolute;left:0px;top:-100%;width:100%;height:200%;"></div>'+
			'<div class="overlay" style="position:absolute;left:0px;top:0px;width:100%;height:100%;background-color: rgba(0, 0, 0, 0.2);opacity:0;"></div>'+
		'</div>'+
	'</div>');
	
	aTurn.prototype.$pattern_t2b = aTurn.prototype.$pattern_b2t.clone();
	aTurn.prototype.$pattern_t2b.find('#page.turn-page').css(a$.css.prefix('transform'),'rotateX(-180deg)');

	
	
	

	////////////////////////////////////////////////////////////////////////////

});