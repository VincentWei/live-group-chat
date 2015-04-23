TopRecords = new Mongo.Collection("wx_top_records");
//nickId, chatCount, lastContent

TopRecords.addOrInsertRecord = function (nickId, lastContent) {
    //console.log("......nickId:"+ nickId+ " "+lastContent); 
    var topR = TopRecords.findOne({nickId: nickId});
    var result;
    if (topR === undefined) {
        var count = ChatRecords.rank(nickId);
        result = TopRecords.insert({nickId: nickId, chatCount: count, lastContent: lastContent});
    } else {
        result = TopRecords.update({nickId: nickId}, {
            nickId: nickId,
            chatCount: topR.chatCount + 1,
            lastContent: lastContent
        });
    }
}

