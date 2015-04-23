Template.about.onRendered(function () {
    var self = this;
    var div = self.find('#list_content_ul');
    var iHeight = document.documentElement.clientHeight || document.body.clientHeight;
    var header = self.find('#panel_heading');
    div.style.height = iHeight - 5 - header.offsetHeight + "px";
});
