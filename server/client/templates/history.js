
HISTORY_RECORDS_SIZE = "historyBlockSize";
HISTORY_DATE = "historyDate";

Session.setDefault(HISTORY_RECORDS_SIZE, FIRST_BLOCK_SIZE);

Template.history.helpers({
    chat_records: function () {
        return ChatRecords.findBlockHistoryItemsByDay(this.time, ASC_SORT,
            Session.get(HISTORY_RECORDS_SIZE));
    }
});

Template.history.events({
    'scroll ul': function (event) {
        var div = document.getElementById("list_content_ul");

        if (div.scrollTop == (div.scrollHeight-div.offsetHeight)) {
            console.log('screenTop=' + div.scrollTop + '  get more');
            Session.set(HISTORY_RECORDS_SIZE, Session.get(HISTORY_RECORDS_SIZE) + INCREASE_BLOCK_SIZE);

        }
    }
});

Template.history.rendered = function() {
    this.autorun(function() {
        Meteor.subscribe("recordsInDay", Session.get(HISTORY_DATE), ASC_SORT,
            Session.get(HISTORY_RECORDS_SIZE));
    });
};

Template.history.onRendered(function () {
    Session.set('detail_mode','history');
    var div = document.getElementById('list_content_ul');
    var iHeight = document.documentElement.clientHeight || document.body.clientHeight;
    var header = document.getElementById('panel_heading');
    div.style.height = iHeight - header.offsetHeight - 15 + "px";
});
