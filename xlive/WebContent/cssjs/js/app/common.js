define(['app/system','iscroll'],function(SYSTEM){
	var is_mobile = a$.browser.ios || a$.browser.android;
	
	
	var _resize=function($page){
		var win=$(window);
		$page.find('.response-size').each(function(){
			var $one=$(this),h=$one.attr('data-h'),w=$one.attr('data-w')||960,max_h=$one.attr('data-max-h');
			var ow=$one.outerWidth();
			if(+h > 0 && +w > +ow){
				h = h*ow/w;
				if(max_h && h>max_h){
					h=max_h;
				}
				$one.css('min-height',h);
			}
		});
		$page.find('.masonry').each(function(){
			$(this).masonry('reload');
		});
		var scroll=null;
		$page.find('.iscroll').each(function(){
			var $one=$(this),top = $one.offset().top;
			$one.css('height',win.height()-top);
			scroll=$one.data('iscroll');
			if(scroll) scroll.refresh();
		});
		var markers = new Array();
		var markersIds='';
		$page.find('.iscroll .marker.change-background').each(function(){
			var $one=$(this);
			markers.push({top:$one.position().top,marker:$one});
			markersIds+=' '+$one.attr('id');
		});
		$page.data('markers', markers);
		$page.data('markersIds',markersIds);
		//
		var emphasize = new Array();
		$page.find('.iscroll .emphasize').each(function(){
			emphasize.push($(this));
		});
		$page.data('emphasize', emphasize);
	};
	$(window).on('resize',function(){
		var	$page = $.mobile.pageContainer.pagecontainer('getActivePage');
		if($page && $page.length > 0) _resize($page);
	});
	return {	
		plug:function($page){
			$page.find('.iscroll').each(function(){
				var options= {tap:true,probeType:3, scrollbars: true,mouseWheel: true,interactiveScrollbars: true,shrinkScrollbars: 'scale',fadeScrollbars: true};
				if(window.PointerEvent) options.disableTouch=true;
				var scroller = new IScroll(this,options);
				scroller.refresh();
				if(window.PointerEvent) $(this).css('touch-action', 'none');
				$(this).data('iscroll',scroller);
			});
			
			$page.find('.masonry').each(function(){
				$(this).masonry();
			});
		},
		on:function($page){
			var _self=this;
			$page.find('.iscroll').each(function(){
				var scroll = $(this).data('iscroll');
				scroll.on('scroll', function(){
					var $p=$(this.scroller).closest('[data-role="page"]');
					if(this.directionY >= 0){
						if(this.y == 0) $p.removeClass('scroll-up');
						else $p.addClass('scroll-up');
					}else{
						if(Math.abs(this.y) < 60) $p.removeClass('scroll-up');
					}
					var nearest_id='',design='',min_dist =300000,markers=$p.data('markers')||[], markersIds=$p.data('markersIds');
					var this_y = this.y;
					$.each(markers, function(i,v){
						var dist=Math.abs(v.top+this_y);
						if(dist < min_dist){
							min_dist = dist;
							nearest_id=v.marker.attr('id');
							design=v.marker.attr('data-design');
						}
					});
					if(nearest_id !=''){
						if(!$p.hasClass(nearest_id)) {
							$p.removeClass(markersIds).addClass(nearest_id);
						}
						if(design && design != ''){
							if(!$p.hasClass(design)) {
								$p.removeClass('black-design white-design').addClass(design);
							}
						}
					}
				});
				
				scroll.on('scrollEnd', function(){
					
					var $p=$(this.scroller).closest('[data-role="page"]');
					if(this.y==0)$p.removeClass('scroll-up');
					var emphasize=$p.data('emphasize')||[], wrap_h=this.wrapperHeight;
					$.each(emphasize,function(i,v) {
						var top = v.offset().top, height=v.outerHeight();bottom=top+height;
						if(top <= wrap_h && bottom >= 0) {
							if((Math.min(bottom, wrap_h)-Math.max(top,0))/height >= 0.5) v.addClass('effect');
						} else {
							if(!is_mobile) v.removeClass('effect');
						}
					});
					
				});
				
				
			});
			$page.find('.ellipsis #bars').on('tap', function(e){
				var $p=$(this).closest('[data-role="page"]');
				$p.find('.page-menu').addClass('expand');
				return false;
			});
			$page.find('.page-menu').on('tap', function(e){
				if($(this).closest('#menu_box').length==0) $(this).removeClass('expand');
			});
			$page.find('[data-href]').on('tap click ', function(){
				var $p=$(this).closest('[data-role="page"]');
				var href = $(this).attr('data-href'), ss = href.split('html');
				var data_url = ss[1] ? href : '';
				$.mobile.pageContainer.pagecontainer('change',href,{transition:'pop',dataUrl:data_url});
				if(window.location.href.indexOf(ss[0]+'html') >= 0 && ss[1]){
					_self.scrollToHash($p,ss[1]);
				} 
				return false;
			});
			$page.find('#to_top').on('tap', function(){
				var $p=$(this).closest('[data-role="page"]');
				var iscroll = $p.find('.iscroll').data('iscroll');
				if(iscroll) {
					iscroll.scrollTo(0, 0, 500);
				}
				return false;
			});
			
			$page.find('.share-menu .share-item').on('tap', function(){
				var href='',u = 'http://probe-home.appspot.com'+window.location.pathname;
				var marker = _self._closestMarker();
				if(marker){
					var marker_id=marker.attr('id');
					if(!marker_id.startsWith('marker_')) u += '#'+marker_id;
				}
				if(a$.browser.androidSDK && window.deviceInterface){
					window.deviceInterface.share(u);
					return false;
				}
				var id = $(this).attr('id');
				if(id=='fb'){
					href='https://www.facebook.com/sharer/sharer.php?u='+encodeURIComponent(u);
				}else if(id=='line'){
					href='http://line.me/R/msg/text/?'+ encodeURIComponent(u);
				}else if(id=='gplus'){
					href ='https://plus.google.com/share?url='+encodeURIComponent(u);
				}else if(id=='twitter'){
					href ='https://twitter.com/intent/tweet?url='+encodeURIComponent(u);
				}else if(id=='linkedin'){
					href = 'http://www.linkedin.com/shareArticle?mini=true&url='+encodeURIComponent(u);
				}
				window.open(href,'_blank');
				//////
				return false;
			});
		},
		_closestMarker:function(){
			var	$page = $.mobile.pageContainer.pagecontainer('getActivePage');
			var iscroll=$page.find('.iscroll').data('iscroll');
			var min_dist =300000,markers=$page.data('markers')||[], marker=null;
			var this_y = iscroll.y;
			$.each(markers, function(i,v){
				var dist=Math.abs(v.top+this_y);
				if(dist < min_dist){
					min_dist = dist;
					marker = v.marker;
				}
			});
			return marker;
		},
		scrollToHash:function($page,hash){
			setTimeout(function(){
				if(hash){
					var iscroll= $page.find('.iscroll').data('iscroll');
					var $markers=$page.find('.marker'+hash);
					var target = $markers.length > 0 ? $markers[0] : null;
					if(iscroll && target) {
						try{
							if(iscroll.y == 0) iscroll.scrollToElement(target,800,0,-120);
							else iscroll.scrollToElement(target,800,0,-72);
						}catch(e){alert('catch :'+e);}
					}
				}
			},100);
		},
		resize:function($page){
			_resize($page);
		}
	};
});