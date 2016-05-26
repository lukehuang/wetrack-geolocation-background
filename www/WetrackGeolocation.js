var WetrackGeolocation = function() {};


// 底层调用接口，参数分别为：事件、参数（数组）和回调函数
WetrackGeolocation.prototype.call_native = function(name , args , callback) {
    return cordova.exec(callback, this.error_callback, 'WetrackGeolocation', name, args);
}

WetrackGeolocation.prototype.error_callback = function(msg) {
	console.log("Javascript Callback Error: " + msg);
}
// 获取当前位置
WetrackGeolocation.prototype.getCurrentPosition = function(callback) {
    function onSuccess = function(msg) {
        callback(JSON.parse(msg))
    }
    this.call_native("getCurrentPosition", [] , onSuccess);
}
// 监听位置变化
// WetrackGeolocation.prototype.watchPosition = function(args , callback) {
//     function onSuccess = function(msg) {
//         callback(JSON.parse(msg))
//     }
//     this.call_native("watchPosition", args , onSuccess);
// }
// 监听位置变化
// WetrackGeolocation.prototype.stopWatch = function() {
//     this.call_native("stopWatch", [] , null);
// }
// 初始化上传：args解析满足以下格式。默认POST
// {
//     api : "" ,
//     headers: { key : val , ...}
//     params : { key : val , ...}
// }
// WetrackGeolocation.prototype.initUploader = function(args , callback) {
//     this.call_native("initUploader", args , callback);
// }
//
// WetrackGeolocation.prototype.startUploader = function() {
//     this.call_native("startUploader", [] , null);
// }
//
// WetrackGeolocation.prototype.stopUploader = function() {
//     this.call_native("stopUploader", [] , null);
// }


if(!window.plugins) {
	window.plugins = {};
}

if(!window.plugins.wetrackGeolocation) {
	window.plugins.wetrackGeolocation = new WetrackGeolocation();
}

module.exports = new WetrackGeolocation();
