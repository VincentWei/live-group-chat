
Router.configure({
    waitOn: function() {
        return [Meteor.subscribe('liveRecords', LIVE_TIME)];
    }
});

Router.map(function() {
    this.route('wechatLiveMain', {
        path: '/'
    });

    this.route('userRank', {
        path: '/users',
        onBeforeAction: function() {
            Meteor.subscribe('userRanks', DESC_SORT, FIRST_BLOCK_SIZE);
            this.next();
        },

        action: function() {
            this.render();
        }
    });

    this.route('userRecords', {
        path: '/users/:nickId',

        onBeforeAction: function() {
            Session.set(USER_RECORDS_SIZE, FIRST_BLOCK_SIZE);
            Session.set(HISTORY_NICKID, this.params.nickId);
            Meteor.subscribe('recordsInNick', this.params.nickId, ASC_SORT,FIRST_BLOCK_SIZE);
            this.next();
        },

        data: function() {
            return {nickId: this.params.nickId};
        },

        action: function() {
            this.render();
        }
    });


    this.route('history', {
        path: '/history/:date',

        onBeforeAction: function() {
            time = new Date(this.params.date).getTime();
            Session.set(HISTORY_RECORDS_SIZE, FIRST_BLOCK_SIZE);
            Session.set(HISTORY_DATE, time);
            Meteor.subscribe('recordsInDay', time, ASC_SORT, FIRST_BLOCK_SIZE);
            this.next();
        },

        data: function() {
            return {time: new Date(this.params.date).getTime(), date: this.params.date};
        },

        action: function() {
            this.render();
        }
    })

   this.route('about', {
        path: '/about'
   });
});
