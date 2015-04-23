
Template.wechatLiveMain.helpers({
    chat_records: function () {
        return ChatRecords.liveItems();
    },

    today: function() {
        return new Date().Format("yyyy-MM-dd");
    }
});

Template.wechatLiveMain.onRendered(function () {
    Session.set('detail_mode','live');
    var div = document.getElementById('list_content_ul');
    var iHeight = document.documentElement.clientHeight || document.body.clientHeight;
    var header = document.getElementById('panel_heading');
    div.style.height = iHeight - header.offsetHeight - 15 + "px";

    div.scrollTop = div.scrollHeight;
});


