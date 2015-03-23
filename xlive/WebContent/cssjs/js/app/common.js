define(['iscroll'],function(){
	var _resize=function($page){
		var win=$(window);
		$page.find('.iscroll').each(function(){
			var $one=$(this),top = $one.offset().top;
			$one.css('height',win.height()-top);
			var scroll=$one.data('iscroll');
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
	};
	$(window).on('resize',function(){
		var	$page = $.mobile.pageContainer.pagecontainer('getActivePage');
		if($page && $page.length > 0) _resize($page);
	});
	return {	
		plug:function($page){
			$page.find('.iscroll').each(function(){
				var options= {click:true,probeType:3, scrollbars: true,mouseWheel: true,interactiveScrollbars: true,shrinkScrollbars: 'scale',fadeScrollbars: true};
				var scroller = new IScroll(this,options);
				scroller.refresh();
				$(this).data('iscroll',scroller);
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
			$page.find('[data-href]').on('tap', function(){
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
			
		},
		scrollToHash:function($page,hash){
			setTimeout(function(){
				if(hash){
					var iscroll= $page.find('.iscroll').data('iscroll');
					var target = $page.find('.marker'+hash)[0];
					if(iscroll && target) {
						if(iscroll.y == 0) iscroll.scrollToElement(target,800,0,-120);
						else iscroll.scrollToElement(target,800,0,-72);
					}
				}
			},10);
		},
		resize:function($page){
			_resize($page);
		}
	};
});