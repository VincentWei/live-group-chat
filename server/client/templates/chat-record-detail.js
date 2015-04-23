/**
 * Created by xwyan on 15/4/17.
 */
Template.chat_record_detail.helpers({
    createdAtHuman : function(createdAt) {
        if (Session.get('detail_mode') == 'live') {
            var div = document.getElementById("list_content_ul");
            if (div && div.scrollTop + div.offsetHeight >= div.scrollHeight)
                Session.set("scrollToBottom", true);
            else
                Session.set("scrollToBottom", false);
        }
        return ChatRecords.date(createdAt, "hh:mm");
    }
});

function Linkify(inputText) {
    //URLs starting with http://, https://, or ftp://
    var replacePattern1 = /(\b(https?|ftp):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/gim;
    var replacedText = inputText.replace(replacePattern1, '<a href="$1" target="_blank">$1</a>');

    //URLs starting with www. (without // before it, or it'd re-link the ones done above)
    var replacePattern2 = /(^|[^\/])(www\.[\S]+(\b|$))/gim;
    var replacedText = replacedText.replace(replacePattern2, '$1<a href="http://$2" target="_blank">$2</a>');

    //Change email addresses to mailto:: links
    var replacePattern3 = /(\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,6})/gim;
    var replacedText = replacedText.replace(replacePattern3, '<a href="mailto:$1">$1</a>');

    return replacedText;
}

Template.chat_record_detail.onRendered (function () {
    var detail = this.$('div.content');
    var html = Linkify (detail.text ());
    detail.html (html);

    if(Session.get('detail_mode') == 'live' && Session.get("scrollToBottom")) {
        var div = document.getElementById("list_content_ul");
        div.scrollTop = div.scrollHeight;
    }
});

