(function(){
	Number.prototype.addComma = function() {
		var number = ''+this+'';
	    var x = number.split('.');
	    var x1 = x[0];
	    var x2 = x.length > 1 ? '.' + x[1] : '';
	    var rgx = /(\d+)(\d{3})/;
	    while (rgx.test(x1)) {
	        x1 = x1.replace(rgx, '$1' + ',' + '$2');
	    }
	    return x1 + x2;
	};
	Array.prototype.insert = function (index, item) {
		 return this.splice(index, 0, item);
	};
	Array.prototype.remove = function(from, to) {
		 var rest = this.slice((to || from) + 1 || this.length);
		 this.length = from < 0 ? this.length + from : from;
		 return this.push.apply(this, rest);
	};
	Array.prototype.removeObject = function(obj) {
		 for(var i=0; i<this.length;i++){ 
			 if(this[i]==obj) return this.splice(i,1); 
		  }
	};
	Array.prototype.makeArray=function(obj){
		for(var k in obj){
			this.push(obj[k]);
		}
		return this;
	};
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
		//try{
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
		//}catch(e){
		//	alert('parseDate error :'+e);	
		//}
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
	var cssPrefix = (function () {
	  var styles = window.getComputedStyle(document.documentElement, ''),
	    pre = (Array.prototype.slice
	      .call(styles)
	      .join('') 
	      .match(/-(moz|webkit|ms)-/) || (styles.OLink === '' && ['', 'o'])
	    )[1],
	    dom = ('WebKit|Moz|MS|O').match(new RegExp('(' + pre + ')', 'i'))[1];
	  return {
	    dom: dom,
	    lowercase: pre,
	    css: '-' + pre + '-',
	    js: pre[0].toUpperCase() + pre.substr(1)
	  };
	})();
	var a$={
			browser:{
				ios : /(iPad|iPhone|iPod)/g.test( navigator.userAgent ),
				msie: navigator.userAgent.indexOf('MSIE') > -1,
				ie: navigator.userAgent.indexOf('Trident') > -1,
				ff: navigator.userAgent.toLowerCase().indexOf('firefox') > -1,
				/*
				ie10: (a$.browser.msie && document.documentMode==10),
				ie9:  (!a$.browser.ie10 && a$.browser.msie && document.documentMode==9),
				ie8:  (!a$.browser.ie9 && a$.browser.msie && window.postMessage),
				ie7:  (a$.browser.msie && navigator.userAgent.indexOf('MSIE 7.') > -1),
				*/
				mobilesafari:/Apple.*Mobile/.test(navigator.userAgent),
				safari:navigator.userAgent.indexOf('Safari') > -1 && navigator.userAgent.indexOf('Chrome') < 0,
				android:navigator.userAgent.indexOf('Android') > -1,
				androidSDK:navigator.userAgent.indexOf('Android') > -1 && navigator.userAgent.indexOf('Chrome') < 0,
				androidVersion: parseFloat(navigator.userAgent.slice(navigator.userAgent.indexOf("Android")+8))
			},
			cssPrefix:cssPrefix
			,
			css:{
				prefix:function(css){
					return cssPrefix.css+css;
				},		
				transitionend:
					{'webkitTransition' : 'webkitTransitionEnd',
				    'mozTransition'    : 'transitionend',
				    'oTransition'      : 'oTransitionEnd',
				    'msTransition'     : 'transitionend',//'MSTransitionEnd',
				    'Transition'       : 'transitionend'
					}[cssPrefix.lowercase+'Transition'],
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
				var _self=this;
				setTimeout(function(){
					$e.bind(_self.css.transitionend,function(e){
						e.stopPropagation();
						$(this).unbind(_self.css.transitionend).removeClass(class_name);
						if(cb)cb();
					}).stop().addClass(class_name).css(p);
				},10);
			},
			setTransition:function($e,class_name,p,cb){
				var _self=this;
				setTimeout(function(){
					$e.bind(_self.css.transitionend,function(e){
						e.stopPropagation();
						$(this).unbind(_self.css.transitionend);
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
				if(!$b.hasClass(size)) $b.removeClass('ui-size-s ui-size-m ui-size-l').addClass(size);
				if(this.slimMode()) $b.addClass('ui-slim-mode');
				else $b.removeClass('ui-slim-mode');
				$b[(w <= 680)?'addClass':'removeClass']('ui-size-sm');
				return size;
		    },
		    slimMode:function(){
		    	return $(window).height()/$(window).width() >= 1.2;
		    },
		    fbNJPG:function(url,def){
				if(url && url['url']) url=url['url'];
				var vn=(url && url.length>0)?url.split(';')[0]:def;
	 			if(vn && vn.startsWith('http') && vn.endsWith('_s.jpg'))vn=vn.replace('_s.jpg','_n.jpg');
	 			return vn;
			},
			icon:function(gcs,options,cb){
				gcs=(gcs||{});
				if(gcs && gcs.url && gcs.url.length>0) return this.fbIcon(gcs,options);
				var opts=$.extend(true, {element:null,w:0,h:0,default_icon:null,'background-size':'cover','background-position':'center','background-repeat':'no-repeat'}, options||{});
				if(opts.element)opts.element=$(opts.element);
				if(opts.w==0 && opts.element) opts.w= opts.element.width();
				if(opts.h==0 && opts.element) opts.h= opts.element.height();
				var s=Math.round(Math.max(opts.w||0,opts.h||0));
				if(s>0) {
					if(gcs.w || gcs.h) s='=s'+Math.ceil(s*Math.max(gcs.w||0,gcs.h||0)/Math.min(gcs.w||1,gcs.h||1)/50)*50;
					else s='=s'+s; 
				}else s='';
				var url = (gcs.u && gcs.u.length>0)?gcs.u+s:(gcs.b && gcs.o)?aGCS.downLoadUrl(gcs.b,gcs.o):opts.default_icon;
				if(opts.element){
					opts.element.data('gcs',gcs);
					if(url){
						opts.element.css({'background-image':'url('+url+')','background-size':opts['background-size'],'background-position':opts['background-position'],'background-repeat':opts['background-repeat']});
						this.iconSizePosi(opts.element,url,{'background-size':opts['background-size'],'background-position':opts['background-position']},cb);
					}else{
						opts.element.removeClass('ai-photo').css('background-image','none');
					}
				}
				return url;
			},
			iconSizePosi:function($e,imgurl,acss,cb){
				var _image=new Image(), css=(acss||{});
				$e.removeClass('ai-photo');
				_image.onload=function(){
					$e.attr('data-w',_image.width).attr('data-h',_image.height).addClass('ai-photo');
					$e.find('input').attr('data-w',_image.width).attr('data-h',_image.height);
					if(_image.width < $e.width() || _image.height < $e.height()){
						 $e.css('background-size',(css['background-size']||'auto'));
						 $e.css('background-position',(css['background-position']||'center'));
					} else $e.css('background-size',(css['background-size']||'cover'));
					if(cb) return cb();
				};
				_image.src=imgurl;
			},
			fbIcon:function(icon_obj,options){
				//options tsan,w,h,element,default_icon
				var default_icon='/xlive/images/null.gif';
				var result='',$el=null;
				if(options && options.default_icon)default_icon=options.default_icon;
				if(options && options.element)$el=$(options.element);
				if(icon_obj && icon_obj.url && icon_obj.url.length>0){
					var url=icon_obj.url;
					if(url.startsWith('http') && url.endsWith('_s.jpg')){
						if(options){
							if(options.tsan && options.tsan.length==1 && "tsan".indexOf(options.tsan)>=0)
								result=url.replace("_s.jpg","_"+options.tsan+".jpg");
							else 
								if(options.w || options.h || options.element){
									var big_side=0;
									var filename=url.substring(url.lastIndexOf("/"),url.lastIndexOf("_s.jpg"));									
									if(options.w && options.w>big_side) big_side=options.w;
									if(options.h && options.h>big_side) big_side=options.h;
									if($el && $el.width()>big_side) big_side=$el.width();
									if($el && $el.height()>big_side) big_side=$el.height();
									if(big_side<=75)result=url.replace('_s.jpg','_t.jpg');
									else if(big_side<=130)result=url;
									else if(big_side<=180)result=url.replace('_s.jpg','_a.jpg');									
									else if(big_side<=320)result=url.replace(filename+"_s.jpg","/s320x320"+filename+"_n.jpg");
									else if(big_side<=480)result=url.replace(filename+"_s.jpg","/s480x480"+filename+"_n.jpg");									
									else if(big_side<=600)result=url.replace(filename+"_s.jpg","/s600x600"+filename+"_n.jpg");
									else if(big_side<=720)result=url.replace(filename+"_s.jpg","/s720x720"+filename+"_n.jpg");
									else result=url.replace('_s.jpg','_n.jpg');
								}else result=url;
						}else{
							if($(document.body).hasClass('ui-size-s'))result=url;
							else result=url.replace('_s.jpg','_n.jpg');
						}
					}else result=url;					
				}else result=default_icon;
				
				if($el!=null){
					if(result.length==0) $el.css({"background-image":"none"});
					else{
						$el.css({'background-image':'url(/xlive/images/load.gif)','background-size':'auto','background-position':'center','background-repeat':'no-repeat'});
						$('<img/>').attr('src', result).load(function() {
							if(icon_obj.w>0 && icon_obj.h>0 && $el.width()>icon_obj.w && $el.height()>icon_obj.h)
								$el.css({'background-image':'url('+result+')','background-size':'auto','background-position':'center','background-repeat':'no-repeat'});
							else						
								$el.css({'background-image':'url('+result+')','background-size':'cover','background-position':'center'});  
						});							
					}
				}
				return result;
			},
			random:function(from,to){
				return Math.floor(Math.random() * (to - from + 1) + from);
			},
			qString:function(){
				return (function(a) {
					var idx=-1;
				    if(a == "") {
				    	idx = window.location.href.indexOf('?');
				    	if(idx < 0) return {};
				    	a=window.location.href.substring(idx+1).split('&');
				    }
				    var b={};
				    for (var i = 0; i < a.length; ++i){
				        var p=a[i].split('='),k=p[0],v=p[1];
				        if (p.length < 2) continue;
				        b[k] = decodeURIComponent(v.replace(/\+/g, " "));
				        if(v.indexOf('?')>=0) {
				        	for(var s=2;s<p.length;++s) b[k] += '='+ decodeURIComponent(p[s].replace(/\+/g, " "));
				        	for(++i;i<a.length;++i) b[k] +='&'+a[i];
				        	break;
				        } 
				    }
				    return b;
				})(window.location.search.substr(1).split('&'));
			},
			ObjectEquals:function(x,y){
				  var p=null;
				  for(p in y) {
				      if(typeof(x[p])=='undefined') {return false;}
				  }
				  for(p in y) {
				      if (y[p]) {
				          switch(typeof(y[p])) {
				              case 'object':
				                  if (!this.ObjectEquals(y[p],x[p])) {return false; } break;
				              case 'function':
				                  if (typeof(x[p])=='undefined' ||(p != 'equals' && y[p].toString() != x[p].toString())){return false;}
				                  break;
				              default :
				                  if (y[p] != x[p]) {return false; }
				          }
				      } else {
				          if (x[p]) {return false;}
				      }
				  }
				  for(p in x) {
				      if(typeof(y[p])=='undefined') {return false;}
				  }
				  return true;
			},
			getSelectRange:function(el) {
			    var start = 0, end = 0, normalizedValue, range,textInputRange, len, endRange;
			    if(typeof el.selectionStart == "number" && typeof el.selectionEnd == "number") {
			        start = el.selectionStart;
			        end = el.selectionEnd;
			    }else{
			        range = document.selection.createRange();
			        if (range && range.parentElement() == el) {
			            len = el.value.length;
			            normalizedValue = el.value.replace(/\r\n/g, "\n");
			            textInputRange = el.createTextRange();
			            textInputRange.moveToBookmark(range.getBookmark());
			            endRange = el.createTextRange();
			            endRange.collapse(false);
			            if (textInputRange.compareEndPoints("StartToEnd", endRange) > -1) {
			                start = end = len;
			            } else {
			                start = -textInputRange.moveStart("character", -len);
			                start += normalizedValue.slice(0, start).split("\n").length - 1;
			                if(textInputRange.compareEndPoints("EndToEnd", endRange) > -1) {
			                    end = len;
			                } else {
			                    end = -textInputRange.moveEnd("character", -len);
			                    end += normalizedValue.slice(0, end).split("\n").length - 1;
			                }
			            }
			        }
			    }
			    return {'start': start,'end': end};
			},
			selectRange:function(el, start, end) {
				if(el.setSelectionRange){
					el.focus();
					el.setSelectionRange(start, end);
				}else if(el.createTextRange){
		            var range = el.createTextRange();
		            range.collapse(true);
		            range.moveEnd('character', end);
		            range.moveStart('character', start);
				}
			},
			inputSelectAll:function($one){
				if(this.browser.ios) {
					$one[0].selectionStart = 0;
					$one[0].selectionEnd = 9999;
				}else{
					$one.select();
				}
			}
			/////
		};
	
		$(window).bind('resize',function(){
			a$.pageSize();
		});
/*
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
				$.extend(true,this.options,options);
				if(this.options.url && this.options.method && this.options.arg && !this.options['_'+options.method])this.request(this.options);
			}
			return this;
		},
		request:function(options){
			if(options)$.extend(true,this.options,options);
			var _self=this,os=this.options,url=os.url,arg=os.arg,method=os.method,result='_'+method;
			this.options[result] = null;
			url=url.startsWith('/xlive')?url:'/xlive/web/'+url;
			var setting={'url':url,type:'POST',contentType:'multipart/form-data',headers:{'X-XLive-Version':'alive.1.0','X-XLive-Content':'xml','xsessionid':'8788'}};
			var $arg=xml$(arg),am=$arg.node(method);
			var x=xml$('<xlive></xlive>').text('method','').text('method/@name',method).text('method/arguments','').text('method/arguments/'+method,'');
			var m=x.node('method/arguments/'+method);
			var cs=((am)?am:$arg.node()).childNodes;
			for(var i=0;i<cs.length;++i)m.appendChild(cs[i].cloneNode(true));
			setting.data=x.toString();
			if(aChannel)setting.data=setting.data.replace('</'+method+'>','<client-id>'+aChannel.getChannelId()+'</client-id>'+'</'+method+'>');
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
				_self.options[result]=ret;
				if(_self.options.callback)_self.options.callback(ret);
				dfd.resolve(ret);
				if(_self.options[result+'_DFD']){
					for(var d=0;d<_self.options[result+'_DFD'].length;++d){
						_self.options[result+'_DFD'][d].notify(ret);
					}
				}
				if(_self.options[result+'_dfd']){
					for(var d=0;d<_self.options[result+'_dfd'].length;++d){
						_self.options[result+'_dfd'][d].resolve(ret);
					}
				}
				_self.options[result+'_dfd']=null;
			};
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
	function F(){};
	F.prototype = _dm;
	this._dms=(this._dms||{});
	if(name && typeof name == 'string') {
		if(!this._dms[name]) this._dms[name]=new F().init(name,options);
		return this._dms[name];
	}
	return new F().init(name,options);
};
*/

var _jso = {
		init:function(name,options){
			this.options=(this.options||{_name:name});
			if(options){
				$.extend(true,this.options,options);
				if(this.options.url && this.options.method && this.options.arg && !this.options['_'+options.method])this.request(this.options);
			}
			return this;
		},
		request:function(options){
			if(options)$.extend(true,this.options,options);
			var os=this.options,url=os.url,arg=os.arg,method=os.method,result='_'+method;
			this.options[result] = null;
			url=url.startsWith('/xlive')?url:'/xlive/jso/'+url;
			var setting={'url':url,type:'POST',contentType:'multipart/form-data',headers:{'X-Content':'json','xsessionid':'8788dy'}};
			var data={'method':method,'arg':arg};
			if(aChannel)data.client_id=aChannel.getChannelId();
			setting.data= JSON.stringify(data);
			var _self=this,dfd=$.Deferred();
			setting.success=function(data,textStatus,jqXHR){
				var ret=$.parseJSON(data);
				_self.options[result]=ret;
				if(_self.options.callback)_self.options.callback(ret);
				dfd.resolve(ret);
				if(_self.options[result+'_DFD']){
					for(var d=0;d<_self.options[result+'_DFD'].length;++d){
						_self.options[result+'_DFD'][d].notify(ret);
					}
				}
				if(_self.options[result+'_dfd']){
					for(var d=0;d<_self.options[result+'_dfd'].length;++d){
						_self.options[result+'_dfd'][d].resolve(ret);
					}
				}
				_self.options[result+'_dfd']=null;
			};
			$.ajax(setting);
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

function jso$(name,options){
	function J(){};
	J.prototype = _jso;
	this._jsos=(this._jsos||{});
	if(name && typeof name == 'string'){
		if(!this._jsos[name]) this._jsos[name]=new J().init(name,options);
		return this._jsos[name];
	}
	return new J().init(name,options);
}

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
		var _self=this,dfd=$.Deferred();
		if(this._apiLoaded(api,version))dfd.resolve();
		else{
			this.initial().done(function(){
				options=$.extend(true,{},options);
				options.callback=function(){_self.options.apiLoaded.push({'api':api,'version':version});dfd.resolve();};
				google.load(api,version,options);
			});
		}
		return dfd.promise();
	}
};
var aGCS={
	options:{
		projectNumber:'305077958359',
		clientId :'305077958359-7j18oshsfcion2e5hmjbiugh79ln7rln.apps.googleusercontent.com',
		apiKey : 'AIzaSyABsdg0PrSxDlqJiYESLwwjcNiL2_AXo60',
		scopes : 'https://www.googleapis.com/auth/devstorage.full_control',
		API_VERSION : 'v1',//'v1beta2',
		PROJECT : '305077958359',
		BUCKET : 'a-yup',
		object : "",
		GROUP :'group-0000000000000000000000000000000000000000000000000000000000000000',
		GROUP :'00b4903a97bfb935bb52a19107d41bc05c014ac376c05a6d6b7449919ce203c3',
		ENTITY : 'allUsers',//user-userId, user-email, group-groupId, group-email,allUsers, allAuthenticatedUsers
		ROLE : 'OWNER',//READER, OWNER
		ROLE_OBJECT :'READER'
	},
	__loadApi_dfd_array:[],
	_loadApi:function(){
		var dfd=$.Deferred(),_self=this;
		var loadapi = function(){
		    window.gapi.client.setApiKey(_self.options.apiKey);
		    window.setTimeout(function(){
		    	window.gapi.auth.authorize({client_id:_self.options.clientId,scope:_self.options.scopes,immediate:true},function(){
		  	      window.gapi.client.load('storage',_self.options.API_VERSION,function(){
					$.each(_self.__loadApi_dfd_array,function(i,v){
						v.resolve();
					});
					_self.__loadApi_dfd_array.length=0;
		  	      });
		    	});
		    }, 1);
		};
		_self.__loadApi_dfd_array.push(dfd);
		if(window.gapi && window.gapi.client && window.gapi.client.storage) {
			$.each(_self.__loadApi_dfd_array,function(i,v){
				v.resolve();
			});
			_self.__loadApi_dfd_array.length=0;
		} else if(window.gapi && window.gapi.client && !window.gapi.client.storage) {
			loadapi();
		} else {
			if(!window.handleClientJSLoad){
				window.handleClientJSLoad=function(){
					window.handleClientJSLoad=null;
					loadapi();
				};
				$.getScript('https://apis.google.com/js/client.js?onload=handleClientJSLoad');
			}
		}
		return dfd.promise();
	},
	api:function(){
		return this._loadApi();
	},
	listBuckets:function(project) {
		  var dfd=$.Deferred(),_self=this;
		  this._loadApi().done(function(){
		      window.gapi.client.storage.buckets.list({
		      	'project': project||_self.options.PROJECT,
		      	'projectId': project||_self.options.PROJECT
		        })
		        .execute(function(resp){
		        	dfd.resolve(resp);
		        });
		  });
		  return dfd.promise();
	},
	listObjects:function(bucket) {
		  var dfd=$.Deferred(),_self=this;
		  this._loadApi().done(function(){
		      window.gapi.client.storage.objects.list({
		    	  'bucket': bucket||_self.options.BUCKET
		        })
		        .execute(function(resp){
		        	dfd.resolve(resp);
		        });
		  });
		  return dfd.promise();
	},
    upload:function(container,bucket,prefix,servingurl){
        var dfd=$.Deferred(),_self=this;
        this._loadApi().done(function(){
            var dfds=[];
            container.find('input[type=file]').each(function(){
                var promise = _self._uploadFile($(this),bucket,prefix,servingurl);
                if(promise) dfds.push(promise);
            });
            $.when.apply(null, dfds).done(function(){dfd.resolve(arguments);});
        });
        return dfd.promise();
    },
    uploadInput:function($input,bucket,prefix,servingurl){
        var dfd=$.Deferred(),_self=this;
        this._loadApi().done(function(){
        	_self._uploadFile($input,bucket,prefix,servingurl).done(function(jso){
        		return dfd.resolve(jso);
        	});
        });
        return dfd.promise();
    },
    downLoadUrl:function(bucket,object){
        return 'http://storage.googleapis.com/'+bucket+'/'+encodeURI(object);
    },
    _android_uploadFile:function($input,bucket,prefix,servingurl){
    	var uri = $input.attr('data-uri');
    	if(!uri) return null;
    	var dfd=$.Deferred(),_self=this;
    	window.deviceInterface.uploadGCS(JSON.stringify({
    		'uri':uri,
    		'bucket':bucket,
    		'prefix':prefix,
    		'IDF':IDF(function(jso){
		    	var retjso={valid:jso.valid,why:jso.why,input:$input};
		    	var gcs={n:jso.name,b:bucket,o:jso.object,u:'',m:jso.mime_type};
		    	if($input){
		    		gcs.w=$input.attr('data-w');
		    		gcs.h=$input.attr('data-h');
		    	}
		    	if(jso.valid) $input.data('gcs',gcs);
		    	else alert('upload error :'+jso.why);
		    	if(!servingurl || !retjso.valid) return dfd.resolve(retjso);
		    	_self.getServingUrl(gcs.b,gcs.o).done(function(resp2){
		    		gcs.u=resp2;
		    		dfd.resolve(retjso);
		    	});
    		})
    	}));
    	return dfd.promise();
    },
    _uploadFile:function($input,bucket,prefix,servingurl) {
    	if(window.deviceInterface) return this._android_uploadFile($input,bucket,prefix,servingurl);
    	if(!$input.get(0) || !$input.get(0).files[0]) return null;
		var dfd=$.Deferred(),_self=this,fileData=$input.get(0).files[0];
      	const boundary = '-------314159265358979323846';
      	const delimiter = '\r\n--' + boundary + '\r\n';
      	const close_delim = '\r\n--' + boundary + '--';
      	var reader = new FileReader();
      	reader.readAsBinaryString(fileData);
      	reader.onload = function(e) {
      		var contentType = fileData.type || 'application/octet-stream';
      		var resource = {'group':_self.options.GROUP,'entity': _self.options.ENTITY, 'role': _self.options.ROLE };
      		var acl=[resource];
      		var name = (prefix||'')+fileData.name;
      		var metadata = {'name':name,'mimeType': contentType,'acl': acl};
      		var base64Data = btoa(reader.result);
      		var multipartRequestBody =
      			delimiter +'Content-Type: application/json\r\n\r\n' + JSON.stringify(metadata) + 
      			delimiter +'Content-Type: ' + contentType + '\r\n' +'Content-Transfer-Encoding: base64\r\n' +
      			'\r\n' + base64Data + close_delim;
      		var request = gapi.client.request({
      			'path': '/upload/storage/v1/b/' + (bucket||_self.options.BUCKET) + '/o',
      			'method': 'POST',
      			'params': {'uploadType': 'multipart'},
      			'headers': {'Content-Type': 'multipart/mixed; boundary="' + boundary + '"'},
      			'body': multipartRequestBody
      			});
      		try{
			    request.execute(function(resp){
			    	var retjso={valid:true,why:'',input:$input};
			    	var gcs={n:fileData.name,b:bucket,o:metadata.name,u:'',m:metadata.mimeType};
			    	gcs.w=$input.attr('data-w')||0;
			    	gcs.h=$input.attr('data-h')||0;
			    	if(resp.id) $input.data('gcs',gcs);
			    	else {
			    		retjso.valid=false;retjso.why=resp.error.message;
			    		alert('upload error :'+resp.error.message);
			    	}			    	
			    	if(!servingurl || !retjso.valid) return dfd.resolve(retjso);
			    	_self.getServingUrl(gcs.b,gcs.o).done(function(resp2){
			    		gcs.u=resp2;
			    		dfd.resolve(retjso);
			    	});
			    });
      		}catch(e) {
      			alert('File upload an error has occurred: ' + e.message);
      		}
      	}
      	return dfd.promise();
    },
    getServingUrl:function(bucket,object){
    	var dfd=$.Deferred();
		jso$().request({url:'img',method:'getServingUrl',arg:{'gcs_fname':bucket+'/'+object}}).done(function(jso){
			if(jso.valid)dfd.resolve(jso.url);
		});
    	return dfd.promise();
    }
};

var aFB={
	initialized:false,
	_requesting:false,
	options:{
		//appId:'181836915197998',
		appId:'449312651828567',
		fbConnetion:'//connect.facebook.net/zh_TW/all.js',
		perms:'publish_stream'
	},
	initial:function(options){
		this.options=$.extend(true,{},this.options,options);
		var dfd=$.Deferred(),_self=this;
		if(this.initialized) {
			dfd.resolve(this);
			return dfd.promise();
		}
		(_self.dfdArray=(_self.dfdArray||[])).push(dfd);
		if(!_self._requesting){
			window.fbAsyncInit = function(){
				try{
					FB.init({appId:_self.options.appId, status: true, cookie: true,xfbml: true, oauth: true});
				}catch(e){alert('facebook SDK failure :'+e);}
				_self.initialized=true;
				$.each(_self.dfdArray,function(i,v){
					v.resolve(_self);
				});
				_self.dfdArray=[];
				_self._requesting = false;
			};
		    (function(d, s, id){
		       var js, fjs = d.getElementsByTagName(s)[0];
		       if (d.getElementById(id)) {return;}
		       js = d.createElement(s); js.id = id;
		       js.src = _self.options.fbConnetion;
		       fjs.parentNode.insertBefore(js, fjs);
		     }(document, 'script', 'facebook-jssdk'));
		}
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
  		var _self=this;
  		if(resp && resp.authResponse)FB.api('/me/permissions',function(perm){_self._permission=perm;});
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
		options:{
			yid:'',
			name:'',
			type:''
		},
		def_msg:{
				target:{
					yid:'',
					name:'',
					type:'',
					self:true
				},
				data:{}
		},
		setup:function(){
			var dfd=$.Deferred();
			var script= document.createElement('script');
			script.type= 'text/javascript';
			script.onload = function(){dfd.resolve();};
			script.src= '/_ah/channel/jsapi';
			document.body.appendChild(script);
			return dfd.promise();
		},
		openChannel:function(){
			var _self=this;
			if(this.token && this.channel && this.socket) return this.closeOpenChannel();
			try{
				jso$().request({url:'channel',method:'getToken',arg:{'channel_id':this.channel_id}}).done(function(jso){
					_self.channel_id=jso.channel_id;
					_self.token=jso.token;
	  				_self._openChannel();
				});
			}catch(e){}
		},
		_openChannel:function(){
			var _self=this;
			try{
				this.channel = new goog.appengine.Channel(this.token);
				var on={onopen:function(){_self._opened();},onmessage:function(msg){_self._message(msg);},
						onerror:function(msg){_self._error(msg);},onclose:function(){_self._close();}};
				this.socket = this.channel.open(on);
			}catch(e){
				setTimeout((function(){_self._openChannel();}),100);
			}
		},
		closeOpenChannel:function(){
			if(this.socket)this.socket.close();
			this.token=this.channel=this.socket=null;
			var _self=this;
			setTimeout((function(){_self.openChannel();}),100);
		},
		closeChannel:function(){
			if(this.socket)this.socket.close();
			this.token=this.channel=this.socket=null;
		},
		_opened:function(){
			this.update();
		},
		_message:function(msg){
			return this.message($.parseJSON(msg.data));
		},
		message:function(jso){
			return true;
		},
		_error:function(msg){
			this.token=this.channel=this.socket=null;
		},
		_close:function(){
			jso$().request({url:'channel',method:'closed',arg:{'channel_id':this.channel_id,'options':this.options}}).done(function(jso){});
			if(!this.dead)this.openChannel();
		},
		clone_message:function(){
			return $.extend(true,{},this.def_msg);
		},
		send:function(msg){
			jso$().request({url:'channel',method:'send',arg:{'channel_id':this.channel_id,'options':this.options,'message':$.extend(true,{},this.def_msg,msg)}}).done(function(jso){});
		},
		update:function(){
			var _self=this;
			if(this.socket){
				jso$().request({url:'channel',method:'update',arg:{'channel_id':this.channel_id,'options':this.options}}).done(function(jso){});
			}else {
				if(this.channel_id) setTimeout(function(){_self.update();},1000);
			}
		},
		getChannelId:function(){
			return this.channel_id;
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
	

window.a$=a$;
//window.xml$=xml$;
//window.dm$=dm$;
window.jso$=jso$;
window.aFB=aFB;
window.aGoogle=aGoogle;
window.aGCS=aGCS;
window.aChannel=aChannel;

})(window);

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
      Math.uuidSchool=function(){
          return Math.uuid(8, 60);
      };
      Math.uuidPhoto=function(){
          return Math.uuid(4, 60);
      };
	})();

$.fn.grid = function(options){
    return this.each(function(){
        var o = $.extend(true,{grid: null},options);
        var $kids = $(this).children(),
            gridCols = {solo:1, a:2, b:3, c:4, d:5, e:6, f:7},
            grid = o.grid,iterator;
        if( !grid ){
            if( $kids.length <= 7 ){
                for(var letter in gridCols){if(gridCols[letter] == $kids.length){ grid = letter; }}
            }else{grid = 'a';}
        }
        iterator = gridCols[grid];
        $(this).addClass('ui-grid-' + grid);
        $kids.filter(':nth-child(' + iterator + 'n+1)').addClass('ui-block-a');
        if(iterator > 1){   
            $kids.filter(':nth-child(' + iterator + 'n+2)').addClass('ui-block-b');
        }   
        if(iterator > 2){   
            $kids.filter(':nth-child(3n+3)').addClass('ui-block-c');
        }   
        if(iterator > 3){   
            $kids.filter(':nth-child(4n+4)').addClass('ui-block-d');
        }   
        if(iterator > 4){   
            $kids.filter(':nth-child(5n+5)').addClass('ui-block-e');
        }
        if(iterator > 5){   
            $kids.filter(':nth-child(6n+6)').addClass('ui-block-f');
        }
        if(iterator > 6){   
            $kids.filter(':nth-child(7n+7)').addClass('ui-block-g');
        }
    }); 
};

$.fn.selectRange = function(start, end) {
    return this.each(function() {
        if (this.setSelectionRange) {
            this.focus();
            this.setSelectionRange(start, end);
        } else if (this.createTextRange) {
            var range = this.createTextRange();
            range.collapse(true);
            range.moveEnd('character', end);
            range.moveStart('character', start);
            range.select();
        }
    });
};





var aSound={
		loop:false,
			_load:function(){
				var _self=this;
	    		try{
	    			var src='/xlive/audio/'+((navigator.userAgent.indexOf('MSIE') > -1)?'order.mp3':'order.wav');
	    			_self.sound=document.createElement('audio');
	    			_self.sound.setAttribute('src',src);
	    			_self.sound.setAttribute('loop',aSound.loop);
					if(typeof _self.sound.loop == 'boolean') _self.sound.loop=aSound.loop;
					else{
						if(aSound.loop){
						    _self.sound.addEventListener('ended', function() {
						        _self.sound.currentTime=0;_self.sound.play();
						    },false);
						}
					}
					document.body.appendChild(_self.sound);
	    		}catch(e){}
			},
			play:function(on){
				var _self=this;
				try{
 	    		if(_self.sound){
 	    			_self.soundOn=on;
 	    			if(on){
 	    				_self.sound.play();
 	    				if(navigator.userAgent.indexOf('Chrome/') > 0 && aSound.loop){
 	    					setTimeout(function(){
	 	    						_self.sound.load();
	 	    						if(_self.soundOn) _self.play(_self.soundOn);
 	    						},7000);
 	    				}
 	    			}else _self.sound.pause();
 	    		}
				}catch(e){}
	    	}
	};
window.aSound=aSound;


window.browserInterface=function(rjs){
	require([rjs.requirejs],function(JS){
		if(JS[rjs.func])JS[rjs.func](rjs.param);
	});
};
var idfObject={};

window.IDF=function(idf,param){
	if(typeof idf == 'function'){
		var id=Math.uuid(8, 60);
		idfObject[id]=idf;
		return id;
	}else{
		var f=idfObject[idf];
		if(f){
			f($.parseJSON(param||{}));
			delete idfObject[idf];
		}
	}
};




