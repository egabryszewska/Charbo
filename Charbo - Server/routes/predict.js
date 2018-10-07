var express = require('express');
var router = express.Router();
var db = require('../database.js');

/* GET home page. */
router.get('/predict', function(req, res, next) {
    res.render('predict', { title: 'Prediction Algorithm'});
});

router.post('/predict', function(req,res,next){

});

module.exports = router;
