
(function(){
	
	String.prototype.isEmpty=function() {
		return (this=='undefined'||this==''||this.strip()=='');
	};
	String.prototype.strip=function() {
	    return this.replace(/^\s+/, '').replace(/\s+$/, '');
	};
	String.prototype.stripTags=function() {
	    return this.replace(/<\w+(\s+("[^"]*"|'[^']*'|[^>])+)?>|<\/\w+>/gi, '');
	};
	String.prototype.stripScripts=function() {
	    return this.replace(new RegExp('<script[^>]*>([\\S\\s]*?)<\/script>', 'img'), '');
	};
	String.prototype.startsWith=function(pattern) {
	   return this.indexOf(pattern) === 0;
	};
	String.prototype.endsWith=function(pattern) {
	    var d = this.length - pattern.length;
	    return d >= 0 && this.lastIndexOf(pattern) === d;
	};
	Date.prototype.parseDate=function(datestring,pattern) {
		try{
			if(!pattern){
				var r=/(\d{4}|\d{3}|\d{2})\D*(\d{2}|\d{1}|\d{0})\D*(\d{2}|\d{1}|\d{0})\D*(\d{2}|\d*)\D*(\d{2}|\d*)\D*(\d{2}|\d*)\D*(\d{3}|\d*)/;
				datestring.match(r);
				var year=(!RegExp.$1)?1970:(RegExp.$1.length==3)?1911+RegExp.$1:(RegExp.$1.length==2)?1900+RegExp.$1:RegExp.$1;
				this.setFullYear(year,(RegExp.$2||'01')-1,(RegExp.$3||'01'));
				this.setHours((RegExp.$4||'00'),(RegExp.$5||'00'),(RegExp.$6||'00'),(RegExp.$7||'00'));
			}else{
				var yy,MM,dd,HH,mm,ss,i;
				yy=((i=pattern.indexOf('yyyy'))>=0)?parseInt(datestring.substr(i,4),10):null;
				if(!yy)yy=((i=pattern.indexOf('cyy'))>=0)?parseInt(datestring.substr(i,3),10)+1911:null;
				MM=((i=pattern.indexOf('MM'))>=0)?parseInt(datestring.substr(i,2),10):null;
				dd=((i=pattern.indexOf('dd'))>=0)?parseInt(datestring.substr(i,2),10):null;
				HH=((i=pattern.indexOf('HH'))>=0)?parseInt(datestring.substr(i,2),10):null;
				mm=((i=pattern.indexOf('mm'))>=0)?parseInt(datestring.substr(i,2),10):null;
				ss=((i=pattern.indexOf('ss'))>=0)?parseInt(datestring.substr(i,2),10):null;
				if(yy!=null) this.setFullYear(yy);
				if(MM!=null&&MM>0) this.setMonth(MM-1);
				if(dd!=null&&dd>0) this.setDate(dd);
				if(HH!=null) this.setHours(HH);
				if(mm!=null) this.setMinutes(mm);
				if(ss!=null) this.setSeconds(ss);
				this.pattern=pattern;
				this.validate=
				((yy!=null)?(this.getFullYear()==yy):true)&&
				((MM!=null&&MM>0)?(this.getMonth()+1==MM):true)&&
				((dd!=null&&dd>0)?(this.getDate()==dd):true)&&
				((HH!=null)?(this.getHours()==HH):true)&&
				((mm!=null)?(this.getMinutes()==mm):true)&&
				((ss!=null)?(this.getSeconds()==ss):true);
			}
		}catch(e){
			alert('parseDate error :'+e);	
		}
		return this;
	};
	Date.prototype.formatDate=function(pattern){
		var str=(pattern||'yyyyMMddHHmmssS');
		str=str.replace(/yyyy/g,this.getFullYear());
		var tmp='000'+(this.getFullYear()-1911);
		str=str.replace(/cyy/g,tmp.substring(tmp.length-3));
		tmp='00'+(this.getMonth()+1);
		str=str.replace(/MM/g,tmp.substring(tmp.length-2));
		tmp='00'+this.getDate();
		str=str.replace(/dd/g,tmp.substring(tmp.length-2));
		tmp='00'+this.getHours();
		str=str.replace(/HH/g,tmp.substring(tmp.length-2));
		tmp='00'+this.getMinutes();
		str=str.replace(/mm/g,tmp.substring(tmp.length-2));
		tmp='00'+this.getSeconds();
		str=str.replace(/ss/g,tmp.substring(tmp.length-2));
		tmp='000'+this.getMilliseconds();
		str=str.replace(/S/g,tmp.substring(tmp.length-3));
		return str;
	};
	Date.prototype.isLeap=function(year) {
		return ((year%4==0)&&(y%100!=0))||(y%400==0);
	};
	Date.prototype.setLastDate=function() {
		var d=new Date(this.getFullYear(),this.getMonth()+1,1);
		this.setDate(new Date(d.getTime()-8600000).getDate());
		return this;
	};
	Date.prototype.nextDate=function(dayoffset){
		this.setTime(this.getTime()+(dayoffset*86400000));
		return this;
	};
	Date.prototype.cloneDate=function() {
		return new Date(this.getTime());
	};
	Date.prototype.toUTC=function(){
		return new Date(this.getTime()+(this.getTimezoneOffset()*60*1000));
	};
	Date.prototype.toLocal=function(){
		return new Date(this.getTime()-(this.getTimezoneOffset()*60*1000));
	};
	Function.prototype.bind=function(context){
		if (arguments.length < 2 && typeof arguments[0] === "undefined") return this;
		var __method = this, args = Array.prototype.slice.call(arguments,2);
		return function() {
			return __method.apply( context, args.concat( Array.prototype.slice.call( arguments ) ) );
		};
	};
	$.browser.ie10=($.browser.msie && document.documentMode==10);
	$.browser.ie9=(!$.browser.ie10 && $.browser.msie && document.documentMode==9);
	$.browser.ie8=(!$.browser.ie9 && $.browser.msie && window.postMessage);
	$.browser.ie7=($.browser.msie && navigator.userAgent.indexOf('MSIE 7.') > -1);
	$.browser.mobilesafari=/Apple.*Mobile/.test(navigator.userAgent);
	$.browser.safari=navigator.userAgent.indexOf('Safari') > -1 && navigator.userAgent.indexOf('Chrome') < 0;

	a$={
			css:{
				vp:($.browser.opera)?'-o-':($.browser.webkit)?'-webkit-':($.browser.mozilla)?'-moz-':'-ms-',
				transitionend:($.browser.opera)?'oTransitionEnd':($.browser.webkit)?'webkitTransitionEnd':($.browser.mozilla)?'transitionend':($.browser.msie)?'MSTransitionEnd':'transitionEnd',
				transition:function(object,css,cb,ms){
					ms=(ms||300);
					if($.isFunction(cb)) object.animate(css,ms,cb);
					else object.animate(css,ms);			
				},
				cssvp:function(e,prop,css){
					e.style[a$.css.vp+prop]=css;
				}
			},
			load:function($e,url,always){
				var dfd=$.Deferred();
				if(!(always===true) && $e.data('a$_loaded')) dfd.resolve($e);
				else $e.load(url,function(){dfd.resolve($e);}).data('a$_loaded',true);
				return dfd.promise();
			},
			supportTransition:function(){
				var s=document.body.style;
				return (s.transition !== undefined ||s.WebkitTransition !== undefined ||s.MozTransition !== undefined ||s.OTTransition !== undefined || s.msTransition!== undefined);
			},
			supportTransform:function(){
				var s=document.body.style;
				return (s.transform !== undefined ||s.WebkitTransform !== undefined ||s.MozTransform !== undefined ||s.OTransform !== undefined||s.msTransform !== undefined);
			},
			feature:function(f){
				return (document.body.style[a$.css.vp+f] !== undefined);
			},
			doTransition:function($e,class_name,p,cb){
				setTimeout(function(){
					$e.bind(a$.css.transitionend,function(e){
						e.stopPropagation();
						$(this).unbind(a$.css.transitionend).removeClass(class_name);
						if(cb)cb();
					}).stop().addClass(class_name).css(p);
				},10);
			},
			setTransition:function($e,class_name,p,cb){
				setTimeout(function(){
					$e.bind(a$.css.transitionend,function(e){
						e.stopPropagation();
						$(this).unbind(a$.css.transitionend);
						if(cb)cb();
					}).stop().addClass(class_name).css(p);
				},10);
			},
			isMedia:function(query){
	            var div = document.createElement("div");
	            div.id = "ncz1";
	            div.style.cssText = "position:absolute;top:-1000px";
	            document.body.insertBefore(div, document.body.firstChild);            
		        div.innerHTML = "_<style media=\"" + query + "\"> #ncz1 { width: 1px; }</style>";
		        div.removeChild(div.firstChild);
		        return div.offsetWidth == 1;    
		    },
		    setStyle:function(style){
	            var sty= document.createElement("style");
	            sty.setAttribute("type", "text/css");
	            sty.innerHTML=style;
		        document.getElementsByTagName("head")[0].appendChild(sty);
		    },
		    pageSize:function(){
				var w=$(window).width(), $b=$('body'),
				size = (w <= 480) ? 'ui-size-s' : (w < 960) ? 'ui-size-m' : 'ui-size-l';
				if(!$b.hasClass(size)) $b.removeClass('ui-size-s').removeClass('ui-size-m').removeClass('ui-size-l').addClass(size);
			}
		};
	
var _resize=null;
$(window).bind('resize',function(){
	if(_resize) clearTimeout(_resize);
	_resize = setTimeout(function(){a$.pageSize();},200);
});
var mason_style='.size_s .mason3 .m1c,.size_s .mason3 .m2c,.size_s .mason3 .m3c{width:100%;}'+
'.size_m .mason3 .m1c{width:50%;}'+
'.size_m .mason3 .m2c,.size_m .mason3 .m3c{width:100%;}'+
'.size_l .mason3 .m1c{width:33.33%;}'+
'.size_l .mason3 .m2c{width:66.66%;}'+
'.size_l .mason3 .m3c{width:100%;}'+
'.size_s .mason2 .m1c,.size_s .mason2 .m2c{width:100%;}'+
'.size_m .mason2 .m1c,.size_l .mason2 .m1c{width:50%;}'+
'.size_m .mason2 .m2c,.size_l .mason2 .m2c{width:100%;}';
a$.setStyle(mason_style);

$.Mason.prototype._init = function( callback ) {
    var mason32=this.element.hasClass('ui-mason-3')?3:this.element.hasClass('ui-mason-2')?2:0;
    if(!this.options.columnWidth && mason32 > 0){
    	this.options.columnWidth=function(containerWidth){
			a$.pageSize();
			var $b= $('body'),cs=1;
			if(mason32==3) cs=($b.hasClass('ui-size-s')?1:$b.hasClass('ui-size-m')?2:3);
			if(mason32==2) cs=($b.hasClass('ui-size-s')?1:$b.hasClass('ui-size-m')?2:2);
			return containerWidth / cs;
    	};
    	this.isFluid=true;
    	this._aliveStyle=true; 
    }
    this._getColumns();
    this._reLayout(callback);
};
$.Mason.prototype._placeBrick=function( brick ) {
    var $brick = $(brick),colSpan, groupCount, groupY, groupColY, j;
    if(this._aliveStyle) colSpan = Math.ceil( ($brick.outerWidth(true)-1) / this.columnWidth );
    else colSpan = Math.ceil( $brick.outerWidth(true) / this.columnWidth );
    colSpan = Math.min( colSpan, this.cols );
    if( colSpan === 1 ) {groupY = this.colYs;
    } else {
      groupCount = this.cols + 1 - colSpan;
      groupY = [];
      for ( j=0; j < groupCount; j++ ) {
        groupColY = this.colYs.slice( j, j+colSpan );
        groupY[j] = Math.max.apply( Math, groupColY );
      }
    }
    var minimumY = Math.min.apply( Math, groupY ),shortCol = 0;
    for (var i=0, len = groupY.length; i < len; i++) {
      if ( groupY[i] === minimumY ) {
        shortCol = i;
        break;
      }
    }
    var position = {top: minimumY + this.offset.y};
    position[ this.horizontalDirection ] = this.columnWidth * shortCol + this.offset.x;
    this.styleQueue.push({ $el: $brick, style: position });
    var setHeight = minimumY + $brick.outerHeight(true),setSpan = this.cols + 1 - len;
    for ( i=0; i < setSpan; i++ ) {
      this.colYs[ shortCol + i ] = setHeight;
    }
  };






var _xml={
		__type$:'xml$'
		,
		init:function(root){
			if(root && root.__type$ && root.__type$==this.__type$) this.root=root.root;
			else this.root=(typeof root == 'string')?this.parse((root=='')?'<root></root>':root).documentElement:(root)?root:this.parse('<root></root>').documentElement;
			return this;
		},
		nodes:function(xpath,cb){
			var r = [];
			if(this.root.ownerDocument.evaluate){
				var q = this.root.ownerDocument.evaluate(xpath,this.root,null,XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
			    for(var i=0,n = q.snapshotLength; i < n; i++)r.push(q.snapshotItem(i));
			}else r = this.root.selectNodes(xpath);
			if(typeof cb !== 'function') return r;
			cb(r);
			return this;
		},
		node$:function(xpath){
			return xml$(this.node(xpath));
		},
		node:function(xpath){
			if(arguments.length==0||!xpath)return this.root;
			if(this.root.ownerDocument.evaluate){
				var q=this.root.ownerDocument.evaluate(xpath,this.root,null,XPathResult.FIRST_ORDERED_NODE_TYPE, null);
				return (q)? q.singleNodeValue : null;
			}
			return this.root.selectSingleNode(xpath);
		},
		text:function(xpath,text){
			var n=(xpath=='.'||xpath==undefined)?this.root:this.node(xpath);
			if(arguments.length==1||arguments.length==0) return (n)?(($.browser.msie)?n.text:n.textContent) :'';
			text=(typeof text =='undefined')?'':text;
			if(n)n[$.browser.msie?'text':'textContent']=text;
			else{
				var xps=xpath.split('/'),xp=xps.pop(),cs=null,c=null,attr=(xp && xp[0]=='@')?xp.substring(1):null;
				if(attr){
					if(n=this.node(xps.join('/'))){
						n.setAttribute(attr,text);
						return this;
					}
					xp=xps.pop();
				}
				while(xp){
					c=this.root.ownerDocument.createElement(xp);
					if(!cs)	{
						if(attr) c.setAttribute(attr,text);
						else c[$.browser.msie?'text':'textContent']=text;
					}else c.appendChild(cs);
					cs=c;
					if(xps.length>0 && (n=this.node(xps.join('/')))){
						n.appendChild(cs); break;
					}
					xp=xps.pop();
				}
				if(!n && cs)this.root.appendChild(cs);
			}
			return this;
		},
		toString:function(){
			return ($.browser.msie)?this.root.xml:new XMLSerializer().serializeToString(this.root);
		},
		adopt:function(node){
			return ($.browser.msie)? node:this.root.ownerDocument.adoptNode(node);
		},
		parse:function(str){
			if($.browser.msie){
				var dom=new ActiveXObject('Msxml2.DOMDocument');
				dom.async=false;
				dom.loadXML(str);
				return dom;
			}
			return (new DOMParser()).parseFromString(str, "text/xml");
		}
	};
function xml$(node){
	function F() {}
	F.prototype=_xml;
	return new F().init(node);
};

var _dm ={
		init:function(name,options){
			this.options=(this.options||{_name:name});
			if(options){
				$.extend(this.options,options);
				if(this.options.url && this.options.method && this.options.arg && !this.options['_'+options.method])this.request(this.options);
			}
			return this;
		},
		request:function(options){
			if(options)$.extend(this.options,options);
			var os=this.options,url=os.url,arg=os.arg,method=os.method,result='_'+method;
			this.options[result] = null;
			url=url.startsWith('/xlive')?url:'/xlive/web/'+url;
			var setting={'url':url,type:'POST',contentType:'multipart/form-data',headers:{'X-XLive-Version':'alive.1.0','X-XLive-Content':'xml','xsessionid':'8788'}};
			var $arg=xml$(arg),am=$arg.node(method);
			var x=xml$('<xlive></xlive>').text('method','').text('method/@name',method).text('method/arguments','').text('method/arguments/'+method,'');
			var m=x.node('method/arguments/'+method);
			var cs=((am)?am:$arg.node()).childNodes;
			for(var i=0;i<cs.length;++i)m.appendChild(cs[i].cloneNode(true));
			setting.data=x.toString();
			if(aChannel)setting.data=setting.data.replace('</'+method+'>','<client-id>'+aChannel.getClientId()+'</client-id>'+'</'+method+'>');
			var dfd=$.Deferred();
			setting.success=function(data,textStatus,jqXHR){
				if($.browser.msie)data.setProperty('SelectionLanguage','XPath');
				var r=xml$($('xlive',data)[0]);
				if(r.node('exception')) alert('exception :'+r.toString());
				var ret=(result=='_get-properties')?$('xlive properties',data)[0]:$('xlive return',data)[0];
				this.options[result]=ret;
				if(this.options.callback)this.options.callback(ret);
				dfd.resolve(ret);
				if(this.options[result+'_DFD']){
					for(var d=0;d<this.options[result+'_DFD'].length;++d){
						this.options[result+'_DFD'][d].notify(ret);
					}
				}
				if(this.options[result+'_dfd']){
					for(var d=0;d<this.options[result+'_dfd'].length;++d){
						this.options[result+'_dfd'][d].resolve(ret);
					}
				}
				this.options[result+'_dfd']=null;
			}.bind(this);
			$.ajax(setting);
			return dfd.promise();
		},
		prop:function(){
			var dfd=$.Deferred(),ret=this.options['_get-properties'];
			if(ret)dfd.resolve(ret);
			else (this.options['_get-properties_dfd']=(this.options['_get-properties_dfd']||[])).push(dfd);
			return dfd.promise();
		},
		result:function(method,always){
			method=(method||this.options.method);
			var dfd=$.Deferred(),ret=this.options['_'+method],as=(always)?'_DFD':'_dfd';
			if(ret)((always)? dfd.notify(ret):dfd.resolve(ret));
			if(!ret||always)(this.options['_'+method+as]=(this.options['_'+method+as]||[])).push(dfd);
			return dfd.promise();
		},
		notify:function(method){
			var result='_'+(method||this.options.method),ret=this.options[result];
			if(ret){
				if(this.options[result+'_DFD']){
					for(var d=0;d<this.options[result+'_DFD'].length;++d){
						this.options[result+'_DFD'][d].notify(ret);
					}
				}
				if(this.options[result+'_dfd']){
					for(var d=0;d<this.options[result+'_dfd'].length;++d){
						this.options[result+'_dfd'][d].resolve(ret);
					}
				}
				this.options[result+'_dfd']=null;
			}
		}
};

function dm$(name,options){
	function F(){}
	F.prototype = _dm;
	this._dms=(this._dms||{});
	if(name && typeof name == 'string') {
		if(!this._dms[name]) this._dms[name]=new F().init(name,options);
		return this._dms[name];
	}
	return new F().init(name,options);
};



var aGoogle={
	options:{
		key:'',
		apiLoaded:[]
	},
	__gApi:function(){
		var dfd=$.Deferred();
		if(this.__loaded)dfd.resolve();
		else $.getScript('https://www.google.com/jsapi?key='+this.options.key,function(){aGoogle.__loaded=true;dfd.resolve();});
		return dfd.promise();
	},
	_apiLoaded:function(api,version){
		var m=this.options.apiLoaded;
		for(var i=0;i<m.length;++i)	if(api==m.api && m.version==version) return true;
		return false;
	},
	initial:function(){
		var dfd=$.Deferred();
		this.__gApi().done(function(){dfd.resolve();});
		return dfd.promise();
	},
	api:function(api,version,options){
		var dfd=$.Deferred();
		if(this._apiLoaded(api,version))dfd.resolve();
		else{
			this.initial().done(function(){
				options=$.extend({},options);
				options.callback=function(){this.options.apiLoaded.push({'api':api,'version':version});dfd.resolve();}.bind(this);
				google.load(api,version,options);
			}.bind(this));
		}
		return dfd.promise();
	}
};

var aFB={
	options:{
		appId:'181836915197998',
		fbConnetion:'//connect.facebook.net/zh_TW/all.js',
		perms:'publish_stream'
	},
	initial:function(options){
		this.options=$.extend({},this.options,options);
		var dfd=$.Deferred();
		var r= document.createElement('div');
		r.setAttribute('id','fb-root');
		document.body.appendChild(r);
		window.fbAsyncInit = function(){
			try{
		    FB.init({appId:this.options.appId, status: true, cookie: true,xfbml: true});
			}catch(e){alert('facebook SDK failure :'+e);}
		    dfd.resolve(this);
		}.bind(this);
		var e = document.createElement('script'); e.async = true;
	    e.src = document.location.protocol +this.options.fbConnetion;
	    r.appendChild(e);
	    return dfd.promise();
	},
	fbCanvas:function(){
		return this._canvas;
	},
	getPageInfo:function(){
		var dfd=$.Deferred();
		if(FB && this._canvas)FB.Canvas.getPageInfo(function(info){dfd.resolve(info);});
		else dfd.resolve({scrollTop:0,scrollLeft:0});
		return dfd.promise();
	},
	setStatusResp:function(resp){
  		this._authResponse=(resp||{});
  		this._permission={};
  		if(resp && resp.authResponse)FB.api('/me/permissions',function(perm){this._permission=perm;}.bind(this));
	},
	checkMallPerms:function(){
   		var auth=this._authResponse.authResponse;
   		var ok=(auth && auth.accessToken)?true:false;
   		var perm=this._permission;
   		if(!perm||!perm.data||!perm.data[0])return false;
   		var data=perm.data[0];
   		return  ok && ((data.user_photos==1 && data.user_videos==1 && data.manage_pages==1)?true:false);
	 },
	 getMallPerms:function(){
		 return 'user_photos,user_videos,manage_pages';
	 }
};
var aChannel={
		sendMessageText:'<send-message><sender><id></id></sender><receiver><id></id><fid></fid><mall-fid></mall-fid><barter-fid></barter-fid></receiver><message></message><self>true</self></send-message>',
		setup:function(){
			var dfd=$.Deferred();
			var script= document.createElement('script');
			script.type= 'text/javascript';
			script.onload = function(){dfd.resolve();};
			script.src= '/_ah/channel/jsapi';
			document.body.appendChild(script);
			return dfd.promise();
		},
		openChannel:function(param){
			this.param=param;
			if(this.token && this.channel && this.socket) return this.closeOpenChannel();
			try{
				var n=xml$('<get-token></get-token>').text('fid',param.fid).text('name',param.name).text('mall-fid',param.mallFid)
				.text('mall-name',param.mallName).text('barter-fid',param.barterFid).text('barter-name',param.barterName).node();
				dm$().request({url:'channel',method:'get-token',arg:n})
				.done(function(data){
					this.token=xml$(data).text('token');
					this.id=xml$(data).text('id');
	  				this._openChannel();
	  			}.bind(this));
			}catch(e){}
		},
		_openChannel:function(){
			try{
				this.channel = new goog.appengine.Channel(this.token);
				var on={onopen:function(){this._opened();}.bind(this),onmessage:function(msg){this._message(msg);}.bind(this),
						onerror:function(msg){this._error(msg);}.bind(this),onclose:function(){this._close();}.bind(this)};
				this.socket = this.channel.open(on);
			}catch(e){
				setTimeout((function(){this._openChannel();}).bind(this),100);
			}
		},
		closeOpenChannel:function(){
			if(this.socket)this.socket.close();
			this.token=this.channel=this.socket=null;
			setTimeout((function(){this.openChannel(this.param);}).bind(this),100);
		},
		closeChannel:function(){
			if(this.socket)this.socket.close();
			this.token=this.channel=this.socket=null;
		},
		_opened:function(){
			if(this.updated){
				this.updated=false;
				this.updateChannel(this.param);
			}
		},
		_message:function(msg){
			return this.message(msg.data,(msg.data)? xml$(msg.data).node():null);
		},
		message:function(msg,xml){
			return true;
		},
		_error:function(msg){
			this.token=this.channel=this.socket=null;
		},
		_close:function(){
			dm$().request({url:'channel',method:'closed',arg:xml$('<closed></closed>').text(id,this.id).node()});
			if(!this.dead)this.openChannel(this.param);
		},
		sendMessage:function(msg_param){
			var n=xml$(this.sendMessageText).text('sender/id',this.id)
			.text('receiver/id',msg_param.id)
			.text('receiver/fid',msg_param.fid)
			.text('receiver/mall-fid',msg_param.mallFid)
			.text('receiver/barter-fid',msg_param.barterFid)
			.text('message',msg_param.message)
			.text('self',msg_param.self).node();
			dm$().request({url:'channel',method:'send-message',arg:n});
		},
		updateChannel:function(param){
			param=$.extend(this.param,param);
			if(this.socket){
				var n=xml$('<update></update>').text('id',this.id).text('fid',param.fid).text('name',param.name)
				.text('mall-fid',param.mallFid).text('mall-name',param.mallName).text('barter-fid',param.barterFid).text('barter-name',param.barterName).node();
				dm$().request({url:'channel',method:'update',arg:n}).done(function(){});
			}else this.updated=true;
		},
		getClientId:function(){
			return this.id;
		}
};

var aTab ={
		options:{
			defaultIndex:0,
			tabs:['empty'], //array
			callback:null
		},
		init:function(e,options) {
			this.options=$.extend({},this.options,options);
		    this.$element=$(e);
		    this.$element.addClass('aTab');
		    this.build();
		    return this;
		},
		build:function(){
			var tabs='<li><a href="#">'+this.options.tabs.join('</a></li><li><a href="#">')+'</a></li><li class="shadow"></li>';
			$('<ul id="nav">'+tabs+'</ul>').appendTo(this.$element).find('li').eq(this.options.defaultIndex).addClass('selected');
			this.$element.click(function(e) {
				this.$element.find('ul#nav li').removeClass('selected');
				var sel=$(e.target).closest('li').addClass('selected').prevAll('li').length;
				if(this.options.callback)this.options.callback(sel);
				return false;
			}.bind(this));
		},
		selectedIndex:function(sel){
			if(arguments.length==0)	return this.$element.find('li.selected').prevAll('li').length;
			this.$element.find('ul#nav li').removeClass('selected').eq(sel).addClass('selected');
			if(this.options.callback)this.options.callback(sel);
		}
};
var aTab2 ={
		options:{
			defaultIndex:0,
			tabs:['empty'], //array
			callback:null
		},
		init:function(e,options) {
			this.options=$.extend({},this.options,options);
		    this.$element=$(e);
		    this.$element.addClass('aTab2');
		    this.build();
		    return this;
		},
		build:function(){
			var tabs='<li><a href="#">'+this.options.tabs.join('</a></li><li><a href="#">')+'</a></li>';
			$('<ul id="nav" class="tabrow">'+tabs+'</ul>').appendTo(this.$element).find('li').eq(this.options.defaultIndex).addClass('selected');
			this.$element.click(function(e) {
				var $li=$(e.target).closest('li');
				if($li.length==0)return;
				this.$element.find('ul#nav li').removeClass('selected');
				var sel=$li.addClass('selected').prevAll('li').length;
				if(this.options.callback)this.options.callback(sel);
				return false;
			}.bind(this));
		},
		selectedIndex:function(sel){
			if(arguments.length==0)	return this.$element.find('li.selected').prevAll('li').length;
			this.$element.find('ul#nav li').removeClass('selected').eq(sel).addClass('selected').trigger('click');
			if(this.options.callback)this.options.callback(sel);
		}
};

var aCalendar ={
		options:{
			weekNames:['sun','mon','tue','wen','thr','fri','sat'],
			monthNames:['January','February','March','April','May','June','July','August','September','October','November','December'],
			dateString:null,
			callback:null
		},
		init:function(e,options) {
			this.options=$.extend({},this.options,options);
		    this.$element=$(e);
		    this.$element.addClass('aCalendar');
		    this.build();
		    return this;
		},
		build:function(){
			var cal=$('<div class="class_calendar"><table id="xlvid_table"><tbody><tr id="xlvid_month_row"></tr><tr id="xlvid_week_row"></tr></tbody></table></div>').appendTo(this.$element);
			this.$element.find('.class_calendar').css({
				'line-height':'22px',border:'1px solid gray',padding:'4px',margin:'5px','border-radius':'8px',
				'-moz-border-radius':'8px','-webkit-border-radius':'8px',background:'url("/xlive/images/grayTexture.gif") repeat',
				'-moz-box-shadow':'2px 2px 5px rgba(0,0,0,0.8)','-webkit-box-shadow':'2px 2px 5px rgba(0,0,0,0.8)','-o-box-shadow':'2px 2px 5px rgba(0,0,0,0.8)',
				'box-shadow':'2px 2px 5px rgba(0,0,0,0.8)'})
				.mousedown(function(e){this._mousedown(e);}.bind(this));
			var body=cal.find('tbody'),mrow=cal.find('#xlvid_month_row'),wrow=cal.find('#xlvid_week_row');
			$('<td id="xlvid_prev_month"></td><td id="xlvid_month" colSpan="5"></td><td id="xlvid_next_month"></td>').appendTo(mrow);
			var row=null;
			this.days=[];
			for(var i=0;i<7;++i){
				$('<td id="xlvid_week"></td').html(this.options.weekNames[i]).addClass((i==0)?'sun':(i==6)?'sat':'').appendTo(wrow);
			}
			for(var i=0;i<6;++i){
				row=$('<tr></tr>');
				for(var d=0;d<7;++d){
					$(this.days[this.days.length]=document.createElement('td')).prop({id:'xlvid_day'}).addClass((d==0)?'sun':(d==6)?'sat':'').appendTo(row);
					if(this.days.length==38)break;
				}
				row.appendTo(body);
			}
			$('<td id="xlvid_prev_year"></td><td id="xlvid_next_year"></td><td id="xlvid_year" colSpan="2"></td>').appendTo(row);
			this.current=new Date();
			if(this.options.dateString) this.current.parseDate(this.options.dateString);
			this.setDate(this.current);
		},
		_mousedown:function(e){
			var id=e.target.id,v=this.current;
			if(id=='xlvid_prev_month'){v.setMonth(v.getMonth()-1);return this.setDate(v);}
			if(id=='xlvid_next_month'){v.setMonth(v.getMonth()+1);return this.setDate(v);}
			if(id=='xlvid_prev_year'){v.setFullYear(v.getFullYear()-1);return this.setDate(v);}
			if(id=='xlvid_next_year'){v.setFullYear(v.getFullYear()+1);return this.setDate(v);}
			if(id=='xlvid_day'){
				this.past=new Date(v.getTime());
				var day=(e.target.innerHTML);
				v.setDate(day);
				if(this.options.callback) return this.options.callback(v.cloneDate());
			}
		},
		getDate:function(){
			return this.current.cloneDate();
		},
		setDate:function(date){
			this.past=this.current;
			this.current=date;
			var save=this.current.getDate();
			this.current.setDate(1);
	        var first=this.current.getDay();
	        var last=new Date(this.current.getFullYear(),this.current.getMonth()+1,1,0,0,0,0);
	        last=new Date(last.getTime()-3600*1000).getDate();
	        var now=new Date();
	        if(this.today){
	        	Element.removeClassName(this.today,'today');
	        	this.today=null;
	        }
	        for(var i=0;i<this.days.length;++i) {
	        	if((i-first+1)==now.getDate()&& this.current.getMonth()==now.getMonth()&&this.current.getFullYear()==now.getFullYear()){
	        		$(this.days[i]).addClass('today');
	        		this.today=this.days[i];
	        	}
	        	if((i<first||((i-first+1)>last))){
	        		$(this.days[i]).html('');
	        		this.days[i].style.visibility='hidden';
	        	}else{
	        		$(this.days[i]).html(i-first+1);
	        		this.days[i].style.visibility='visible';
	        	}
	        }
	        this.$element.find('#xlvid_month').html(this.options.monthNames[this.current.getMonth()]);
	        this.$element.find('#xlvid_year').html(this.current.getFullYear());
	        this.current.setDate(save);
		}
};

var aImg ={
	options:{
		width:null,
		height:null,
		src:null,
		cover:false,
		coverCenter:false,
		demo:false,
		demoDuration:1000,
		demoType:'fit',
		imgCss:null,
		callback:null
	},
	init:function(e,options) {
		this.options=$.extend({},this.options,options);
	    this.$element=$(e).addClass('aImg');
	    if(this.options.width)this.$element.css('width',this.options.width);
	    if(this.options.height)this.$element.css('height',this.options.height);
	    if(this.options.cover)this.$element.css('overflow','hidden');
	    this.imgSrc(this.options.src);
	    return this;
	},
	imgSrc:function(src){
		this.$element.find('img#aimg_img').remove();
		if(!src) return;
		var _image=new Image();
		_image.onload=function(){this._load(_image);}.bind(this);
		_image.src=src;
	},
	_load:function(_image){
		var $e=this.$element,disp=$e.css('display'),visi=$e.css('visibility');
		$e.css({visibility:'hidden',display:'block'});
		var cw=(this.options.width||this.$element.width()),ch=(this.options.height||this.$element.height());
		$e.css({visibility:visi,display:disp});
		var $img=this.$element.find('img#aimg_img');
		if($img.length>0 && $img[0].src==_image.src){
			$img=$($img[0]);
			if(this.options.demo && this.options.coverCenter) $img.unbind('hover');
		}else $img=$('<img id="aimg_img"></img>').appendTo($e);
		var w=cw,h=ch;
		var iw=_image.width,ih=_image.height,mw=0,mh=0,sw=0,sh=0;
		var width_first=(this.options.cover)?(iw/w < ih/h):(iw/w > ih/h);
		if(width_first)	{
			sw=Math.min(w,iw);
			$img.attr('width',sw+'px');
			mw=parseInt((w-sw)/2,10);
			sh=parseInt(ih*(sw/iw),10);
			mh=parseInt((h-sh)/2,10);
		}else {
			sh=Math.min(h,ih);
			$img.attr('height',sh+'px');
			mh=parseInt((h-sh)/2,10);
			sw=parseInt(iw*(sh/ih),10);
			mw=parseInt((w-sw)/2,10);
		}
		if(this.options.cover &&this.options.coverCenter)$img.css({marginTop:mh+'px',marginBottom:mh+'px'});
		else if(mh>0) $img.css({marginTop:mh+'px',marginBottom:mh+'px'});
		if(this.options.cover &&this.options.coverCenter)$img.css({marginLeft:mw+'px',marginRight:mw+'px'});
		else if(mw>0) $img.css({marginLeft:mw+'px',marginRight:mw+'px'});
		$img.attr({src:_image.src});
		this._marginTop=mh;
		this._marginLeft=mw;
		this._width=sw;
		this._height=sh;
		this.$element.find('img#aimg_img').not($img).remove();
		$img.unbind('hover');
		if(this.options.demo && this.options.coverCenter && ((width_first && mh < -10) || (!width_first && mw < -10)) ){
			var d=this.options.demoDuration;type=this.options.demoType;
			$img.hover(function(){
				type=(new Date().getTime()%2==0)?'swing':'fit';
				if(width_first) {
					if(type=='swing'){
						$img.animate({marginTop:mh*2},d, function(){
							$img.animate({marginTop:0},2*d,function(){
								$img.animate({marginTop:mh},d);
							});
						});
					}
					if(type=='fit'){
						var aw=sw*((sh+2*mh)/sh),aleft=(sw-aw)/2;
						$img.animate({marginTop:0,marginLeft:aleft,height:sh+2*mh,width:aw},d,function(){
						});
					}
				}else{
					if(type=='swing'){
						$img.animate({marginLeft:mw*2},d, function(){
							$img.animate({marginLeft:0},2*d,function(){
								$img.animate({marginLeft:mw},d);
							});
						});
					}
					if(type=='fit'){
						var ah=sh*((sw+2*mw)/sw),atop=(sh-ah)/2;
						$img.animate({marginLeft:0,marginTop:atop,width:sw+2*mw,height:ah},d,function(){
						});
					}
				}
			},function(){
				$img.stop();
				if(type=='swing'){
					if(width_first)	$img.css({marginTop:mh});
					else $img.css({marginLeft:mw});
				}
				if(type=='fit'){
					if(width_first)	$img.css({marginTop:mh,marginLeft:0,height:sh,width:sw,marginBottom:mh});
					else $img.css({marginLeft:mw,marginTop:0,width:sw,height:sh,marginRight:mw});
				}
			});
		}
		if(this.options.callback) return this.options.callback($img);
	},
	resetImg:function(options){
		this.options=$.extend(this.options,options);
	    if(this.options.width)this.$element.css('width',this.options.width);
	    if(this.options.height)this.$element.css('height',this.options.height);
		var _image=new Image();
		_image.onload=function(){this._load(_image);}.bind(this);
		_image.src=options.src;
	}
};

var aScroller = {
	options:{
		step:32,
		hScroll:false,
		useTransform:false,
		useTransition:true,
		fadeScrollbar:false
	},
	init:function(e,options) {
		this.options=$.extend({},this.options,options);
	    this.$element=$(e);
	    this.$element.addClass('aScroller');
	    this.build();
	    return this;
	},
	build:function(){
		var as=this.$element.contents().detach();
		var h='<div id="wrapper" style="position:relative;width:100%;height:100%;overflow:hidden;">'+
  			  	'<div id="scroller" style="position:relative;width:100%;"></div>'+
  			  '</div>'+
  			  '<div id="track" style="position:absolute;top:0px;right:1px;bottom:0px;width:10px;overflow:hidden">'+
  			  	'<div id="bar" style="position:relative;right:0px;display:none;border:1px solid white;left:0px;width:3px;border-radius:3px;-moz-border-radius:3px;-webkit-border-radius:3px;padding:1px;background:url(/xlive/web/img?method=png&c=000000&o=0.5) left top repeat;"></div>'+
  			  '</div>';
		this.$element.css({position:'relative'}).append(h);
		this.$element.find('div#scroller').append(as);
		var $wrap=this.$element.find('#wrapper');
		/*
		if($.browser.mobilesafari){
			var os=this.options;
			os.onScrollEnd=function(){this.showBar_iscroll(true);}.bind(this);
			this.iscroll=new iScroll(this.$element.find('#wrapper')[0],os);
			this.iscroll.refresh();
			$wrap.mouseover(function(e){e.stopPropagation();e.preventDefault();this.showBar_iscroll(true);}.bind(this));
			this.showBar_iscroll(false);
			return;
		}
		*/
		$wrap.bind('mousewheel',function(e,delta,deltaX,deltaY) {
			e.stopPropagation();e.preventDefault();this._mousewheel(e,delta, deltaX, deltaY);
		}.bind(this));
		$wrap.mouseover(function(e) {
			e.stopPropagation();e.preventDefault();$this.setBar();this.showBar(true);
		}.bind(this));
		var $bar=this.$element.find('div#bar'),$track=this.$element.find('div#track'),$this=this;
		var scr=this.$element.find('#scroller')[0],wrap=this.$element.find('#wrapper')[0];
		$bar.css({cursor:'pointer'})
		.draggable({
			axis: 'y',
			containment: 'parent',
			drag: function(event,ui){
				$bar.data('drag',true);
				if($bar[0].offsetTop+$bar[0].offsetHeight>=$track[0].offsetHeight)
					 wrap.scrollTop=scr.offsetHeight-wrap.offsetHeight+1;
				else wrap.scrollTop=$bar[0].offsetTop/wrap.offsetHeight*scr.offsetHeight;
			},
			stop: function(event, ui) {
				$bar.data('drag',false);
				$this.setBar();$this.showBar(true);
			}
		});
		$track.hover(
			function(){
				clearTimeout($this._showbar);
				$bar.css({opacity:(scr.offsetHeight> wrap.offsetHeight)?'0.5':'0',display:'block'});
			},function(){
				$this.showBar(false);
		});
		if($.browser.mobilesafari){
			 var $scr=this.$element.find('div#scroller');
			 $scr[0].addEventListener('touchmove', function(event) {
				    event.preventDefault();
				    var el = event.target;
				    var touch = event.touches[0];
				    var curY = $(el).offset().top - touch.pageY;
				    $wrap[0].scrollTop=$wrap[0].scrollTop+curY;
			},false);
		}
		this.scroll_top=0;
		this.setBar();
		this.showBar(true);
	},
	dispose:function(){
		if(this.iscroll)this.iscroll.destroy();
		this.iscroll=null;
	},
	getScroller:function(){
		return this.$element.find('div#scroller')[0];
	},
	clear:function(){
		this.$element.find('div#scroller').empty();
		this.refresh();
		return this;
	},
	add:function(div){
		this.$element.find('div#scroller').append(div);
	},
	refresh:function(){
		if(this.iscroll){
			setTimeout(function(){if(!this.iscroll)return;this.iscroll.refresh();this.showBar_iscroll(false);}.bind(this),2);
		}else{this.setBar();this.showBar(false);}
	},
	showBar_iscroll:function(on){
		var bar=this.iscroll['vScrollbarWrapper'];
		if(!bar)return;
		var scr=this.$element.find('#scroller')[0],wrap=this.$element.find('#wrapper')[0];
		(this._timeout && clearTimeout(this._timeout));
		if(on){
			$(bar).css({opacity:(scr.offsetHeight>wrap.offsetHeight)? '1':'0',display:'block'});
			this._timeout=setTimeout((function(){this.showBar_iscroll(false);}).bind(this), 500);
		}else $(bar).fadeOut();
	},
	showBar:function(on){
		var $bar=this.$element.find('#bar');
		var wrap=this.$element.find('#wrapper')[0],scr=this.$element.find('#scroller')[0];
		(this._showbar && clearTimeout(this._showbar));
		if($bar.data('drag'))return this._showbar=setTimeout((function(){this.showBar(on);}).bind(this), 1000);
		if(on){
			$bar.css({opacity:(scr.offsetHeight> wrap.offsetHeight)?'0.5':'0',display:'block'});
			this._showbar=setTimeout((function(){this.showBar(false);}).bind(this), 1000);
		}else $bar.fadeOut();
	},
	setBar:function(){
		var wrap=this.$element.find('#wrapper')[0],scr=this.$element.find('#scroller')[0];
		var scr_h=(scr.offsetHeight||1);
		var wrap_h=(wrap.offsetHeight||1);
		var b_h=Math.max(Math.round(wrap_h*wrap_h/scr_h),5);
		var b_p=parseInt(wrap.scrollTop*wrap_h/scr_h,10);
		this.$element.find('#bar').css({display:(scr_h > wrap_h)?'block':'none',height:b_h+'px',top:b_p+'px'});
	},
	_mousewheel:function(event,delta,deltaX,deltaY){
		event.stopPropagation();
		event.preventDefault();
		var wrap=this.$element.find('#wrapper')[0],scr=this.$element.find('#scroller')[0];
		if(wrap.offsetHeight>=scr.offsetHeight)return wrap.scrollTop=0;
		this.showBar(true);
        var max= scr.offsetHeight-wrap.offsetHeight;
        var top=wrap.scrollTop;
        top +=-delta*this.options.step;
        top =(top<0)?0:(top>max)?max:top;
        this.scrollOffset(null,top);
	},
	scrollEnd:function(){
	},
	scrollOffset:function(x,y){
		this.$element.find('#wrapper')[0].scrollTop=y;
		this.setBar();
	}
};
	

var aPxml ={
		options:{
			node:null,
			blankString:'--blank--',
			completeString:'complete',
			galleryString:'gallery',
			uploadString:'set upload file ...',
			uploadMsgString:'press the upload button to upload file',
			uploadFailString:'upload failure :',
			fbAlbumsString:'FB albums',
			fbVideosString:'FB videos',
			galleryString:'Gallery',
			albumsString:'albums',
			videosString:'videos',
			dirString:'directories',
			numString:'enter the number for the item : ',
			numMaxString:' >  max :',
			numMinString:' < min :',
			kvItem:'item : ',
			kvInfo:'information : ',
			showCancel:true,
			showToolbar:true,
			showComplete:true,
			fbId:'me',
			cbComplete:null,
			cbCancel:null
		},
		init:function(e,options) {
			this.options=$.extend({},this.options,options);
		    this.$element=$(e);
		    this.$element.addClass('aPxml');
		    this.setup();
		    return this;
		},
		setup:function(){
			var b='<div class="class_pxml _default_"><div id="pxml_toolbar"><div id="xlvid_back"><span id="xlvid_backright"></span></div><div id="xlvid_name"></div><img id="xlvid_cancel" src="/xlive/images/pxml/cancel.png"></img></div><div id="pxml_viewport"><div id="pxml_canvas"></div></div></div>';
			$(b).appendTo(this.$element);
			this._$viewport=this.$element.find('#pxml_viewport');
			this._$canvas=this.$element.find('#pxml_canvas');
			this._$toolBar=this.$element.find('#pxml_toolbar');
			this._$back=this.$element.find('#xlvid_back');
			this._$back_title=this.$element.find('#xlvid_backright');
			this._$name=this.$element.find('#xlvid_name');
			this._$cancel=this.$element.find('#xlvid_cancel');
			if(!this.options.showCancel)this._$cancel.hide();
			if(!this.options.showToolbar)this._$toolBar.hide();
			this._$currentBox=null;
			this.$element.click(function(e){this._click(e);}.bind(this));
			this.$element.mousedown(function(e){this._mousedown(e);}.bind(this));
			this._$back_title.html(this.options.completeString);
			this._checkTitleWidth();
			if(this.options.node) this.build(this.options.node);
		},
		_mousedown:function(e){
			var id=e.target.id;
			if(id=='xlvid_input_button'){
				e.stopPropagation();e.preventDefault();
				var $item=$(e.target).closest('#xlvid_item');
				return $item.find('input')[0][!$item.hasClass('editing')?'focus':'blur']();
			}
			if(id=='xlvid_back'||id=='xlvid_backright'){
				e.stopPropagation();e.preventDefault();
				if(this._$currentBox.prev('#pxml_box').length>0) return this.back();
				if(!this._commitBox(this._$currentBox))return;
				return this.complete();
			}
			if(id=='xlvid_cancel'){
				e.stopPropagation();e.preventDefault();
				return this.cancel();
			}
		},
		_click:function(e){
			e.stopPropagation();e.preventDefault();
			var id=e.target.id;
			var $item=$(e.target).closest('#xlvid_item');
			if($item.length > 0){
				var node=$item.data('node');
				var $node=xml$(node);
				if(id=='xlvid_exec_method'){
					var m=node.getAttribute('methodName');
					return (m)?(this[m])?this[m](node):alert('methodName not found :'+m):null;
				}
				if($item.hasClass('advanced')&& node) return this._createBox(node);
				var pxml=node.getAttribute('pxml');
				if(pxml=='select' && $item.hasClass('item_node')){
					var multi=node.getAttribute('multiple');
					if(multi=='true'){
						$item.toggleClass('checked');
						var sbs = $item.siblings($item[0].tagName).andSelf(); 
						var options='';
						for(var i=0;i<sbs.length;++i){
							var $sb=$(sbs[i]);
							if($sb.hasClass('checked')){
								var optval=($sb.data('option')||'');
								if(node.getAttribute('showValue')=='true')optval +='('+$sb.data('optval')+')';
								options += ((options!='')?',':'')+optval;
							}
						}
						$node.text('.',options);
					}else{
						$item.siblings($item[0].tagName).removeClass('checked'); 
						$item.addClass('checked');
						var optval=($item.data('option')||'');
						if(node.getAttribute('showValue')=='true')optval +='('+$item.data('optval')+')';
						$node.text('.',optval);
					}
					return;
				}
				if(id=='xlvid_bool_onoff'){
					$item.toggleClass('checked');
					return $node.text('.',$item.hasClass('checked')? node.getAttribute('true'):node.getAttribute('false'));
				}
				if(id=='xlvid_int_add'){
					var max =node.getAttribute('max');
					max = (!max && max != 0) ? Number.MAX_VALUE : max;
					var inp = $item.find('input');
					var v = (parseInt(inp.val(),10)||0)+1;
					if(v <= max || max==Number.MAX_VALU)inp.val(v);
					return;
				}
				if(id=='xlvid_int_sub'){
					var min = node.getAttribute('min');
					min = (!min && min != 0) ? Number.MIN_VALUE : min;
					var inp =$item.find('input');
					var v = (parseInt(inp.val(),10)||0)-1;
					if(v >= min || min == Number.MIN_VALUE) inp.val(v);
					return;
				}
			}
		},
		build:function(node){
			if(node.getAttribute('pxml')=='pxml'){
				this._disposeAllBox();
				this._createBox(this.options.node=node);
			}
		},
		_disposeAllBox:function(){
			this._$currentBox=null;
			this._$canvas.empty();
		},
		boxForToolBar:function($box){
			var node=$box.data('node');
			var name=(node.getAttribute('name')||'');
			if(name)this._$name.html(name);
			var n=$box.prev('#pxml_box').data('node');
			this._$back_title.html((n)?(n.getAttribute('name')||n.tagName):this.options.completeString);
			this._$back[(!n && !this.options.showComplete)?'hide':'show']();
			this._checkTitleWidth();
		},
		_checkTitleWidth:function(){
			try{
				var b=this._$toolBar[0],k=this._$back[0];
				var bw=b.clientWidth-parseInt(b.style.paddingLeft||0,10)-parseInt(b.style.paddingRight||0,10);
				var kw=k.offsetWidth+parseInt(k.style.marginLeft||0,10)+parseInt(k.style.marginRight||0,10);
				this._$name.css({width:(bw-kw-32)+'px'});
			}catch(e){}
		},
		complete:function(){
			if(this.options.cbComplete)this.options.cbComplete();
		},
		cancel:function(){
			if(this.options.cbCancel)this.options.cbCancel();
		},
		back:function(){
			var $box = this._$currentBox.prev('#pxml_box');
			if($box.length>0){
				if(!this._commitBox(this._$currentBox)) return;
				var $old=this._$currentBox;
				this._$currentBox=$box;
				this._refreshBox($box);
				this._slideToBox($box,$old);
			}
		},
		_slideToBox:function($box,$old){
			this.boxForToolBar($box);
			var THIS=this;
			/*
			this._$viewport.animate({scrollLeft:$box.position().left},{
				duration:300,
				complete: function(){if($old)$old.detach();THIS.slideToBoxEnd();}
			});
			*/
			this._$canvas.animate({left:-$box.position().left},{
				duration:300,
				complete: function(){if($old)$old.detach();THIS.slideToBoxEnd();}
			});
		},
		slideToBoxEnd:function(){
			if(FB && FB.Canvas)FB.Canvas.setSize();
		},
		_commitBox:function($box){
			var $lis=$box.find('li');
			for(var i=0;i<$lis.length;++i){
				var $li=$($lis[i]);
				var node=$li.data('node');
				var $node=xml$(node);
				if($li.hasClass('textarea_node')){
					$node.text('.',$li.find('textarea').val());
					continue;
				}
				if($li.hasClass('rich_node')){
					$node.text('.',$li.find('.aRich').data('aRich').commit().getText());
					continue;
				}
				if($li.hasClass('input')){
					var $input = $li.find('input');
					if($input) {
						$input.blur();
						$node.text('.', $input.val()==$input.data('blank')?'':$input.val());
					}
					continue;
				}
				if($li.hasClass('kv_node')){
					var val=$li.find('#kv_k').val();
					node.setAttribute('k', (val||''));
					var val=$li.find('#kv_v').val();
					node.setAttribute('v', (val||''));
					continue;
				}
				if($li.hasClass('int')){
					var $input = $li.find('input');
					$input.blur();
					var v=parseInt(($input.val()==$input.data('blank'))?'':$input.val(),10);
					var n = node.getAttribute('name');
					if(!isFinite(v)){
						alert(this.options.numString+n);
						return false;
					}
					var max =node.getAttribute('max');
					max = (!max && max != 0) ? Number.MAX_VALUE : max;
					var min =node.getAttribute('min');
					min = (!min && min != 0) ? Number.MIN_VALUE : min;
					if(max!=Number.MAX_VALUE && v > max){
						alert(n+' : '+v+this.options.numMaxString+max);
						return false;
					}
					if((min!=Number.MIN_VALUE) && v < min){
						alert(n+' : '+v+this.options.numMinString+min);
						return false;
					}
					$node.text('.',v);
					continue;
				}
				if($li.hasClass('img_node')){
					var up = $li.find('#xlvid_uploader').data('aUploader');
					if(up && up.fileCount()>0) {
						alert(this.options.uploadMsgString);
						return false;
					}
					continue;
				}
				if($li.hasClass('html_node')){
					var mod=$li.data('module');
					if(mod){
						var ok=(mod['commit'])? mod.commit($box):true;
						if(ok===false) return false;
						if(mod['result']){
							if(typeof(ok=mod.result())=='string')$node.text('.',ok);
						}
					}
					continue;
				}
			}
			return this.commitBox($box);
		},
		commitBox:function($box){
			return true;
		},
		_refreshBox:function($box){
			$box=($box||this._$currentBox);
			var $lis=$box.find('li');
			for(var i=0;i<$lis.length;++i){
				var $li=$($lis[i]);
				var node=$li.data('node');
				var $node=xml$(node);
				if($li.hasClass('select')){
					this._setItemValue($li,$node.text());
					continue;
				}
				if($li.hasClass('rich')){
					this._setItemValue($li,$node.text().stripTags());
					continue;
				}
				if($li.hasClass('textarea')){
					this._setItemValue($li,$node.text());
					continue;
				}
				if($li.hasClass('kv')){
					var k=node.getAttribute('k'),v=node.getAttribute('v');
					k=(!k||k=='')?node.getAttribute('kblank'):k;
					v=(!v||v=='')?node.getAttribute('vblank'):v;
					$li.find('#k').text(k);
					$li.find('#v').text(v);
					continue;
				}
				if($li.hasClass('date')){
					var text=$node.text();
					var f=(node.getAttribute('format')||'yyyy-MM-dd');
					text=(text!='')?new Date().parseDate(text).formatDate(f):text;
					this._setItemValue($li,text);
					continue;
				}
				if($li.hasClass('img')||$li.hasClass('fbimg')){
					var text=$node.text();
					var aimg=$li.find('.aImg');
					if(text==''){
						this._setItemValue($li,'');
						aimg.hide();
					}else {
						this._setItemValue($li,'',true);
						aimg.data('aImg').imgSrc(text);
						aimg.show();
					}
					continue;
				}
				if($li.hasClass('html')){
					this._setItemValue($li,$node.text());
					continue;
				}
				if($li.hasClass('pxml')){
					var t=$node.text('data');
					this._setItemValue($li,(t&&t!='')?t:' ');
					continue;
				}
			}
			this.refreshBox($box);
		},
		refreshBox:function($box){
		},
		_createBox:function(node){
			var $box=(this._$currentBox)?this._$currentBox.next('#pxml_box'):null;
			if(!$box || $box.length==0){
				var $befs = (this._$currentBox)?this._$currentBox.prev('#pxml_box'):null;
				var count=(!$befs)?3:$befs.length+3;
				var width=this._$viewport.width();
				var padding=8;
				if($.browser.msie && width==0)width=this._$viewport[0].offsetWidth;
				if(this._$canvas.width()<count*width)this._$canvas.css({width:count*width+'px'});
				$box=$('<div></div>').prop('id','pxml_box').css({'width':(width-padding*2)+'px','min-height':'1px'}).appendTo(this._$canvas);
			}
			if('pxml' == node.getAttribute('pxml')) this.pxmlForBox(node,$box);
			else this.nodeForBox(node,$box);
			if(this._$currentBox) this._commitBox(this._$currentBox);
			this._$currentBox=$box;
			this._slideToBox($box);
		},
		pxmlForBox:function(node,$box){
			if($box.data('node')==node)return;
			$box.data('node',node);
			$box.empty();
			var ns=xml$(node).nodes('child::*');
			var $gul=null;
			for(var i =0;i<ns.length;++i){
				var pxml = ns[i].getAttribute('pxml');
				if(pxml){
					if(pxml == 'g'){
						if($gul)this._completeCreateULGroup($gul);
						$gul=null;
						var $subg=this._createULGroup($box,ns[i]);
						var sub=xml$(ns[i]).nodes('child::*[@pxml]');
						for(var m=0;m<sub.length;++m)this._createLINode($subg,sub[m]);
						this._completeCreateULGroup($subg);
						continue;
					} 
					if(!$gul) $gul=this._createULGroup($box,ns[i]);
					this._createLINode($gul,ns[i]);
				}
			}
			var create=node.getAttribute('create');
			if(create && create.strip().length > 0){
				if(!$gul) $gul=this._createULGroup($box,node);
				var attrs=create.split(";");
				var tag=null,label='';
				for(var i=0;i<attrs.length;++i){
					var kv=attrs[i].split(":");
					if(kv[0]=='tag')tag=node.ownerDocument.createElement(kv[1]);
					else if(tag)tag.setAttribute(kv[0],kv[1]);
					if(kv[0]=='label')label=kv[1];
				}
				var $li=$('<li></li>').attr('id','xlvid_item').css({'textAlign':'left'}).data({'node':node,'pattern':tag,'ul':$gul}).html('+'+label).appendTo($gul);
				$li.click(function(){
					var n,$newli;
					node.appendChild(n=$li.data('pattern').cloneNode(true));
					$li.data('ul').find('li:last-child').before($newli=this._createLINode(null,n));
					this._adjustElementWidth($newli);
				}.bind(this));
			}
			if($gul)this._completeCreateULGroup($gul);
		},
		_completeCreateULGroup:function($gul){
		},
		nodeForBox:function(node,$box){
			if($box.data('node')==node)return;
			$box.data('node',node);
			$box.empty();
			var $ul=this._createULGroup($box,node);
			var pxml=node.getAttribute('pxml');
			if(pxml=='select') this._selectNode($ul,node);
			if(pxml=='kv') this._kvNode($ul,node);
			if(pxml=='rich') this._richNode($ul,node);
			if(pxml=='textarea') this._textareaNode($ul,node);
			if(pxml=='date') this._dateNode($ul,node);
			if(pxml=='img') this._imgNode($ul,node);
			if(pxml=='fbimg') this._fbimgNode($ul,node);
			if(pxml=='html') this._htmlNode($ul,node);
			return $ul;
		},
		_adjustElementWidth:function($li){
			if($li.hasClass('kv')){
				var w=($li.width()-40)/3-10;
				$li.find('#k').css({width:w});
				$li.find('#v').css({width:2*w});
			}
		},
		_fixElementWidth:function($li,$e,padding){
			padding=(padding||24);
			var width=0,cs=$li[0].childNodes;
			for(var i=0; i<cs.length; i++){
				var w = (cs[i].offsetWidth) ? cs[i].offsetWidth:(cs[i].tagName.toLowerCase()=='img')? 24 : 0;
				width +=(cs[i] != $e[0])? w:0;
			}
			width=$li[0].offsetWidth-width-padding;
			if(width < 0)width=0;
			$e.css({'width':width+'px'});
		},
		_selectNode:function($ul,node){
			var options=node.getAttribute('options').split(',');
			var values=(''+(node.getAttribute('values')||node.getAttribute('options'))).split(',');
			for(var i=0;i<options.length;++i){
				var ops=(options[i]||'');
				var vals=(values[i]||'');
				var $li=$('<li id="xlvid_item" class="select_node item_node"><div id="xlvid_name"></div><img id="xlvid_checked" src="/xlive/images/pxml/checked.png"></img><div id="xlvid_value"></div></li>').appendTo($ul);
				$li.data({'node':node,option:ops,optval:vals}).find('#xlvid_name').html(ops);
				var $val=$li.find('#xlvid_value').html(vals);
				if(ops !='' && xml$(node).text().indexOf(ops)>=0) $li.addClass('checked');
				else $li.removeClass('checked');
				if(node.getAttribute('showValue')=='true') this._fixElementWidth($li,$val,64);
				else $val.hide();
			}
		},
		_richNode:function($ul,node){
			var $li=$('<li id="xlvid_item" class="rich_node item_node"></li>').css({'height':'auto'}).data('node',node).appendTo($ul);
			var rich=$('<div></div>').css({margin:'0 12px 0 0'}).appendTo($li).aRich({lightVersion:true,editable:!('true'==node.getAttribute('readonly'))}).data('aRich');;
			if(!rich.options.editable)rich.enabled(false);
			rich.setText(xml$(node).text());
			return rich;
		},
		_textareaNode:function($ul,node){
			var cols=(node.getAttribute('cols')||100),rows=(node.getAttribute('rows')||10);
			var $li=$('<li id="xlvid_item" class="textarea_node item_node"></li>').css({'height':'auto'}).data('node',node).appendTo($ul);
			var $area=$('<textarea></textarea>').css({width:'98%'}).attr({'cols':cols,'rows':rows}).val(xml$(node).text()).appendTo($li);
			if('true'==node.getAttribute('readonly'))$area.attr('READONLY','READONLY');
			return $area;
		},
		_kvNode:function($ul,node){
			var $li=$('<li id="xlvid_item" class="kv_node item_node" style="align:left;text-align:left;padding:12px;"></li>').css({'height':'auto'}).data('node',node).appendTo($ul);
			$('<div style="line-height:24px"></div>').html(this.options.kvItem).appendTo($li);
			$('<input id="kv_k" style="margin:8px 0px;padding:4px;" ></input>').css({width:'94%'}).val(node.getAttribute('k')).appendTo($li);
			$('<div style="line-height:24px;padding:4px;"></div>').html(this.options.kvInfo).appendTo($li);
			var $area=$('<textarea id="kv_v"></textarea>').css({width:'96%'}).attr({'rows':10}).val(node.getAttribute('v')).appendTo($li);
			return $area;
		},
		_dateNode:function($ul,node){
			var $li=$('<li id="xlvid_item" class="date_node item_node"><center><div id="set_date"></div></center></li>').css({'height':'auto'}).data('node',node).appendTo($ul);
			var $cal=$('<div></div>').aCalendar().appendTo($li.find('center'));
			var cal=$cal.data('aCalendar');
			var df=(node.getAttribute('format')||'yyyy-MM-dd');
			var ds=xml$(node).text();
			if(ds !='') {
				var date=new Date().parseDate(ds);
				cal.setDate(date);
				$li.find('#set_date').html(date.formatDate(df));
			}
			cal.options.callback=function(date){
				$li.find('#set_date').html(date.formatDate(df));
				xml$($li.data('node')).text('.',date.formatDate(df));
			};
		},
		_imgNode:function($ul,node){
		},
		_fbimgNode:function($ul,node){
			var $li=$('<li id="xlvid_item" class="fbimg_node item_node"><center style="position:relative"><div id="pxml_fbimg_ximg"></div></center></li>').css({'height':'auto'}).data('node',node).appendTo($ul);
    		var img_src=xml$(node).text();
    		if(img_src && img_src != '' && img_src.indexOf(';') >= 0) img_src=img_src.split(';')[0];
			var $aimg=$li.find('#pxml_fbimg_ximg').aImg({src:img_src}).css({position:'relative'});
			var video_true=node.getAttribute('video')=='true'?true:false;
			var detail_true=node .getAttribute('detail')=='true'?true:false;
    		var $imgdel=$('<img id="pxml_fbimg_del" src="/xlive/images/pxml/minus.png"></img>').css({position:'absolute',top:'0px',right:'0px',zIndex:'1'}).appendTo($aimg);
    		$imgdel.css('visibility',(img_src=='')?'hidden':'visible').click(function(e){
				$aimg.data('aImg').imgSrc(null);
				xml$(node).text('.','');
				$imgdel.css('visibility','hidden');
    			});
    		var defw=$li.width();
    		var tabs=[this.options.fbAlbumsString,this.options.galleryString];
    		if(video_true) tabs.push(this.options.fbVideosString);
    		var $tab=$('<div id="pxml_imgtab"></div>').aTab({'tabs':tabs}).appendTo($li);
    		$tab.data('aTab').options.callback=function(idx){
    			if(idx==0){
    				var $gv=$gyarea.find('#pxml_gyview');
    				$gv.data('left',$gv[0].scrollLeft);
    				$gyarea.hide();
    				var $fvv=$fbvarea.find('#pxml_fbvview');
    				$fvv.data('left',$fvv[0].scrollLeft);
    				$fbvarea.hide();
    				$fbarea.show();
    				var $fv=$fbarea.find('#pxml_fbview');
    				if($fv.data('left'))$fv[0].scrollLeft=$fv.data('left');
    			}
    			if(idx==2){
    				if(!$tab.data('videos')){
    					loadFBVideos();
    					$tab.data('videos',true);
    				}
					var $gv=$gyarea.find('#pxml_gyview');
					$gv.data('left',$gv[0].scrollLeft);
					$gyarea.hide();
    				var $fv=$fbarea.find('#pxml_fbview');
    				$fv.data('left',$fv[0].scrollLeft);
    				$fbarea.hide();
    				$fbvarea.show();
    				var $fvv=$fbvarea.find('#pxml_fbvview');
    				if($fvv.data('left'))$fvv[0].scrollLeft=$fv.data('left');
    			}
    			if(idx==1){
    				if(!$tab.data('gallery')){
    					loadGallery();
    					$tab.data('gallery',true);
    				}
    				var $fv=$fbarea.find('#pxml_fbview');
    				$fv.data('left',$fv[0].scrollLeft);
    				$fbarea.hide();
    				var $fvv=$fbvarea.find('#pxml_fbvview');
    				$fvv.data('left',$fvv[0].scrollLeft);
    				$fbvarea.hide();
    				$gyarea.show();
    				var $gv=$gyarea.find('#pxml_gyview');
    				if($gv.data('left'))$gv[0].scrollLeft=$gv.data('left');
    			}
    		}.bind(this);
    		//
    		$('<div style="clear:both"></div>').appendTo($li);
			//////////////////////////
			var $fbarea=$('<div id="pxml_fbarea"><div id="pxml_fbnav"><span id="pxml_fbroot"></span><span id="pxml_fbdesc"></span></div><div id="pxml_fbview"><div id="pxml_fbcanvas"><div id="pxml_fbalbums"><div id="pxml_albums"></div></div><div id="pxml_fbphotos"><div id="pxml_photos"></div></div><div style="clear:both;"></div></div></div></div>').appendTo($li);
			$fbarea.find('#pxml_fbnav').click(function(e){
				$fbarea.find('#pxml_fbdesc').html('');
				$fbarea.find('#pxml_fbview').animate({scrollLeft:0},{duration:300});
			}.bind(this));
			$fbarea.find('#pxml_fbview').css('width',defw+'px');
			$fbarea.find('#pxml_fbcanvas').css('width',(defw*3)+'px');
			$fbarea.find('#pxml_fbalbums').css('width',defw+'px');
			var $albums=$fbarea.find('#pxml_albums').css('width',(defw-12)+'px').aScroller({step:60});//scroll
			var albums=$albums.data('aScroller');
			$fbarea.find('#pxml_fbphotos').css('width',(defw-4)+'px');
			var $photos=$fbarea.find('#pxml_photos').css('width',(defw-12)+'px').aScroller({step:60});//scroll
			var photos=$photos.data('aScroller');
			$fbarea.find('#pxml_fbroot').html(this.options.albumsString);
			$albums.click(function(e){
				var $a=$(e.target).closest('#pxml_album');
				if($a.length>0){
					$fbarea.find('#pxml_fbdesc').html('&nbsp;:&nbsp;'+$a.data('name'));
					photos.changeAlbum($a.data('id'));
					var view=$fbarea.find('#pxml_fbview');
					view.animate({scrollLeft:view[0].offsetWidth},{duration:300});
				}
				return false;
			}.bind(albums));
			albums.refresh();
			//
			var names={};
			var albumids={};
			var lasts={};
			FB.api('/'+this.options.fbId+'/albums', function(resp) {
			    for(var i=0, l=resp.data.length; i<l; i++) {
			        var pid=''+resp.data[i].cover_photo;
			        names[pid]=resp.data[i].name;
			        albumids[pid]=resp.data[i].id;
			        lasts[pid]=(i==(l-1));
			        FB.api('/'+pid,function(resp2){
			        	 if(resp2.picture){
			        		var $album=$('<div id="pxml_album"><div id="pxml_outter"><div id="pxml_inner"><div id="pxml_photo"></div></div></div><div id="pxml_album_title"></div></div>').appendTo(albums.getScroller());
			        		$album.find('#pxml_photo').aImg({width:120,height:100,src:resp2.picture});
			        		$album.find('#pxml_album_title').html(names[resp2.id]);
			        		$album.data({id:albumids[resp2.id],name:names[resp2.id]});
			        		if(lasts[resp2.id])	$('<div style="clear:both"></div>').appendTo(albums.getScroller());
			        		albums.refresh();
			        	 } 
			        	}.bind(this)
			        );
			    }
			 }.bind(this));
			$photos.click(
				function(e){
					var d=$(e.target).closest('#pxml_photo').data('data');
					if(d && d.picture){
						$aimg.data('aImg').imgSrc(d.picture);
						var data=(detail_true)?d.picture+';'+'photo;'+d.id+';'+((d.name)?d.name:'')+';'+d.name: d.picture;
						xml$(node).text('.',data);
						$imgdel.css('visibility','visible');
					}
					return false;
				}
			);
			photos.refresh();
			photos.changeAlbum=function(album_id){
				$(photos.getScroller()).empty();
				FB.api('/'+album_id+'/photos',{limit:100}, function(resp) {
					 var $fbdesc=$fbarea.find('#pxml_fbdesc');
					 $fbdesc.html($fbdesc.html()+' ('+resp.data.length+')');
					 for(var i=0, l=resp.data.length; i<l; i++) {
		        		var $frame=$('<div id="pxml_frame"><div id="pxml_outter"><div id="pxml_inner"><div id="pxml_photo"></div></div></div></div>').appendTo(photos.getScroller());
		        		$frame.find('#pxml_photo').aImg({width:120,height:100,src:resp.data[i].picture}).data('data',resp.data[i]);
					 }
					 $('<div style="clear:both"></div>').appendTo(photos.getScroller());
					 photos.refresh();
				});
			}.bind(photos);
			var $gyarea=$('<div id="pxml_gyarea" style="display:none"><div id="pxml_gynav"><span id="pxml_gyroot"></span><span id="pxml_gydesc"></span></div><div id="pxml_gyview"><div id="pxml_gycanvas"><div id="pxml_gydirs"><div id="pxml_dirs"></div></div><div id="pxml_gypngs"><div id="pxml_pngs"></div></div><div style="clear:both;"></div></div></div></div>').appendTo($li);
			$gyarea.find('#pxml_gynav').click(
				function(){
					$gyarea.find('#pxml_gydesc').html('');
					$gyarea.find('#pxml_gyview').animate({scrollLeft:0},{duration:300});
					return false;
			});
			$gyarea.find('#pxml_gyview').css('width',defw+'px');
			$gyarea.find('#pxml_gycanvas').css('width',(defw*3)+'px');
			$gyarea.find('#pxml_gydirs').css('width',defw+'px');
			$gyarea.find('#pxml_dirs').click(
				function(e){
					var $dir=$(e.target).closest('#pxml_dir');
					if($dir.length>0){
						var node=$dir.data('node');
						$gyarea.find('#pxml_gydesc').html('&nbsp;:&nbsp;'+node.getAttribute('name'));
						$gyarea.find('#pxml_pngs').data('aScroller').changeDir(node);
						var $view=$gyarea.find('#pxml_gyview');
						$view.animate({scrollLeft:$view[0].offsetWidth},{duration:300});
					}
					return false;
			});
			$gyarea.find('#pxml_gypngs').css('width',(defw-4)+'px');
			$gyarea.find('#pxml_pngs').click(
				function(e){
					var $png=$(e.target).closest('#pxml_png');
					if($png.length>0){
						$aimg.data('aImg').imgSrc($png.data('name'));
						xml$(node).text('.',$png.data('name'));
						$imgdel.css('visibility','visible');
					}
					return false;
				}
			);
			var pngs= $gyarea.find('#pxml_pngs').css('width',(defw-12)+'px').aScroller({step:60}).data('aScroller');
			var dirs= $gyarea.find('#pxml_dirs').css('width',(defw-12)+'px').aScroller({step:60}).data('aScroller');
			$gyarea.find('#pxml_gyroot').html(this.options.dirString);
			dirs.refresh();
			var loadGallery=function(){
					 $gyarea.show();
					 dm$('gallery',{url:'pxml',method:'pxml',arg:xml$('<pxml><name>gallery</name></pxml>').node()}).result('pxml').done(
							 function(data){
								 var gs=xml$(data).nodes('data/gallery/dir');
								 for(var i=0, l=gs.length; i<l; i++) {
					        		var $dir=$('<div id="pxml_dir"><div id="pxml_outter"><div id="pxml_inner"><div id="pxml_png"></div></div></div><div id="pxml_dir_title"></div></div>').appendTo(dirs.getScroller());
					        		$dir.find('#pxml_png').aImg({width:120,height:100,src:gs[i].getAttribute('src')});
					        		$dir.find('#pxml_dir_title').html(gs[i].getAttribute('name'));
					        		$dir.data('node',gs[i]);
								 }
								 $('<div style="clear:both"></div>').appendTo(dirs.getScroller());
								 dirs.refresh();
							 }
					 	);
				}.bind(this);
			pngs.refresh();
			pngs.changeDir=function(dir_node){
				 $(pngs.getScroller()).empty();
				 var ps=xml$(dir_node).nodes('png');
				 var $desc=$gyarea.find('#pxml_gydesc');
				 $desc.html($desc.html()+' ('+ps.length+')');
				 for(var i=0, l=ps.length; i<l; i++) {
	        		var $frame=$('<div id="pxml_frame"><div id="pxml_outter"><div id="pxml_inner"><div id="pxml_png"></div></div></div></div>').appendTo(pngs.getScroller());
	        		$frame.find('#pxml_png').aImg({width:120,height:100,src:ps[i].getAttribute('src')}).data('name',ps[i].getAttribute('src'));
				 }
				 $('<div style="clear:both"></div>').appendTo(pngs.getScroller());
				 pngs.refresh();
			};
			var $fbvarea=$('<div id="pxml_fbvarea" style="display:none"><div id="pxml_fbvnav"><span id="pxml_fbvroot"></span><span id="pxml_fbvdesc"></span></div><div id="pxml_fbvview"><div id="pxml_fbvcanvas"><div id="pxml_fbvideos"><div id="pxml_videos"></div></div><div style="clear:both;"></div></div></div></div>').appendTo($li);
			$fbvarea.find('#pxml_fbvview').css('width',defw+'px');
			$fbvarea.find('#pxml_fbvcanvas').css('width',(defw*3)+'px');
			$fbvarea.find('#pxml_fbvideos').css('width',(defw-4)+'px');
			$fbvarea.find('#pxml_videos').css('width',(defw-12)+'px');
			$fbvarea.find('#pxml_fbvroot').html(this.options.videosString);
			var videos=$fbvarea.find('#pxml_videos').aScroller({step:60}).click(function(e){
				var $v=$(e.target).closest('#pxml_video');
				if($v.length > 0){
					var d=$v.data('data');
					$aimg.data('aImg').imgSrc(d.picture);
					var data=d.picture+';video;'+d.id+';'+((d.name)?d.name:'')+';'+((d.description)?d.description:'')+';'+d.source+';';
					xml$(node).text('.',data);
					$imgdel.css('visibility','visible');
				}
				return false;
			}).data('aScroller');
			videos.refresh();
			var loadFBVideos = function(){
				$fbvarea.show();
				FB.api('/'+this.options.fbId+'/videos/uploaded',{limit:100},function(resp) {
				    for(var i=0, l=resp.data.length; i<l; i++) {
				    	var data=resp.data[i];
		        		var $frame=$('<div id="pxml_frame"><div id="pxml_outter"><div id="pxml_inner"><div id="pxml_video"></div></div></div><div id="pxml_video_title"></div></div>').appendTo(videos.getScroller());
		        		$frame.find('#pxml_video').aImg({width:120,height:100,src:data.picture}).data({id:data.id,name:data.name,'data':data});
		        		$frame.find('#pxml_video_title').html(data.name);
				    }
				    $('<div style="clear:both"></div>').appendTo(videos.getScroller());
				    videos.refresh();
				 });
			}.bind(this);
			$li.css('height','auto');
		},
		_htmlNode:function($ul,node){
			var $li=$('<li id="xlvid_item" class="html_node item_node"></li>').css({'height':'auto'}).data('node',node).appendTo($ul);
			var obj=node.getAttribute('objectName');
			$li.load(node.getAttribute('url'),function(){
				try{
					if(obj=eval(obj)){
						if(obj['initial']) obj['initial'](node);
						$li.data('module',obj);
					}
				}catch(e){};
			});
		},
		_createULGroup:function($box,node,first){
			var prolog,epilog,$first=(first)?$($box[0].firstChild):null;
			var $html=$('<div><div class="prolog"></div><div id="border"><ul></ul></div><div class="epilog"></div></div>');
			if($first)$first.before($html);
			else $box.append($html);
			$html.find('#border').css({border:'1px solid gray',padding:'6px 0px',background:'white','border-radius':'8px','-moz-border-radius':'8px','-webkit-border-radius':'8px','-o-border-radius':'8px','-ms-border-radius':'8px'});
			if(node &&(prolog=node.getAttribute('prolog'))) $html.find('.prolog').html(prolog);
			if(node &&(epilog=node.getAttribute('epilog')))	$html.find('.epilog').html(epilog);		
			return $html.find('ul').data('node',node||$box.data('node'));
		},
		_createLINode:function($ul,node){
			var icon,pxml;
			var $li=$('<li id="xlvid_item"><img id="xlvid_icon" src="/xlive/images/pxml/minus.png" style="display:none"></img><div id="xlvid_icon"></div><div id="xlvid_name"></div><img id="xlvid_advance" src="/xlive/images/pxml/advance.png"></img><div id="xlvid_value"></div></li>').data('node',node);
			if($ul)$li.appendTo($ul);
			if(node.getAttribute('removable')=='true'){
				$li.find('img#xlvid_icon').css('display','block').click(function(e){
					if(node && node.parentNode) node.parentNode.removeChild(node);
					$li.detach();
					return false;
				}.bind(this));
			}
			icon=node.getAttribute('icon');
			if(icon)$li.find('div#xlvid_icon').css('display','block').aImg({width:24,height:24,src:icon});
			$li.find('div#xlvid_name').html((node.getAttribute('name') || ''));
			pxml=node.getAttribute('pxml');
			$li.addClass('pxml_item '+pxml);
			if(pxml=='text') this._textItem($li,node);
			if(pxml=='bool') this._boolItem($li,node);
			if(pxml=='input') this._inputItem($li,node);
			if(pxml=='int') this._intItem($li,node);
			if(pxml=='date') this._dateItem($li,node);
			if(pxml=='rich') this._richItem($li,node);
			if(pxml=='textarea') this._textareaItem($li,node);
			if(pxml=='select') this._selectItem($li,node);			
			if(pxml=='img')this._imgItem($li,node);
			if(pxml=='fbimg')this._fbimgItem($li,node);
			if(pxml=='html')this._htmlItem($li,node);
			if(pxml=='pxml')this._pxmlItem($li,node);
			if(pxml=='exec')this._execItem($li,node);
			if(pxml=='kv')this._kvItem($li,node);
			$value=$li.find('div#xlvid_value');
			if($value.css('display') != 'none') this._fixElementWidth($li,$value,$li.hasClass('advanced')?28:2);
			return $li;
		},
		_getNodeBlankString:function(node){
			var b=node.getAttribute('blank');
			return b?b:this.options.blankString;
		},
		_setItemValue:function($li,value,displaynone){
			$li[(value=='')?'addClass':'removeClass']('blank');
			$li.find('#xlvid_value').css('display',(displaynone===true) ?'none':'inline-block').html((value=='')?this._getNodeBlankString($li.data('node')):value);
		},
		_textItem:function($li,node){
			this._setItemValue($li,xml$(node).text());
		},
		_boolItem:function($li,node){
			$li.find('#xlvid_value').hide();
			$li.append('<div id="xlvid_bool_onoff"></div>');
			if(xml$(node).text()==node.getAttribute('true'))$li.addClassName('checked');
		},
		_inputItem:function($li,node){
			$li.find('#xlvid_value').hide();
			$li.append('<img id="xlvid_input_button" src="/xlive/images/pxml/input_img.png"></img>');
			var $input=$('<input id="xlvid_input"></input>').appendTo($li);
			$input.data('blank',this._getNodeBlankString($li.data('node')))
			.data('value',xml$(node).text())
			.focus(function(){
				if($input.val()==$input.data('blank')) $input.val('');
				$li.removeClass('blank').addClass('editing');
			})
			.blur(function(){
				$li.removeClass('editing');
				if($input.val().strip().length==0){
					$li.addClass('blank');
					$input.val($input.data('blank'));
				}
			});
			$input.val(xml$(node).text());
			$input.blur();
			this._fixElementWidth($li,$input,8);
		},
		_kvItem:function($li,node){
			$li.addClass('advanced');
			$li.find('#xlvid_value').hide();
			var $k=$('<div id="k"></div>').appendTo($li),kb=node.getAttribute('kblank'),kk=node.getAttribute('k');
			$k.data('kblank',kb).data('value',kk).css({display:'inline-block',paddingRight:'8px',color:'gray',whiteSpace:'nowrap',lineHeight:'24px',align:'left',textAlign:'left',overflow:'hidden',textOverflow:'ellipsis'});
			$k.text((!kk||kk=='')? kb:kk);
			var $v=$('<div id="v"></div>').appendTo($li),vb=node.getAttribute('vblank'),vv=node.getAttribute('v');
			$v.data('vblank',vb).data('value',vv).css({display:'inline-block',color:'gray',whiteSpace:'nowrap',lineHeight:'24px',align:'left',textAlign:'left',overflow:'hidden',textOverflow:'ellipsis'});
			$v.text((!vv||vv=='')? vb:vv);
			$k.css({width:'1px'});
			$v.css({width:'2px'});
			if($li.width() > 0)this._adjustElementWidth($li);
		},
		_intItem:function($li,node){
			$li.find('#xlvid_value').hide();
			$li.append('<img id="xlvid_int_add" src="/xlive/images/pxml/plus.png"></img><img id="xlvid_int_sub" src="/xlive/images/pxml/minus.png"></img>');
			var $input=$('<input id="xlvid_input"></input>').appendTo($li);
			$input.data('blank',this._getNodeBlankString($li.data('node')))
			.data('value',xml$(node).text())
			.focus(function(){
				if($input.val()==$input.data('blank')) $input.val('');
				$li.removeClass('blank').addClass('editing');
				
			}).blur(function(){
				$li.removeClass('editing');
				if($input.val().length==0){
					$li.addClass('blank');
					$input.val($input.data('blank'));
				}
			});
			$input.val(xml$(node).text());
			$input.blur();
			this._fixElementWidth($li,$input,8);
		},
		_dateItem:function($li,node){
			if(!('true'==node.getAttribute('readonly'))) $li.addClass('advanced');
			var d=xml$(node).text();
			this._setItemValue($li,(d!='')? new Date().parseDate(d).formatDate((node.getAttribute('format')||'yyyy-MM-dd')):'');
		},
		_richItem:function($li,node){
			this._setItemValue($li.addClass('advanced'),xml$(node).text().stripTags());
		},
		_textareaItem:function($li,node){
			this._setItemValue($li.addClass('advanced'),xml$(node).text());
		},
		_selectItem:function($li,node){
			this._setItemValue($li.addClass('advanced'),xml$(node).text());
		},
		_imgItem:function($li,node){
			var text=xlm$(node).text();
			var fdm=node.getAttribute('dmName');
			var dm=(fdm)?this.getDataModel(fdm):null;
			if(dm && !('true'==node.getAttribute('readonly'))) $li.addClass('advanced');
			$('<div id="xlvid_img"></div>').aImg({src:text}).appendTo($li);
			if(text=='') this._setItemValue($li,'');
			else{
				$li.find('#xlvid_value').hide();
				$li.find('div#xlvid_img').aImg('imgSrc',text);
			}
		},
		_fbimgItem:function($li,node){
			var text=xml$(node).text();
			if(!('true'==node.getAttribute('readonly'))) $li.addClass('advanced');
			$('<div id="xlvid_img"></div>').aImg({src:text,width:24,height:24}).appendTo($li);
			if(text=='') this._setItemValue($li,'');
			else{
				$li.find('#xlvid_value').hide();
				if(text.indexOf(';')>=0) text=text.split(';')[0];
				$li.find('div#xlvid_img').aImg('imgSrc',text);
			}
		},
		_htmlItem:function($li,node){
			this._setItemValue($li.addClass('advanced'),xml$(node).text());
		},
		_execItem:function($li,node){
			$li.find('#xlvid_name').hide();
			$li.find('#xlvid_value').hide();
			this._fixElementWidth($li,$('<div id="xlvid_exec_method"></div>').appendTo($li).html(node.getAttribute('name')),2);
		},
		_pxmlItem:function($li,node){
			this._setItemValue($li.addClass('advanced'),node.getAttribute('data'));
		}
};
var aWxml ={
		options:{
			removeItemString:'remove item :',
			addItemString:'+ add item',
			addOptionString:'+ add option',
			inputNameString:'input name',
			optionNameProlog:'options name setting',
			optionNameEpilog:'',
			itemProlog:'',
			itemEpilog:'items name setting',
			optionNameBlank:'option name, (required)',
			itemNameBlank:'name',
			itemValueBlank:'value',
			multipleString:'multiple'
		},
		init:function(e,options){
			for(var p in aPxml)	if(!this[p])this[p]=aPxml[p];
			this.options=$.extend({},aPxml.options,this.options,options);
		    this.$element=$(e);
		    this.$element.addClass('aPxml aWxml');
		    this.setup();
		    return this;
		},
		_click:function(e){
			var id=e.target.id;
			if(id=='xlvid_remove'){
				var $item=$(e.target).closest('#xlvid_item');
				var name=$item.find('#xlvid_name').html();
				if($item.hasClass('select_node')&& $item.hasClass('item_node')&& $item.hasClass('write')){
					var $inp=$item.find('#xlvid_input');
					name=($inp.val()==$inp.data('blank'))?'':$inp.val();
				}
				if(name==''||confirm(this.options.removeItemString+name)){
					if($item.hasClass('pxml_item'))$item.data('node').parentNode.removeChild($item.data('node'));
					$item.remove();
				}
				return false;
			}
			if(id=='xlvid_multiple_onoff'){
				var $item=$(e.target).closest('#xlvid_item').toggleClass('checked');
				if($item.hasClass('checked')) $item.data('node').setAttribute('multiple','true');
				else {
					$item.data('node').removeAttribute('multiple');
					var $lis=$item.closest('#pxml_box').find('li#xlvid_item'),got=false;
					for(var i=0;i<$lis.length;++i){
						var $li=$($lis[i]);
						if($li.hasClass('item_node')){
							if(!got && $li.hasClass('checked'))got=true;
							else $li.removeClass('checked');
						}
					}
				}
				return false;
			}
			if(id=='xlvid_input'||id=='xlvid_input_value'){
				e.stopPropagation();
				return false;
			}
			if(id=='xlvid_name'){
				var $item=$(e.target).closest('#xlvid_item');
				if($item.hasClass('add_item')){
					if($item.hasClass('select_node')){
						e.stopPropagation();
						this.addOption($item);
					}
					if($item.hasClass('group')){
						e.stopPropagation();
						var node=this.addSelectXml($item);
						$item.before(this._createLINode(null,node));
						//return this._createBox(node);
						return;
					}
				}
			}
			return aPxml._click.apply(this,[].concat(e));
		},
		_commitBox:function($box){
			var pxml=$box.data('node').getAttribute('pxml');
			if(pxml=='select' && this._isWritable($box.data('node'))){
				var $lis=$box.find('li'),options='',values='',data='';
				for(var i=0;i<$lis.length;++i){
					var $li=$($lis[i]);
					if($li.hasClass('attr_name')){
						var $inp = $li.find('input').blur();
						if($inp.length>0) {
							var val=($inp.val()==$inp.data('blank'))?'':$inp.val();
							//if(val=='') {
							//	alert(this.options.inputNameString);
							//	return false;
							//}
							$li.data('node').setAttribute('name',val);
						}
					}
					if($li.hasClass('item_node')){
						var $inp1=$li.find('#xlvid_input').blur();
						var $inp2=$li.find('#xlvid_input_value').blur();
						$li.data('option', $inp1.val()==$inp1.data('blank')?'':($inp1.val()||''));
						$li.data('optval', $inp2.val()==$inp2.data('blank')?'':($inp2.val()||''));
						options+=((options!='')?',':'')+$li.data('option');
						values+=((values!='')?',':'')+$li.data('optval');
						if($li.hasClass('checked')) {
							data+=((data!='')?',':'')+$li.data('option');
							if($li.data('node').getAttribute('showValue')=='true')data+='('+$li.data('optval')+')';
						}
					}
				}
				$box.data('node').setAttribute('options',options);
				$box.data('node').setAttribute('values',values);
				xml$($box.data('node')).text('.',data);
			}
			return aPxml._commitBox.apply(this,[].concat($box));
		},
		_refreshBox:function($box){
			var $lis=$box.find('li');
			for(var i=0;i<$lis.length;++i){
				var $li=$($lis[i]);
				if(this._isWritable($li.data('node'))&& !$li.hasClass('add_item')){
					$li.find('#xlvid_name').html($li.data('node').getAttribute('name'));
					var $v=$li.find('#xlvid_value');
					if($v.length>0 && $v.css('display') != 'none')this._fixElementWidth($li,$v,42);
				}
			}
			return aPxml._refreshBox.apply(this,[].concat($box));
		},
		_completeCreateULGroup:function($gul){
			var node=$gul.data('node');
			if(node && this._isWritable(node)){
				if(node.getAttribute('addItem')=='false') return;
				var $li=$('<li id="xlvid_item"></li>').data('node',node).addClass('group add_item write').appendTo($gul);
				$('<div id="xlvid_name"></div>').html(this.options.addItemString).appendTo($li);
			}
		},
		nodeForBox:function(node,$box){
			if($box.data('node')==node)return;
			var prolog=(node.getAttribute('itemProlog')||this.options.itemProlog);
			var epilog=(node.getAttribute('itemEpilog')||this.options.itemEpilog);
			node.setAttribute('prolog',prolog);
			node.setAttribute('epilog',epilog);
			aPxml.nodeForBox.apply(this,[].concat(node,$box));
			if(this._isWritable(node)) this._createItemAttrGroup($box,node);
		},
		_createItemAttrGroup:function($box,node){
			if(this._isWritable(node)){
				node.setAttribute('prolog',(node.getAttribute('optionNameProlog')||this.options.optionNameProlog));
				node.setAttribute('epilog',(node.getAttribute('optionNameEpilog')||this.options.optionNameEpilog));
				var $ul=this._createULGroup($box,node,true);
				var $li=$('<li id="xlvid_item" class="node_attr attr_name write"></li>').data('node',node).appendTo($ul);
				var $input=$('<input id="xlvid_input"></input>').val(node.getAttribute('name')).data({blank:this.options.optionNameBlank}).appendTo($li);
				$input.focus(function(){
					if($input.val()==$input.data('blank'))$input.val('');
					$input.removeClass('blank').addClass('editing');
				});
				$input.blur(function(){
					$input.removeClass('editing');
					if($input.val().length==0)$input.addClass('blank').val($input.data('blank'));
				});
				$input.blur();
				if(node.getAttribute('nameReadOnly')=='true') $input.attr('readonly', 'true');
				$('<img id="xlvid_input_button" src="/xlive/images/pxml/input_img.png"></img>').appendTo($li);
				$input.css('width',($li.width()-52)+'px');
				// multiple
				var $li2=$('<li id="xlvid_item" class="node_attr attr_multiple write"></li>').data('node',node).appendTo($ul);
				$('<div id="xlvid_name"></div>').html(this.options.multipleString).appendTo($li2);
				$('<div id="xlvid_multiple_onoff"></div>').appendTo($li2);
				if(node.getAttribute('multiple')=='true')$li2.addClass('checked');
			}
		},
		_isWritable:function(node){
			return xml$(node).node('ancestor-or-self::*[@writable=\'true\']')?true:false;
		},
		_createLINode:function($ul,node){
			var $li = aPxml._createLINode.apply(this,[].concat($ul,node));
			if(this._isWritable(node)){
				$li.addClass('write');
				$remove=$('<img id="xlvid_remove" src="/xlive/images/pxml/minus.png"></img>');
				$($li[0].firstChild).before($remove);
				$li.find('#xlvid_advance').attr('src','/xlive/images/pxml/write_advance.png');
				var $v=$li.find('#xlvid_value');
				if($v.length>0 && $v.css('display') != 'none') this._fixElementWidth($li,$v,54);
				if(node.getAttribute('removable')=='false')$remove.hide();
			}
			return $li;
		},
		_selectNode:function($ul,node){
			if(!this._isWritable(node))return aPxml._selectNode.apply(this,[].concat($ul,node));
			var options=node.getAttribute('options').split(',');
			var values=(''+(node.getAttribute('values')||node.getAttribute('options'))).split(',');
			var showvalue=(node.getAttribute('showValue')=='true'),data=xml$(node).text();
			for(var i=0; i< options.length;++i){
				if(options[i]=='') continue;
				var $li=$('<li id="xlvid_item" class="select_node item_node write"></li>').data({'node':node,option:(options[i]||''),optval:(values[i]||'')}).appendTo($ul);
				$('<img id="xlvid_remove" src="/xlive/images/pxml/minus.png"></img>').appendTo($li);
				$('<div id="xlvid_name"></div>').html((options[i]||'')).hide().appendTo($li);
				var $inp1=$('<input id="xlvid_input"></input>').val((options[i]||'')).data('blank',this.options.itemNameBlank).appendTo($li);
				$inp1.focus(function(){
					if($inp1.val()==$inp1.data('blank'))$inp1.val('');
					$inp1.removeClass('blank').addClass('editing');
				}).blur(function(){
					$inp1.removeClass('editing');
					if($inp1.val().length==0) $inp1.addClass('blank').val($inp1.data('blank'));
				}).blur();
				$('<img id="xlvid_checked" src="/xlive/images/pxml/checked.png"></img>').appendTo($li);
				$('<input id="xlvid_value"></input>').val((values[i]||'')).hide().appendTo($li);
				
				var $inp2=$('<input id="xlvid_input_value"></input>').val((values[i]||'')).data('blank',this.options.itemValueBlank).appendTo($li);
				$inp2.focus(function(){
					if($inp2.val()==$inp2.data('blank'))$inp2.val('');
					$inp2.removeClass('blank').addClass('editing');
				}).blur(function(){
					$inp2.removeClass('editing');
					if((typeof $inp2.val())=='undefined' || $inp2.val().length==0) $inp2.addClass('blank').val($inp2.data('blank'));
				}).blur();
				var w=($li.width()-24*2-40)/2;
				$inp1.css('width',w+'px');
				$inp2.css('width',w+'px');
				if(!showvalue) {
					$inp2.hide();
					$inp1.css('width',(w*2)+'px');
				}
				if(($li.data('option')!='' && data.indexOf($li.data('option'))>=0)) $li.addClass('checked');
			}
			if(node.getAttribute('addOption')=='false') return;
			var $li2=$('<li id="xlvid_item" class="select_node add_item write"></li>').data('node',node).appendTo($ul);
			$('<div id="xlvid_name"></div>').html(this.options.addOptionString).appendTo($li2);
		},
		addOption:function($addli){
			var node=$addli.data('node'),showvalue=(node.getAttribute('showValue')=='true'),data=xml$(node).text();
			var $li=$('<li id="xlvid_item" class="select_node item_node write"></li>').data({'node':node,option:'',value:''});
			$addli.before($li);
			$('<img id="xlvid_remove" src="/xlive/images/pxml/minus.png"></img>').appendTo($li);
			$('<div id="xlvid_name"></div>').html('').hide().appendTo($li);
			var $inp1=$('<input id="xlvid_input"></input>').data('blank',this.options.itemNameBlank).val('').appendTo($li);
			$inp1.focus(function(){
				if($inp1.val()==$inp1.data('blank'))$inp1.val('');
				$inp1.removeClass('blank').addClass('editing');
			}).blur(function(){
				$inp1.removeClass('editing');
				if($inp1.val().length==0)$inp1.addClass('blank').val($inp1.data('blank'));
			}).blur();
			$('<img id="xlvid_checked" src="/xlive/images/pxml/checked.png"></img>').appendTo($li);
			$('<input id="xlvid_value"></input>').val('').hide().appendTo($li);
			var $inp2=$('<input id="xlvid_input_value"></input>').data('blank',this.options.itemValueBlank).val('').appendTo($li);
			$inp2.focus(function(){
				if($inp2.val()==$inp2.data('blank'))$inp2.val('');
				$inp2.removeClass('blank').addClass('editing');
			}).blur(function(){
				$inp2.removeClass('editing');
				if($inp2.val().length==0)$inp2.addClass('blank').val($inp2.data('blank'));
			}).blur();
			var w=($li.width()-24*2-40)/2;
			$inp1.css('width',w+'px');
			$inp2.css('width',w+'px');
			if(!showvalue) {
				$inp2.hide();
				$inp1.css('width',(w*2)+'px');
			}
		},
		_addSelectXml:function($gli){
			var node=$gli.data('node');
			var s=node.ownerDocument.createElement('i');
			s.setAttribute('pxml','select');
			s.setAttribute('name','');
			s.setAttribute('options','');
			s.setAttribute('values','');
			s.setAttribute('showValue','true');
			node.appendChild(s);
			return s;
		},
		addSelectXml:function($gli){
			return _addSelectXml($gli);
		}
};


(function($) {
	if(typeof Object.create !== 'function'){
	    Object.create=function(o){
	        function F() {}
	        F.prototype = o;
	        return new F();
	    };
	};
	$.plugin = function(name,object) {
		  $.fn[name] = function(options){
			var args=Array.prototype.slice.call(arguments,1);
		    return this.each(function(){
		      var inst=$.data(this,name);
		      if(!inst)$.data(this,name,inst=Object.create(object).init(this,(typeof options === 'object')?options:{}));
		      if(typeof options === 'string' && inst[options])return inst[options].apply(inst,args);
		    });
		  };
		};
})(jQuery);
	
$.plugin('aScroller',aScroller);
$.plugin('aImg',aImg);
$.plugin('aCalendar',aCalendar);
$.plugin('aTab',aTab);
$.plugin('aTab2',aTab2);
$.plugin('aPxml',aPxml);
$.plugin('aWxml',aWxml);


window.a$=a$;
window.xml$=xml$;
window.dm$=dm$;
window.aFB=aFB;
window.aGoogle=aGoogle;
window.aChannel=aChannel;

})(window);


(function($) {
	var types = ['DOMMouseScroll', 'mousewheel'];
	if ($.event.fixHooks) {
	    for ( var i=types.length; i; ) {
	        $.event.fixHooks[ types[--i] ] = $.event.mouseHooks;
	    }
	}
	$.event.special.mousewheel = {
	    setup: function() {
	        if(this.addEventListener)for(var i=types.length; i;)this.addEventListener(types[--i], handler, false );
	        else this.onmousewheel = handler;
	    },
	    teardown: function() {
	        if(this.removeEventListener)for(var i=types.length; i;)this.removeEventListener( types[--i], handler, false );
	        else this.onmousewheel = null;
	    }
	};
	$.fn.extend({
	    mousewheel: function(fn) {
	        return fn ? this.bind("mousewheel",fn):this.trigger("mousewheel");
	    },
	    unmousewheel: function(fn) {
	        return this.unbind("mousewheel", fn);
	    }
	});
	function handler(event) {
	    var orgEvent = event || window.event, args = [].slice.call( arguments, 1 ),delta = 0,deltaX=0,deltaY=0;
	    event=$.event.fix(orgEvent);
	    event.type="mousewheel";
	    if(orgEvent.wheelDelta){delta=orgEvent.wheelDelta/120;}
	    if(orgEvent.detail){delta=-orgEvent.detail/3;}
	    deltaY=delta;
	    if(orgEvent.axis !== undefined && orgEvent.axis === orgEvent.HORIZONTAL_AXIS ) {
	        deltaY = 0;
	        deltaX = -1*delta;
	    }
	    if(orgEvent.wheelDeltaY !== undefined){deltaY = orgEvent.wheelDeltaY/120; }
	    if(orgEvent.wheelDeltaX !== undefined){deltaX = -1*orgEvent.wheelDeltaX/120; }
	    args.unshift(event,delta,deltaX,deltaY);
	    return ($.event.dispatch||$.event.handle).apply(this, args);
	}
	})(jQuery);

