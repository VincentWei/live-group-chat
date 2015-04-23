var timeInterval = 24*60*60*1000;

Meteor.publish('recordsInDay', function(dayms, sort, limitSize) {
	var time = beginTime(dayms);
	return ChatRecords.find({content: {$regex: /^(?!!|！).*$/}, createdAt: {$lt: time+timeInterval, $gte:time}},
		{sort: {createdAt : sort}, limit : limitSize});
});

Meteor.publish('recordsInNick',function(nickid, sort, limitSize) {
	return ChatRecords.find({content: {$regex: /^(?!!|！).*$/}, nickId: nickid},
		{sort: {createdAt : sort}, limit : limitSize});
});

Meteor.publish('liveRecords', function(time) {
	var result = ChatRecords.find({content: {$regex: /^(?!!|！).*$/}, createdAt: {$lt : time}}, {sort: {createdAt : -1},
		limit : 20});
	var resultArray = result.fetch();
	if (resultArray.length != 0) {
		time = resultArray[resultArray.length-1].createdAt;
	}
	return ChatRecords.find({content: {$regex: /^(?!!|！).*$/}, createdAt: {$gte : time}}, {sort: {createdAt : 1}});
});

Meteor.publish("userRanks", function (sort, limit) {
	return TopRecords.find({nickId: {$ne: ''}}, {sort: {chatCount: sort}, limit: limit});
});

Accounts.onCreateUser(function(options, user) {
	if (options.profile)
		user.profile = options.profile;

	var admin = JSON.parse(Assets.getText("admin.txt"));
	user.admin = (user.username === admin.username);

	return user;
});

if (Meteor.isServer) {
	Meteor.startup(function () {
		// code to run on server at startup
		try {
			var admin = JSON.parse(Assets.getText("admin.txt"));

			if(!Meteor.users.findOne({username: admin.username})) {
				Accounts.createUser({
					username: admin.username,
					password: admin.password
				});
			} else {
				console.log('admin exists');
			}
		} catch (error) {
			console.log(error.reason);
		}

		ChatRecords.find({createdAt: {$gte:new Date().getTime()}}).observe({
			added:function(record){
				TopRecords.addOrInsertRecord(record.nickId,record.content);
			}
		});

	});
}
