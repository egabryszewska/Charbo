/**
 * Created by tysovsky on 3/27/17.
 */

var MongoClient = require('mongodb').MongoClient,
    ObjectID = require('mongodb').ObjectID,
    ISODate = require('mongodb').ISODate;

var dbURL = "mongodb://localhost:27017/charboDb";
var userCollection = 'users';
var transactionCollection = 'transactions';


exports.getUsers = function(callback){
    MongoClient.connect(dbURL, function(error, db){

        if(!error){
            var collection = db.collection(userCollection);

            collection.find().toArray(function(err, users){
                if(!err){
                    callback(users);
                }

                db.close();
            });
        }
        else{
            callback([]);
        }


    });
}


exports.getUser = function(user_id, callback){
    MongoClient.connect(dbURL, function(error, db){

        if(!error){
            var collection = db.collection(userCollection);

            collection.find({_id: ObjectID(user_id)}).toArray(function(err, users){
                if(!err && users.length > 0){
                    callback(users[0]);
                }
                db.close();
            });
        }
        else{
            callback(null);
        }


    });
}



exports.updateFirebaseToken = function(user_id, firebase_id, callback){
    console.log(user_id);
    MongoClient.connect(dbURL, function(error, db){
        if(!error){
            var collection = db.collection(userCollection);

            collection.updateOne({_id: new ObjectID(user_id)}, {$set: {'firebase_id': firebase_id}}, function(err, res){

                
                if(!err){
                    callback({status: 'success'});
                }
                else{
                    console.log(err);
                    callback({status: err});
                }

                db.close();

            });

        }
        else{
            console.log(error);
            callback({status: error});
        }
    });
}

exports.getTransaction = function(transaction_id, callback){
    MongoClient.connect(dbURL, function(error, db){

        if(!error){
            var collection = db.collection(transactionCollection);

            collection.find({_id: ObjectID(transaction_id)}).toArray(function(err, transactions){
                if(!err && transactions.length > 0){
                    callback(transactions[0]);
                }
                db.close();
            });
        }
        else{
            callback(null);
        }


    });
}

exports.setTransactionApproved = function(transaction_id, callback){
    MongoClient.connect(dbURL, function(error, db){

        if(!error){
            var collection = db.collection(transactionCollection);
            collection.updateOne({_id: ObjectID(transaction_id)}, {$set: {'approved': true}}, function(err, res){
                 if(!err){
                    callback({status: 'success', "transaction": res});
                }
                else{
                    console.log(err);
                    callback({status: err});
                }

                db.close();
            });
        }
        else{
            callback(null);
        }


    });
}

exports.insertTransaction = function(payer_id, payee_id, amount, memo, date, date_due, callback){
    MongoClient.connect(dbURL, function(error, db){

        if(!error){
            var collection = db.collection(transactionCollection);

            collection.insertOne({'lender_id': payer_id, 'borrower_id': payee_id, 'amount': amount, 'memo': memo, 'date': date, 'date_due': date_due, approved: false, returned: false, attempts_to_return: 0},
                function(err, res){
                    if(!err){
                        callback(res.ops[0])
                    }
                });

            db.close();

        }
        else{
            callback([]);
        }


    });

}

exports.getTransactionsDueToday = function(callback){
    MongoClient.connect(dbURL, function(error, db){

        if(!error){
            var collection = db.collection(transactionCollection);

            var start = new Date();
            start.setHours(0,0,0,0);

            console.log(start.toISOString());

            var end = new Date();
            end.setHours(23,59,59,999);

            console.log(end.toISOString());


            collection.find({date_due: {$gte: start, $lt: end}, approved: true}).toArray(function(err, transactions){
                if(!err){
                    callback(transactions);
                }
                db.close();
            });
        }
        else{
            callback(null);
        }


    });
}