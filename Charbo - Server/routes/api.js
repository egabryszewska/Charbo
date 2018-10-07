var express = require('express');
var router = express.Router();
var db = require('../database.js');
var request = require('request');

var admin = require("firebase-admin");

var serviceAccount = require("/home/ubuntu/HackRU/charbo-15ec2-firebase-adminsdk-dsyh7-9ebbe5fe57.json");


var schedule = require('node-schedule');

function sleep(millis) {
    return new Promise(resolve => setTimeout(resolve, millis));
}

//Repay transactions due today every midnight 4 hr diff
var repayJob = schedule.scheduleJob('0 06 8 * * *', function(){

	console.log("Repaying everything!!!!");
  	//Query transactions that need to be repaid today
  	db.getTransactionsDueToday(function(transactions){
  		console.log(transactions);
	  	for(var i = 0; i < transactions.length; i++){
	  		var transaction = transactions[i];

	  		var borrower = transaction.borrower_id;
	  		transaction.borrower_id = transaction.lender_id;
	  		transaction.lender_id = borrower;
	  		//5% interest rate
	  		transaction.amount = (parseFloat(transaction.amount) * 1.05).toFixed(2);

	  		db.getUser(transaction.lender_id, function(borrower){


	  			db.getUser(transaction.borrower_id, function(lender){
		  			var payload = {
					  notification: {
					    title: "You got $" + transaction.amount,
					    body: "You got the money you lended to " + borrower.first_name + " back!"
					  },
					  data: {
							id: transaction._id+""
						}
					};
					var options = {
					  priority: "high",
					  timeToLive: 60 * 60 *24
					};

					admin.messaging().sendToDevice(lender.firebase_id, payload, options)
					  .then(function(response) {
					    console.log("Successfully sent message:", response);
					  })
					  .catch(function(error) {
					    console.log("Error sending message:", error);
					  });


					 var payload1 = {
					  notification: {
					    title: "Charbo repayment to " + lender.first_name + " initiated.",
					    body: transaction.amount + " will be withdraw from your bank account."
					  },
					  data: {
							id: transaction._id+""
						}
					};
					var options1 = {
					  priority: "high",
					  timeToLive: 60 * 60 *24
					};

					admin.messaging().sendToDevice(borrower.firebase_id, payload1, options1)
					  .then(function(response) {
					    console.log("Successfully sent message:", response);
					  })
					  .catch(function(error) {
					    console.log("Error sending message:", error);
					  });


	  			});

	  		});


	  		

	  		initiateTransfer(transaction);
	  	}

  })
});


admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://charbo-15ec2.firebaseio.com"
});

router.get('/next_repayment', function(req, res, next){
	var next_invoke = repayJob.nextInvocation()._date;
	console.log(next_invoke);
	var now = new Date();

	var diffMs = (next_invoke - now);
	var diffMins = Math.round(((diffMs % 86400000) % 3600000) / 60000); //minutes
	var diffDays = Math.floor(diffMs / 86400000); // days
	var diffHrs = Math.floor((diffMs % 86400000) / 3600000); // hours
	
	// db.getTransactionsDueToday(function(transactions){
	// 	res.json(transactions);
	// })

	res.send("Next repayment in " + diffHrs + ' hours and ' + diffMins+ ' minutes!');
});

router.get('/users', function(req, res, next) {

	db.getUsers(
		function(users){
			res.json(users);
		}
	);
});


router.post('/update_firebase_token', function(req,res,next){
	console.log('updating firebase token')
	var user_id = req.body.user_id;
	var firebase = req.body.firebase;
	db.updateFirebaseToken(user_id, firebase, function(result){
		res.json(result);
	});
});

router.post('/money_request', function(req, res, next) {

	//insert to db
	db.insertTransaction(req.body.lender_id, req.body.borrower_id, req.body.amount, req.body.memo, new Date(), new Date(req.body.date_due), function(insertedTransaction){
		console.log(insertedTransaction);
		res.json(insertedTransaction);


		db.getUser(req.body.lender_id, function(lender){
			var payload = {
			  notification: {
			    title: req.body.borrower_name + " is asking you for $" + req.body.amount,
			    body: req.body.memo
			  },
			  data: {
					id: insertedTransaction._id+""
				}
			};
			var options = {
			  priority: "high",
			  timeToLive: 60 * 60 *24
			};

			admin.messaging().sendToDevice(lender.firebase_id, payload, options)
			  .then(function(response) {
			    console.log("Successfully sent message:", response);
			  })
			  .catch(function(error) {
			    console.log("Error sending message:", error);
			  });

		});

	});
});

initiateTransfer = function(transaction){
	db.getUser(transaction.lender_id, function(lender){
		db.getUser(transaction.borrower_id, function(borrower){
			var headers = {'Content-Type' : 'application/json',
							"Accept": "application/json"};


			var body = {"medium": "balance",
					  	"payee_id": borrower.acc_id,
					  	"amount": parseFloat(transaction.amount),
					  	"status": "completed",
					  	"description": "Money lended with Charbo"};
			request.post({
				headers: headers,
				url:"http://api.reimaginebanking.com/accounts/" + lender.acc_id+"/transfers?key=9f140f7bac0fbdb2b18d59d1b2869927",
				json: body
			}, function(error, response, body){
			  console.log("Initiate transfer response: " + response);
			  console.log(body);
			});
		});
	});
}

router.post('/money_request_approved', function(req, res, next) {
	console.log("Money req approved")
	if(req.body.approved){
		db.setTransactionApproved(req.body.request_id, function(res){

			if(res.status == "success"){
				db.getTransaction(req.body.request_id, function(transaction){

					db.getUser(transaction.borrower_id, function(user){
						console.log('here');
						var payload = {
						  notification: {
						    title: req.body.lender_name + " decided to lend you money.",
						    body: "Make sure you can repay in time!"
						  }
						  
						};
						var options = {
						  priority: "high",
						  timeToLive: 60 * 60 *24
						};

						admin.messaging().sendToDevice(user.firebase_id, payload, options)
						  .then(function(response) {
						    console.log("Successfully sent message:", response);
						  })
						  .catch(function(error) {
						    console.log("Error sending message:", error);
						  });

					});


				initiateTransfer(transaction);
				});
				
			}
		});
	}

	res.json({status: "success"})
	
});


module.exports = router;



