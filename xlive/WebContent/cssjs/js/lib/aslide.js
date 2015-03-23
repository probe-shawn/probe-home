define(['jquery','jqm'], 
function($,$$){
	var PLUGIN_NS = 'aSlide';
	var defaults = {
		speed:450,
		overlay:true,
		fliptimingfunction: 'linear',
		horz:true,
		getPages:function(){},
		getExtraPages:function(){},
		complete:function(){return true;}
	};
	$.fn.aSlide = function (method) {
		var $this = $(this),plugin = $this.data(PLUGIN_NS);
		if(plugin && typeof method === 'string') {
			if(plugin[method]) {
				return plugin[method].apply(plugin, Array.prototype.slice.call(arguments, 1));
			}else $.error('Method ' + method + ' does not exist on jQuery.aSlide');
		}else if(!plugin && (typeof method === 'object' || !method)) {
			return init.apply(this, arguments);
		}
		return $this;
	};
	$.fn.aSlide.defaults = defaults;
	function init(options) {
		if (!options) options = {};
		options = $.extend(true,{}, $.fn.aSlide.defaults, options);
		return this.each(function () {
			var $this = $(this);
			var plugin = $this.data(PLUGIN_NS);
			if (!plugin) {
				plugin = new aSlide(this,options);
				$this.data(PLUGIN_NS, plugin);
			};
		});
	};
	function aSlide(element, options) {
		this.initial(element, options);
	};	
	////////////////////////////////////////////////////////////////////////
	aSlide.prototype = {
		initial:function(element, options){
			this.$element = $(element);
			this.$element.addClass('aSlide');
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
				swipeStatus:function(event, phase, start, stop) {
					switch(phase) {
						case 'start' :
							if(_self.animating) return false;
							_self.swipe = (_self.options.horz) ? ((start.x > stop.x) ? 'r2l' : 'l2r') : (start.y > stop.y)? 'b2t' : 't2b';
							_self.makePages(_self.options.getPages(_self.swipe));
							break;
						case 'move':
							if(_self.animating) return false;
							var offset = (_self.options.horz) ? stop.x-start.x: stop.y-start.y;
							if(_self.swipe=='r2l' || _self.swipe=='l2r'){
								var track = (_self.swipe=='r2l') ? start.x : _self.width-start.x;
								var ps =-_self.width+offset*(_self.width/track); 
								_self._translate(_self.$prev, ps>=0 ? 0 : ps, 0, 0);
								_self._translate(_self.$page, offset, 0, 0);
								_self._translate(_self.$next, _self.width/2+offset/2, 0, 0);

							}else if(_self.swipe=='b2t' || _self.swipe=='t2b'){
								var track = (_self.swipe=='b2t') ? start.y : _self.height-start.y;
								var ps =-_self.height+offset*(_self.height/track); 
								_self._translate(_self.$prev, 0,ps>=0 ? 0 : ps , 0);
								_self._translate(_self.$page, 0, offset,0);
								_self._translate(_self.$next, 0, _self.height/2+offset/2,0);
							}
							break;
						case 'end' :
							if(_self.animating) return false;
							_self.animating = true;
							_self.noop = false;
							_self.swipe = (_self.options.horz) ? ((start.x > stop.x) ? 'r2l' : 'l2r') : (start.y > stop.y)? 'b2t' : 't2b';
							_self.calcSpeed(start,stop);
							setTimeout(function(){_self.slidePage();},1);
							break;
					}
				}
			});
		},
		nextPage:function(pages,cb,speed){
			var _self=this;
			_self.animating = true;
			_self.noop = false;
			if(this.options.horz) this.swipe='r2l';
			else this.swipe='b2t';
			_self.makePages((pages) ? pages : _self.options.getPages());
			if(speed) this.speed=speed;
			else this.speed=this.options.speed;
			var check = setTimeout(function(){
				check=null;
				_self.endSlide(cb);
			},this.speed*1.5);
			this.$page.off(a$.css.transitionend).on(a$.css.transitionend, function(){
				if(check){
					clearTimeout(check);
					_self.endSlide(cb);
				}
			});
			setTimeout(function(){
				_self.slidePage();
			},2);
		},
		prevPage:function(pages,cb,speed){
			var _self=this;
			_self.animating = true;
			_self.noop = false;
			if(this.options.horz) this.swipe='l2r';
			else this.swipe='t2b';
			_self.makePages((pages)?pages:_self.options.getPages(),cb);
			if(speed) this.speed=speed;
			else this.speed=this.options.speed;
			var check = setTimeout(function(){
				check=null;
				_self.endSlide(cb);
			},this.speed*1.5);
			this.$page.off(a$.css.transitionend).on(a$.css.transitionend, function(){
				if(check){
					clearTimeout(check);
					_self.endSlide(cb);
				}
			});
			setTimeout(function(){
				_self.slidePage();
			},2);
		},
		endSlide:function(cb){
			var data={'noop':this.noop,'swipe':this.swipe};
			data.page = (this.noop)? this.pages.page : (this.swipe=='r2l' || this.swipe=='b2t') ? this.pages.next : this.pages.prev;
			if(this.$prev && this.$prev != data.page) this.$prev.remove();
			if(this.$page && this.$page != data.page) this.$page.remove();
			if(this.$next && this.$next != data.page) this.$next.remove();
			this.options.complete(data);
			if(cb) setTimeout(function(){;cb(data);}, 1);
			this.animating = false;
		},
		slidePage:function(){
			var speed = this.speed;
			var back = 'cubic-bezier(0.175, 0.885, 0.32, 1.275)';
			if(a$.browser.androidSDK) back ='cubic-bezier(0,0,1,1)';
			var back_speed= +200;
			
			if(this.swipe=='l2r'){
				if(this.$prev){
					this._translate(this.$prev,0,0,speed);
					this._translate(this.$page,this.width/2,0,speed);
					this._translate(this.$next,this.width/2,0,speed);
				} else {
					this.noop = true;
					speed = this.options.speed-speed;
					this._translate(this.$page,0,0,speed+back_speed,back);
					this._translate(this.$next,this.width/2,0,speed);
				}
			}else if(this.swipe=='r2l'){
				if(this.$next){
					this._translate(this.$prev,-this.width,0,speed);
					this._translate(this.$page,-this.width,0,speed);
					this._translate(this.$next,0,0,speed);
				} else {
					this.noop = true;
					speed = this.options.speed-speed;
					this._translate(this.$prev,-this.width,0,speed);
					this._translate(this.$page,0,0,speed+back_speed,back);
				}
			}else if(this.swipe=='t2b'){
				if(this.$prev){
					this._translate(this.$prev,0,0,speed);
					this._translate(this.$page,0,this.height/2,speed);
					this._translate(this.$next,0,this.height/2,0,speed);
				} else {
					this.noop = true;
					speed = this.options.speed-speed;
					this._translate(this.$page,0,0,speed+back_speed,back);
					this._translate(this.$next,0,this.height/2,speed);
				}
			}else if(this.swipe=='b2t'){
				if(this.$next){
					this._translate(this.$prev,0,-this.height,speed);
					this._translate(this.$page,0,-this.height,speed);
					this._translate(this.$next,0,0,speed);
				} else {
					this.noop = true;
					speed = this.options.speed-speed;
					this._translate(this.$prev,0,-this.height,speed);
					this._translate(this.$page,0,0,speed+back_speed,back);
				}
			}
		},
		makePages:function(pages){
			var _self = this;
			this.pages=pages;
			this.$prev=pages.prev;
			this.$page=pages.page;
			this.$next=pages.next;
			if(this.swipe=='l2r'||this.swipe=='r2l'){
				if(this.$prev) this._translate(this.$prev,-_self.width,0,0);
				this._translate(this.$page,0,0,0);
				if(this.$next) this._translate(this.$next,_self.width/2,0,0);
			}else if(this.swipe=='t2b'||this.swipe=='b2t'){
				if(this.$prev)this._translate(this.$prev,0,-_self.height,0);
				this._translate(this.$page,0,0,0);
				if(this.$next) this._translate(this.$next,0,_self.height/2,0);
			}
			if(this.$prev) this.$prev.appendTo(this.$element);
			if(this.$next)this.$next.insertBefore(this.$page);
			this.$page.off(a$.css.transitionend).on(a$.css.transitionend, function(event){
				if(event.target==_self.$page[0]){
					_self.$page.off(a$.css.transitionend);
					_self.endSlide();
				}
			});
		},
		calcSpeed:function(start,stop) {
			var dx=Math.abs(stop.x-start.x),dy=Math.abs(stop.y-start.y);
			if(this.swipe=='r2l' || this.swipe=='l2r') {
				this.speed = this.options.speed*(1 - dx/this.width);
				this.dot_time = dx/(stop.time-start.time+1);
			}else if(this.swipe=='b2t' || this.swipe=='t2b'){
				this.speed = this.options.speed*(1 - dy/this.height);
				this.dot_time = dy/(stop.time-start.time+1);
			}
			return this.speed;
		},
		reSize:function(){
			this.width=this.$element.outerWidth();
			this.height=this.$element.outerHeight();
			/*
			var $win=$(window);
			this.width=$win.width();
			this.height=$win.height();
			*/
		},
		_transition:function($page,speed,easing) {
			/*
			var quadratic='cubic-bezier(0.25, 0.46, 0.45, 0.94)';
			var circular ='cubic-bezier(0.1, 0.57, 0.1, 1)';
			var circular2= 'cubic-bezier(0.075, 0.82, 0.165, 1)';
			var back ='cubic-bezier(0.175, 0.885, 0.32, 1.275)';
			*/
			speed = speed || 0;
			easing = (easing||this.options.fliptimingfunction); //ease-in-out linear
			if(speed==0)  $page.css(a$.css.prefix('transition'),'');
			else $page.css(a$.css.prefix('transition'),a$.css.prefix('transform')+' ' + speed + 'ms '+easing);
		},
		_translate:function($page, x, y, speed, easing){
			if(!$page) return;
			this._transition($page,speed,easing);
			$page.css(a$.css.prefix('transform'), 'translate3d('+x+'px,'+y+'px,0px)');
		},
		destroy:function(){
			this.$element.aSwipe('destroy');
			this.$element.removeClass('aSlide');
			this.$element.data(PLUGIN_NS,null);
		},
		option:function(key,value){
			this.options[key]=value;
		}
	};
	

});