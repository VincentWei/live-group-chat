ChatRecords = new Mongo.Collection("wx_chat_records");
//nickId, content, contentType, createdAt, hash, comment, commentId, commentAt

LIVE_TIME = new Date().getTime();
FIRST_BLOCK_SIZE = 30;
INCREASE_BLOCK_SIZE = 20;

DESC_SORT = -1;
ASC_SORT = 1;

var timeInterval = 24*60*60*1000;

beginTime = function(ms) {
    var date = new Date(ms);
    return ms - date.getHours()*60*60*1000 - date.getMinutes()*60*1000 - date.getSeconds()*1000 - date.getMilliseconds();

}

ChatRecords.allow({
    insert: function (userId) {
        var user = Meteor.users.findOne(userId);
        return user && user.admin;
    },
    update: function (userId) {
        var user = Meteor.users.findOne(userId);
        return user && user.admin;
    }
});

ChatRecords.liveItems = function() {
    time = LIVE_TIME;
    var starttime = beginTime(time);
    var result = ChatRecords.find({createdAt: {$lt : time, $gte: starttime}}, {sort: {createdAt : DESC_SORT},
        limit : FIRST_BLOCK_SIZE});
    var resultArray = result.fetch();
    if (resultArray.length != 0) {
        time = resultArray[resultArray.length-1].createdAt;
    }
    return ChatRecords.find({createdAt: {$gte : time}}, {sort: {createdAt : ASC_SORT}});
}

ChatRecords.findBlockHistoryItemsByDay = function(msInDay, sort, limitSize) {
    var time = beginTime(msInDay);
    return ChatRecords.find({createdAt: {$lt : time + timeInterval, $gte: time}}, {sort: {createdAt : sort},
        limit: limitSize});
}

ChatRecords.rank = function(nickId) {
    return ChatRecords.find({nickId: nickId}).count();
}

Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

ChatRecords.date = function(ms,format) {
    return new Date(ms).Format(format);
}
