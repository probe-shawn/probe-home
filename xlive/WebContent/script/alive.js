
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
	var a$={
			browser:{
				msie: navigator.userAgent.indexOf('MSIE') > -1,
				/*
				ie10: (a$.browser.msie && document.documentMode==10),
				ie9:  (!a$.browser.ie10 && a$.browser.msie && document.documentMode==9),
				ie8:  (!a$.browser.ie9 && a$.browser.msie && window.postMessage),
				ie7:  (a$.browser.msie && navigator.userAgent.indexOf('MSIE 7.') > -1),
				*/
				mobilesafari:/Apple.*Mobile/.test(navigator.userAgent),
				safari:navigator.userAgent.indexOf('Safari') > -1 && navigator.userAgent.indexOf('Chrome') < 0,
			},
			css:{
				prefix:function(css){
					return (Modernizr.prefixed(css)||css).replace(/([A-Z])/g, function(str,m1){ return '-' + m1.toLowerCase(); }).replace(/^ms-/,'-ms-');
				},		
				transitionend:
					{'WebkitTransition' : 'webkitTransitionEnd',
				    'MozTransition'    : 'transitionend',
				    'OTransition'      : 'oTransitionEnd',
				    'msTransition'     : 'MSTransitionEnd',
				    'transition'       : 'transitionend'
					}[Modernizr.prefixed('transition')],
				transition:function(object,css,cb,ms){
					ms=(ms||300);
					if($.isFunction(cb)) object.animate(css,ms,cb);
					else object.animate(css,ms);			
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
				var w=$(window).width(), $b=$('body'),size = (w <= 480) ? 'ui-size-s' : (w < 960) ? 'ui-size-m' : 'ui-size-l';
				if(!$b.hasClass(size)) $b.removeClass('ui-size-s').removeClass('ui-size-m').removeClass('ui-size-l').addClass(size);
				return size;
		    },
			fbNJPG:function(url,def){
				var vn=(url && url.length>0)?url.split(';')[0]:def;
	 			if(vn && vn.startsWith('http') && vn.endsWith('_s.jpg'))vn=vn.replace('_s.jpg','_n.jpg');
	 			return vn;
			},
			random:function(from,to){
				return Math.floor(Math.random() * (to - from + 1) + from);
			},
			qString:function(){
				return (function(a) {
				    if (a == "") return {};
				    var b = {};
				    for (var i = 0; i < a.length; ++i){
				        var p=a[i].split('=');
				        if (p.length != 2) continue;
				        b[p[0]] = decodeURIComponent(p[1].replace(/\+/g, " "));
				    }
				    return b;
				})(window.location.search.substr(1).split('&'));
			}
		};
	
var _resize=null;
$(window).bind('resize',function(){
	if(_resize) clearTimeout(_resize);
	_resize = setTimeout(function(){a$.pageSize();},200);
});

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
			if(arguments.length==1||arguments.length==0) return (n)?((a$.browser.msie)?n.text:n.textContent) :'';
			//if(arguments.length==1||arguments.length==0) return (n)?((!window.DOMParser)?n.text:n.textContent) :'';
			text=(typeof text =='undefined')?'':text;
			if(n)n[a$.browser.msie?'text':'textContent']=text;
			//if(n)n[(!window.DOMParser)?'text':'textContent']=text;
			else{
				var xps=xpath.split('/'),xp=xps.pop(),cs=null,c=null,attr=(xp && xp.startsWith('@'))?xp.substring(1):null;
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
						else  c[(a$.browser.msie ? 'text':'textContent')]=((text===null)?'':text);
						//else  c[((!window.DOMParser) ? 'text':'textContent')]=((text===null)?'':text);
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
			return (a$.browser.msie)?this.root.xml:new XMLSerializer().serializeToString(this.root);
			//return (!window.DOMParser) ?this.root.xml:new XMLSerializer().serializeToString(this.root);
		},
		adopt:function(node){
			return (a$.browser.msie)? node:this.root.ownerDocument.adoptNode(node);
			//return (!window.DOMParser)? node:this.root.ownerDocument.adoptNode(node);
		},
		parse:function(str){
			if(a$.browser.msie){
				var dom=new ActiveXObject('Msxml2.DOMDocument');
				dom.async=false;
				dom.loadXML(str);
				dom.setProperty("SelectionLanguage", "XPath");
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
			//jq1.9--pasrseXML--bug
			if(a$.browser.msie){
				setting.converters={"* text": window.String, "text html": true, "text json": jQuery.parseJSON, "text xml": window.String};
			}
			setting.success=function(data,textStatus,jqXHR){
				if(a$.browser.msie){
					data=_xml.parse(data);
					//data.setProperty('SelectionLanguage','XPath');
				}
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
	
$.plugin('aImg',aImg);


window.a$=a$;
window.xml$=xml$;
window.dm$=dm$;
window.aFB=aFB;
window.aGoogle=aGoogle;
window.aChannel=aChannel;

})(window);




(function( $, window, undefined ) {
	var supportTouch = $.mobile.support.touch,
		touchStartEvent = supportTouch ? "touchstart" : "mousedown",
		touchStopEvent = supportTouch ? "touchend" : "mouseup",
		touchMoveEvent = supportTouch ? "touchmove" : "mousemove";
	var PLUGIN_NS = 'aSwipe';
	var defaults = {
		startPreventDefault:true,
		fireClickEvent:true,
		scrollSupressionThreshold: 0, 
		durationThreshold: 1000, 
		horizontalDistanceThreshold: 30,  
		verticalDistanceThreshold: 75,
		swipeStatus:null
	};
	$.fn.aSwipe = function (method) {
		var $this = $(this),
			plugin = $this.data(PLUGIN_NS);
		if (plugin && typeof method === 'string') {
			if (plugin[method]) {
				 return plugin[method].apply(this, Array.prototype.slice.call(arguments, 1));
			}else $.error('Method ' + method + ' does not exist on jQuery.aSwipe');
		}else if (!plugin && (typeof method === 'object' || !method)) {
			return init.apply(this, arguments);
		}
		return $this;
	};
	$.fn.aSwipe.defaults = defaults;
	function init(options) {
		if (!options) options = {};
		options = $.extend({}, $.fn.aSwipe.defaults, options);
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
		var $element = $(element),start=undefined,stop=undefined,started=true;
		$element.bind(touchStartEvent, function(event) {
				if(start && stop && options.swipeStatus && started !==false) {
					options.swipeStatus(event,"end",$.extend({},start),$.extend({},stop));
					start = stop = undefined;
				}
				started=true;
				var data = event.originalEvent.touches ? event.originalEvent.touches[0] : event;
				start = {
						time: (new Date()).getTime(),
						x:data.pageX,y:data.pageY,
						coords: [data.pageX, data.pageY],
						origin: $(event.target)
					};
				stop=undefined;
				if(options.swipeStatus) started=options.swipeStatus(event,"start",start,(stop||start));
				function moveHandler(event) {
					if(!start) return;
					if(started===false){
						if(options.swipeStatus) started=options.swipeStatus(event,"start",start,(stop||start));
						if(started===false) return;
					}
					var data = event.originalEvent.touches ? event.originalEvent.touches[0] : event;
					stop = {
						time: (new Date()).getTime(),
						x:data.pageX,y:data.pageY,
						coords:[data.pageX, data.pageY]
					};
					if(options.swipeStatus) options.swipeStatus(event,"move",start,stop);
					if(Math.abs(start.coords[0]-stop.coords[0]) > options.scrollSupressionThreshold ) {
						event.preventDefault();
						event.stopPropagation();
						return false;
					}
				}
				$element.bind(touchMoveEvent, moveHandler).one(touchStopEvent, function(event) {
						$element.unbind(touchMoveEvent, moveHandler);
						if(start && stop) {
							if(stop.time - start.time < options.durationThreshold &&
								Math.abs(start.coords[0] - stop.coords[0]) > options.horizontalDistanceThreshold &&
								Math.abs(start.coords[1] - stop.coords[1]) < options.verticalDistanceThreshold) {
							}
							if(options.swipeStatus) options.swipeStatus(event,"end",$.extend({},start),$.extend({},stop));
						}
						if(start && !stop){
							if(options.swipeStatus) options.swipeStatus(event,"cancel",$.extend({},start),$.extend({},start));
							if(options.fireClickEvent) start.origin.trigger("swipeclick",event);
						}
						start = stop = undefined;
					});
				if(options.startPreventDefault){
					event.preventDefault();
					event.stopPropagation();
					return false;
				}
			});
	};
})( jQuery, this );




(function() {
	  // Private array of chars to use
	  var CHARS = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');

	  Math.uuid = function (len, radix) {
	    var chars = CHARS, uuid = [];
	    radix = radix || chars.length;

	    if (len) {
	      // Compact form
	      for (var i = 0; i < len; i++) uuid[i] = chars[0 | Math.random()*radix];
	    } else {
	      // rfc4122, version 4 form
	      var r;

	      // rfc4122 requires these characters
	      uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
	      uuid[14] = '4';

	      // Fill in random data.  At i==19 set the high bits of clock sequence as
	      // per rfc4122, sec. 4.1.5
	      for (var i = 0; i < 36; i++) {
	        if (!uuid[i]) {
	          r = 0 | Math.random()*16;
	          uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
	        }
	      }
	    }

	    return uuid.join('');
	  };

	  // A more performant, but slightly bulkier, RFC4122v4 solution.  We boost performance
	  // by minimizing calls to random()
	  Math.uuidFast = function() {
	    var chars = CHARS, uuid = new Array(36), rnd=0, r;
	    for (var i = 0; i < 36; i++) {
	      if (i==8 || i==13 ||  i==18 || i==23) {
	        uuid[i] = '-';
	      } else if (i==14) {
	        uuid[i] = '4';
	      } else {
	        if (rnd <= 0x02) rnd = 0x2000000 + (Math.random()*0x1000000)|0;
	        r = rnd & 0xf;
	        rnd = rnd >> 4;
	        uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
	      }
	    }
	    return uuid.join('');
	  };

	  // A more compact, but less performant, RFC4122v4 solution:
	  Math.uuidCompact = function() {
	    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
	      var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
	      return v.toString(16);
	    }).toUpperCase();
	  };
	  
	  Math.uuidPos=function(){
		  return Math.uuid(8, 60);
	  };
	})();




