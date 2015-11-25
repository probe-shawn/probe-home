define([], function(){
	var $MODAL=$('<div id="system_modal" style="display:none;position:absolute;width:100%;height:100%;background-color:rgb(255,255,255);filter:alpha(opacity=1);opacity:0.01;z-index:30000"></div>').appendTo(document.body);
	var SHARE = {u:'http://probe-home.appspot.com',t:'probe technologies',index:'index.html'};
	var START = {js:'',func:'',param:{}};
	return {
		modal:function(true_false){
			$MODAL[true_false ?'show':'hide']();
			try{$.mobile.loading(true_false ?'show':'hide');}catch(e){}
		},
		initial:function(){
			/*
			var $page=$('div[data-role="page"]');
			this._system_service();
			window.history.pushState({}, "", "");
			$.mobile.pageContainer.on('pagecontainerbeforechange',function(e,ui){
				if(ui.toPage && (typeof ui.toPage==="string") && (ui.toPage.indexOf('index.html') >= 0 || ui.toPage.indexOf('.htm') < 0)) {
					if(ui.prevPage && ui.prevPage[0] == $page[0]){
						e.preventDefault();
						window.history.pushState({}, "", "");
					}
				}
			});
			*/
		},
		_system_service:function(){
			var _self=this;
			$(document.body).on('tap','.ai-btn-delete',function(e){
				var $del=$(this),offset=$del.offset();
				var confirm=$del.attr('data-confirm')||'移除';
				$del.css('visibility', 'hidden');
				var $pop=$('<div style="position:absolute;left:0px;top:0px;width:100%;height:100%;z-index:30000"><span style="position:relative;cursor:pointer;left:-1000px;top:-1000px" class="ai-btn-delete-text">'+confirm+'</sapn></div>').appendTo($(document.body));
				$pop.on('tap',function(e){
					$del.css('visibility', 'visible');
					$pop.remove();
					return false;
				});
				var $span=$pop.find('span');
				$span.on('tap',function(e){
					$del.css('visibility', 'visible');
					$pop.remove();
					$del.trigger('ai-btn-delete');	
					return false;
				});
				$span.css('left', offset.left+($del.width()-$span.width())/2+'px' ).css('top', offset.top+($del.height()-$span.height())/2+'px').addClass('pop in');
				return false;
			});
			$(document.body).on('tap','.ai-photo-service',function(e){
				$photo=$(this);
				if(window.deviceInterface) {
					window.deviceInterface.openFileChooser(JSON.stringify({
						'IDF':IDF(function(jso){
								$photo.addClass('ai-photo-change');
								$photo.css({"background-image":"url("+jso.uri+")"});
								$photo.attr('data-uri',jso.uri)
								.attr('data-size',jso.size||'0')
								.attr('data-type',jso.mime_type)
								.attr('data-name',jso.name);
							})
					})); 
					return;
				}
				var $input=$photo.find('>input');
				if($input.length==0){
					$input=$('<input id="chooser" type="file" data-role="none" style="position:absolute;left:-9999px"></input>').appendTo($photo);
				}
				$input.unbind("change").bind("change",function(e){
					if(this.files.length == 0) return;
					_self.modal(true);	
					var reader = new FileReader();
					reader.onload = function(e) {
					  $photo.css({"background-image":"url("+reader.result+")"});
					  $photo.addClass('ai-photo-change');
					  a$.iconSizePosi($photo,reader.result);
					  _self.modal(false);
					};
					reader.readAsDataURL(this.files[0]);
				}).click();	
			});
			$(document.body).on('tap','.ai-btn-share',function(e){
				var $share=$(this),offset=$share.offset();
				var sid = $share.attr('data-sid');
				var tag = $share.attr('data-tag');
				var product = $share.attr('data-product');
				//
				var href='',t1='',td= '',u = SHARE.u, q = '';
				if(product){
					q +='?sid='+sid+'&tag='+tag+'&prod='+product;
					var store=DATA.Store(sid);
					var prod=DATA.Product(product);
					td += prod.name;
					td += ' \n'+store.name+((store.sub_name != '')?'-'+store.sub_name:'');
					td += ' \n'+prod.desc;
					td += ' \n';
					
					t1 += prod.name;
					t1 += ' \n'+store.name+((store.sub_name != '')?'-'+store.sub_name:'');
					t1 += ' \n';
				}else if(sid){
					q +='?sid='+sid;
					var store=DATA.Store(sid);
					td += store.name+((store.sub_name != '')?'-'+store.sub_name:'');
					td += '\n'+store.store_desc;
					td+= '\n'; 
					t1 += store.name+((store.sub_name != '')?'-'+store.sub_name:'');
					t1 += '\n'; 
				}
				td += SHARE.t;
				if(t1 == '') t1 = SHARE.t+'\n';
				if(a$.browser.androidSDK && window.deviceInterface){
					u += '/'+ SHARE.index+q;
					window.deviceInterface.share((t1+u));
					return false;
				}
				var $pop=$(
					'<div id="share_frame"><div id="share_box" class="ui-body-inherit ui-corner-all ui-overlay-shadow">'+
						'<div id="facebook" class="share-button"></div>'+
						'<div id="line" class="share-button"></div>'+
						'<div id="gplus" class="share-button"></div>'+
						'<div id="twitter" class="share-button"></div>'+
						'<div id="linkin" class="share-button"></div>'+
						//'<div id="email" class="share-button"></div>'+
					'</div></div>'
				).appendTo($(document.body));
				// handle history back 
				var back = function(e,ui){
					if(ui.toPage && (typeof ui.toPage==="string") && (ui.toPage.indexOf('index.html') >= 0 || ui.toPage.indexOf('.htm') < 0)) {
						$pop.trigger('tap');
					}
					return false;
				};
				$.mobile.pageContainer.on('pagecontainerbeforechange',back);
				//
				$pop.on('tap',function(e){
					$.mobile.pageContainer.off('pagecontainerbeforechange',back);
					$pop.remove();
					return false;
				}).find('.share-button').on('tap',function(e){
					var id = $(this).attr('id');
					if(id=='facebook'){
						u += '/'+SHARE.index+q;
						href='https://www.facebook.com/sharer/sharer.php?u='+encodeURIComponent(u)+'&t='+encodeURIComponent(td);
					}else if(id=='line'){
						u += '/index.html'+q;
						href='http://line.me/R/msg/text/?'+ encodeURIComponent(t1)+ encodeURIComponent(u);
					}else if(id=='gplus'){
						u += '/'+SHARE.index+q;
						href ='https://plus.google.com/share?url='+encodeURIComponent(u);
					}else if(id=='twitter'){
						u += '/'+SHARE.index+q;
						href ='https://twitter.com/intent/tweet?url='+encodeURIComponent(u)+'&text='+encodeURIComponent(t1);
					}else if(id=='linkin'){
						u += '/'+SHARE.index+q;
						href = 'http://www.linkedin.com/shareArticle?mini=true&url='+encodeURIComponent(u);
					}else if(id=='email'){
						u += '/'+SHARE.index+q;
						href = 'mailto:?subject='+encodeURIComponent(t1)+'&body='+encodeURIComponent(u+'\n')+encodeURIComponent(td);
					}
					window.open(href,'_blank');
					//////
					$.mobile.pageContainer.off('pagecontainerbeforechange',back);
					$pop.remove();
					return false;
				});
				var $box= $pop.find('#share_box');
				var left = offset.left+($share.width()-$box.width())/2;
				if(left+$box.outerWidth() >= $pop.width()){
					left = $pop.width() - $box.outerWidth() - 8;
				}
				if(left <=0 )left = 8;
				var top =  offset.top+($share.height()-$box.height())/2;
				$box.css('left',left+'px' ).css('top', top+'px').addClass('pop in');
				return false;
			});

		},
		upload:function(photo,bucket,prefix,servingurl){ //result $photo.data('gcs') 
			var $photo = photo;
			bucket = bucket|| aGCS.options.BUCKET;
			prefix = prefix|| 'photos/'+ Math.uuidPhoto()+'_';
			var _self=this,dfd=$.Deferred();
			var $input=$photo.data('gcs',null).find('input').data('gcs',null);
			if(!$input.get(0) || !$input.get(0).files[0]){
				dfd.resolve($photo);
				return dfd;
			}
			var $progress=$("<div class='ai-photo-progress-text'><span>1%</span></div>").appendTo($photo);
			var size=(window.deviceInterface) ? parseInt($photo.attr('data-size')||0) : ($input.get(0).files[0].size||0);
			var beat=Math.round(size/5120);
			var percent=0, refresh=null;
			refresh=setInterval(function(){					
				if(percent < 98) $progress.find('span').html((++percent)+"%");
				else clearInterval(refresh);
			}, beat < 20 ? 20 : beat);			
			aGCS.uploadInput($input,bucket,prefix,!window.location.host.startsWith('localhost')).done(function(jso){
				$progress.remove();
				if(!jso.valid){							
					_self.alert({
						message:'圖片上傳失敗，請稍後再試！\n'+jso.why,
						element:$photo
					});
				} else $photo.data('gcs', $input.data('gcs')).removeClass('ai-photo-change');
				dfd.resolve($photo);
			});
			return dfd;
		},
		listView:function($li,expand,cb){
			$li=$li.closest('.ui-li-collapsible');
			var _self=this,$list=$li.closest('[data-role="listview"]'),exclusive=$list.hasClass('exclusive'),transition=$list.attr('data-transition');
			var collapsed_icon='ui-icon-'+($li.attr('data-collapsed-icon')||'plus'), expanded_icon='ui-icon-'+($li.attr('data-expanded-icon')||'minus');
			var expanded = $li.hasClass('ui-listview-expanded');
			if(expand==null||expand==undefined) expand = !expanded;
			var li = $li.next(':not(.ui-li-collapsible)');
			var ends = new Array();
			while(li.length > 0) {
			    if(transition) this.applyTransition(li,expand,transition);
			    else li[(expand)?'slideDown':'slideUp'](300);
			    ends.push(li);
			    li = li.next(':not(.ui-li-collapsible)');
			}
			$li.toggleClass('ui-listview-expanded',expand);
			$li.find('.'+collapsed_icon+', .'+expanded_icon).removeClass((expand)?collapsed_icon:expanded_icon).addClass((expand)?expanded_icon:collapsed_icon);
		    if(exclusive && expand){
		    	$li.siblings(".ui-li-collapsible.ui-listview-expanded").each(function(){
		    		var $li2=$(this);
					var li = $li2.next(':not(.ui-li-collapsible)');
					while(li.length > 0) {
					    if(transition) _self.applyTransition(li,false,transition);
					    else li.slideUp(300);
					    ends.push(li);
					    li = li.next(':not(.ui-li-collapsible)');
					}
					$li2.toggleClass('ui-listview-expanded',false);
					$li2.find('.'+collapsed_icon+', .'+expanded_icon).removeClass((false)?collapsed_icon:expanded_icon).addClass((false)?expanded_icon:collapsed_icon);
		    	});
		    }
		    if(!transition && cb){
		    	$.when.apply(null,ends).done(function(){cb();});
		    }
		    return expand;
		},
		applyTransition:function(element,in_out,transition_class,cb){
			transition_class = (transition_class)||'pop';
		    if(in_out) {
		    	element.show(0, function () {
		            $(this).addClass(transition_class+' in').one('webkitAnimationEnd oanimationend msAnimationEnd mozAnimationEnd animationend', function (e) {
		                $(this).removeClass(transition_class+' in');
		                if(cb)cb();
		            });
		        });
		    }else{
		    	element.addClass(transition_class+' out').one('webkitAnimationEnd oanimationend msAnimationEnd mozAnimationEnd animationend', function (e) {
		            $(this).hide().removeClass(transition_class+' out');
		            if(cb)cb();
		        });
		    }
		},
		collapsible:function($collapsible,expand,cb){
    		var $content = $collapsible.find(">.ui-collapsible-content");
    		var expanded = !$content.hasClass('ui-collapsible-content-collapsed');
    		var ends = new Array();
    		ends.push($content);
    		if(expand == null||expand == undefined) expand = !expanded;
    		if(expand){
	    		$content.slideDown(500, function() {
	    			$collapsible.collapsible("expand");
	    		});	   
	    		if($collapsible.parent().is(":mobile-collapsibleset, :jqmData(role='collapsible-set')")){
	    			$collapsible.siblings(".ui-collapsible:not(.ui-collapsible-collapsed)").each(function(){
	    				var $one=$(this);
	    				$one.find(">.ui-collapsible-content").slideUp(400, function() {
	    	    			$(this).hide();
	    	    			$one.collapsible("collapse");
	    	    		});	
	    				ends.push($one);
	    			});
	    		}
    		}else{
    			$content.slideUp(400, function() {
	    			$(this).hide();
	    			$collapsible.collapsible("collapse");
	    		});	 
    		}
		    if(cb){
		    	$.when.apply(null,ends).done(function(){cb();});
		    }
    		return expand;
		},
		alert:function(options){
			var opt=$.extend(true,{message:'alert',element:null,cb:null,time:1000},options);
			if(opt.element && opt.element[0]) {
				var iscroll = opt.element.closest('.iscroll');
				if(iscroll.length > 0){
					iscroll.data('iscroll').scrollToElement(opt.element[0],0);
				} else opt.element[0].scrollIntoView();
			}
			var timeout=null;
			var $msg=$('<div style="position:absolute;left:0px;top:0px;width:100%;height:100%;z-index:30000"><div id="pane" class="ui-body-inherit ui-corner-all ui-overlay-shadow" style="position:absolute;left:-3000px;top:-3000px;max-width:480px;padding:8px 12px;background-color:rgba(255,255,255,0.9);font-size:14px;"></div></div>').appendTo($(document.body));
			var $p=$msg.find('#pane').html(opt.message);
			var width=(opt.element)?opt.element.width():$(window).width(),height=(opt.element)?opt.element.height():$(window).height();
			var offset=(opt.element)?opt.element.offset():{left:0,top:0};
			$p.css('left',offset.left+(width-$p.width())/2).css('top',offset.top+(height-$p.height())/2).addClass('pop in');
			var close=function(){
				if(timeout) clearTimeout(timeout); 
				timeout=null;
				$msg.remove();
				if(opt.cb) opt.cb();
			};
			$msg.on('tap',function(){close();});
			if(opt.time>0){
				timeout=setTimeout(function(){
					close();
				}, opt.time);
			}
			return $p;
		},
		inputBlur:function(){
			if(document.activeElement) $(document.activeElement).trigger('blur');
		},
		start:function(){
			this.initial();
			if(window.PointerEvent){
				$(window).css('touch-action','none');
				$(document).css('touch-action','none');
			}
			
			var dfd=$.Deferred();
			if(START.js && START.func){
				require([START.js],function(aSTART){
					var def=aSTART[START.func](START.param);
					if(def && def.done){
						def.done(function(){
							dfd.resolve();
						});
					} else dfd.resolve();
				});
			} else dfd.resolve();
			return dfd.promise(); 
		}
	};
});

