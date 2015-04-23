USER_RANK_SIZE = "userRankSize";
Session.setDefault(USER_RANK_SIZE, FIRST_BLOCK_SIZE);

Template.userRank.helpers({
    users: function () {
        return TopRecords.find({}, {sort: {chatCount: DESC_SORT}}).map(
            function (doc, index, cursor) {
                var i = _.extend(doc, {index: index});
                return i;
            });
    }
});

Template.userRank.events({
    'scroll ul': function (event) {
        var div = document.getElementById("list_content_ul");

        if (div.scrollTop == (div.scrollHeight-div.offsetHeight)) {
            if (TopRecords.find().count() >= Session.get(USER_RANK_SIZE))
                Session.set(USER_RANK_SIZE, Session.get(USER_RANK_SIZE) + INCREASE_BLOCK_SIZE);
        }
    }
});

Template.userRank.rendered = function() {
    this.autorun(function() {
        Meteor.subscribe("userRanks", DESC_SORT, Session.get(USER_RANK_SIZE));
    });
};

Template.userRank.onRendered(function () {
    var self = this;
    userIndex = 0;
    var div = self.find('#list_content_ul');
    var iHeight = document.documentElement.clientHeight || document.body.clientHeight;
    var header = self.find('#panel_heading');
    div.style.height = iHeight - 5 - header.offsetHeight + "px";
});

Template.userItem.helpers({
    bg: function (index) {
        return index % 2 == 0 ? " list-group-item-info" : "";
    }
});