const express = require("express");
const router = express.Router();
const http = require('http');
const https = require('https');
const Joi = require('joi');
const request = require('request');
const bodyParser = require('body-parser');
var _ = require('underscore');
const db = require("./db.js");

const reservierungValidate = {
  Aid: Joi.number().required(),
  Uid : Joi.string().required(),
  Eid: Joi.string().required(),
  Strasse: Joi.string().min(3).required(),
  Hausnummer: Joi.number().required(),
  Stadt: Joi.string().min(3).required()
};


router.post('/',(req,res)=> {
  let reservierung = {
  reservierung_aid: req.body.Aid,
  reservierung_uid: req.body.Uid,
  reservierung_eid: req.body.Eid,
  reservierung_straße: req.body.Straße,
  reservierung_hausnummer: req.body.Hausnummer,
  reservierung_stadt: req.body.Stadt
   };

   const result = Joi.validate(req.body, reservierungValidate);
   if(result.error){
     res.status(400).send(result.error.details[0].message);
     return;
   }

  db.createReservierung(reservierung, res);
  console.log(reservierung);

});

router.delete("/:id",(req,res)=>{
  let id = req.params.id;
  db.removeReservierung(id, res);
});

router.put("/:id",(req,res)=>{
  let id = req.params.id;

  let reservierung = {
    reservierung_aid: req.body.Aid,
    reservierung_uid: req.body.Uid,
    reservierung_eid: req.body.Eid,
    reservierung_straße: req.body.Straße,
    reservierung_hausnummer: req.body.Hausnummer,
    reservierung_stadt: req.body.Stadt
   };

   const result = Joi.validate(req.body, reservierungValidate);
   if(result.error){
     res.status(400).send(result.error.details[0].message);
     return;
   }

  db.updateReservierung(reservierung, id, res);
});

router.get("/:id",(req,res)=>{
    let id = req.params.id;
    let reservierung = db.getReservierung(id, res);
});

router.get("/:id/getaetigt",(req,res)=>{
    let id = req.params.id;
    let reservierung = db.getGetätigteReservierungen(id, res);
});

router.get("/:id/erhalten",(req,res)=>{
    let id = req.params.id;
    let reservierung = db.getErhalteneReservierungen(id, res);
});

router.get("/",(req,res)=>{
    if(req.query.uid){
      let reservierung = db.getGetätigteReservierungen(req.query.uid, res);
    }else if(req.query.eid){
      let reservierung = db.getErhalteneReservierungen(req.query.eid, res);
    }else{
      let reservierungen = db.getReservierungen(res);
    }
});

module.exports = router;
