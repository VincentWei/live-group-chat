USER_RECORDS_SIZE = "userBlockSize";
HISTORY_NICKID = "historyNickId";

Session.setDefault(USER_RECORDS_SIZE, FIRST_BLOCK_SIZE);

Template.userRecords.helpers({
    records : function(id) {
        return ChatRecords.find({nickId:id}, {sort: {createdAt: ASC_SORT}}).map(function (doc, index, cursor) {
            var i = _.extend(doc, {index: index});
            return i;
        });
    }
});

Template.userRecords.events({
    'scroll ul': function (event) {
        var div = document.getElementById("list_content_ul");

        if (div.scrollTop == (div.scrollHeight-div.offsetHeight)) {
            Session.set(USER_RECORDS_SIZE, Session.get(USER_RECORDS_SIZE) + INCREASE_BLOCK_SIZE);
        }
    }
});

Template.userRecords.rendered = function() {
    this.autorun(function() {
        Meteor.subscribe("recordsInNick", Session.get(HISTORY_NICKID),
            ASC_SORT, Session.get(USER_RECORDS_SIZE));
    });
};


Template.userRecords.onRendered(function(){
    var self=this;
    var div = self.find('#list_content_ul');
    var iHeight = document.documentElement.clientHeight||document.body.clientHeight;
    var header = self.find('#panel_heading');
    div.style.height = iHeight- 15 -header.offsetHeight+"px";
    var title_div = self.find('#title');
    var container = self.find('#container');
    title_div.style.width = container.offsetWidth-150 +"px";
});


Template.userRecordItem.helpers({
    createdAtHuman : function(createdAt) {
        return ChatRecords.date(createdAt,"MM-dd hh:mm");
    },
    bg:function(index){
        return index % 2 == 0 ? "list-group-item-info" : "";
    }
});

Template.userRecordItem.onRendered (function () {
    var detail = this.$('div.content');
    var html = Linkify (detail.text ());
    detail.html (html);

});
