var express = require('express');
var router = express.Router();
var db = require('../database.js');

/* GET home page. */
router.get('/', function(req, res, next) {
    db.getPictures(function (pictures) {
        res.render('index', { title: 'SkyOutlet', description: 'Pictures of stuff', pictures: pictures});
    });
});

module.exports = router;
