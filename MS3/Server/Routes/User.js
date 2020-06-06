const express = require("express");
const router = express.Router();
const http = require('http');
const https = require('https');
const Joi = require('joi');
const request = require('request');
const bodyParser = require('body-parser');
var _ = require('underscore');
const db = require("./db.js")



const userValidate = {
  Uid: Joi.string().required(),
  Vorname: Joi.string().min(3).required(),
  Nachname :Joi.string().min(2).required(),
  Längengrad: Joi.number().required(),
  Breitengrad: Joi.number().required(),
  Straße: Joi.string().min(3).required(),
  Hausnummer: Joi.number().required(),
  Stadt: Joi.string().min(3).required(),
  Email: Joi.string().min(3).required(),
  Token: Joi.object(),
  Einschränkungen: Joi.array(),
  Bild: Joi.string()
};

const eintragValidate = {
  //Id: Joi.number().required(),
  Name: Joi.string().min(3).required(),
  Anzahl : Joi.number().min(1).required()
};

const einschränkungValidate = {
  //Id: Joi.number().required(),
  Name: Joi.string().min(3).required(),
};

const nachrichtValidate = {
  //Id: Joi.number().required() ,
  Inhalt: Joi.string().min(2).required(),
  Sender: Joi.string().required(),
  Empfänger: Joi.string().required() ,
  Type: Joi.string().min(2).required(),
};

const tokenValidate = {
  //Id: Joi.number().required(),
  Token: Joi.string().min(3).required(),
  Uid: Joi.string().required()
};


var user_anzahl = 0;

router.get("/",(req,res)=>{
    let benutzer = db.getUsersAlle(res);
    //res.status(200).send(benutzer);
});

router.get("/:id",(req,res)=>{
    let id = req.params.id;
    let benutzer = db.getUsers(id, res);
    //res.status(200).send(benutzer);
});

// Nachdem ein Benutzer mit den untenstehenden Eigenschaften erstellt wurde wird er der liste User_liste sowie der Datenbank hinzugefügt.
router.post('/', (req, res) => {
  let benutzer = {
  id: req.body.Uid,
  user_vorname: req.body.Vorname,
  user_nachname :req.body.Nachname,
  user_längengrad: req.body.Längengrad,
  user_breitengrad: req.body.Breitengrad,
  user_straße: req.body.Straße,
  user_hausnummer: req.body.Hausnummer,
  user_stadt: req.body.Stadt,
  user_email: req.body.Email,
  user_token: req.body.Token,
  user_einschränkungen: req.body.Einschränkungen,
  user_bild: req.body.Bild
   };

  const result = Joi.validate(req.body, userValidate);
  if(result.error){
    res.status(400).send(result.error.details[0].message);
    return;
  }
  db.createUser(benutzer, res);
});

router.delete("/:id",(req,res)=>{
  let id = req.params.id;
  db.removeUser(id, res);
  //res.status(200).send("Benutzer gelöscht");
});

router.put("/:id",(req,res)=>{
  let id = req.params.id;

  let benutzer = {
  id: req.body.Uid,
  user_vorname: req.body.Vorname,
  user_nachname :req.body.Nachname,
  user_längengrad: req.body.Längengrad,
  user_breitengrad: req.body.Breitengrad,
  user_straße: req.body.Straße,
  user_hausnummer: req.body.Hausnummer,
  user_stadt: req.body.Stadt,
  user_email: req.body.Email,
  user_vorlieben: [],
  user_einkäufe: [],
  user_token: req.body.Token
   };

   const result = Joi.validate(req.body, userValidate);
   if(result.error){
     console.log(result.error.details[0].message);
     res.status(400).send(result.error.details[0].message);
     return;
   }

  db.updateUser(benutzer, id, res);
  //res.status(200).send("Benutzer geupdated");
});


router.get('/:id/Eintraege', (req, res) => {
  let id = req.params.id;
  let einträge = db.getUserEinträge(id, res);
    //return res.status(200).json(einträge);
});


router.post('/:id/Eintraege', (req, res) => {
  let uid = req.params.id;
  let eintrag = {
    //eintrag_id: req.body.Id,
    eintrag_name: req.body.Name,
    eintrag_anzahl : req.body.Anzahl
  }

  const result = Joi.validate(req.body, eintragValidate);
  if(result.error){
    res.status(400).send(result.error.details[0].message);
    return;
  }
  db.addUserEintrag(uid, eintrag,res);
  //res.status(200).json(eintrag);
});


router.get('/:id/Einschraenkungen', (req, res) => {
  let id = req.params.id;
  let einschränkungen = db.getUserEinschränkungen(id, res);
    //return res.status(200).json(einschränkungen);
});

router.post('/:id/Einschraenkungen', (req, res) => {
  let uid = req.params.id;
  let einschränkung = {
    einschränkung_name: req.body.Name,
  }

  const result = Joi.validate(req.body, einschränkungValidate);
  if(result.error){
    res.status(400).send(result.error.details[0].message);
    return;
  }
  db.addUserEinschränkung(uid, einschränkung, res);
});

router.delete("/:id/Einschraenkungen/:eid",(req,res)=>{
  let eid = req.params.eid;
  let id = req.params.id;
  db.removeUserEinschränkung(id,eid, res);
  //res.status(200).send("Einschränkung gelöscht");
});

router.get('/:id/Nachrichten', (req, res) => {
  let id = req.params.id;
  let nachrichten = db.getUserNachrichten(id, res);
    //return res.status(200).json(nachrichten);
});

router.get('/:id/Nachrichten/sender/:sid', (req, res) => {
  let id = req.params.id;
  let sid = req.params.sid
  let nachrichten = db.getUserNachrichtVerlauf(id, sid, res);
    //return res.status(200).json(nachrichten);
});

router.post('/:id/Nachrichten', (req, res) => {
  let uid = req.params.id;
  let nachricht = {
    //nachricht_id: req.body.Id,
    nachricht_inhalt: req.body.Inhalt,
    nachricht_sender: req.body.Sender,
    nachricht_empfänger: req.body.Empfänger,
    nachricht_type: req.body.Type
  }

  const result = Joi.validate(req.body, nachrichtValidate);
  if(result.error){
    res.status(400).send(result.error.details[0].message);
    return;
  }
  db.addUserNachricht(uid, nachricht, res);
  //res.status(200).json(nachricht);
});

router.delete("/:id/Nachrichten/:nid",(req,res)=>{
  let nid = req.params.nid;
  let id = req.params.id;
  db.removeUserNachricht(id,nid, res);
  //res.status(200).send("Nachricht gelöscht");
});

router.get('/:id/Token', (req, res) => {
  let id = req.params.id;
  let token = db.getUserToken(id, res);
    //return res.status(200).json(token);
});

router.post('/:id/Token', (req, res) => {
  let uid = req.params.id;
  let token = {
  //  token_id: req.body.Id,
    token_token: req.body.Token,
    token_uid: req.body.Uid
  }

  const result = Joi.validate(req.body, tokenValidate);
  if(result.error){
    res.status(400).send(result.error.details[0].message);
    return;
  }
  db.addUserToken(uid, token, res);
  //res.status(200).json(token);
});

router.delete("/:id/Token",(req,res)=>{
  //let tid = req.params.tid;
  let id = req.params.id;
  db.removeToken(id , res);
  //res.status(200).send("Token gelöscht");
});


router.put("/:id/Einschraenkungen/:eid",(req,res)=>{
  let id = req.params.id;
  let eid = req.params.eid

  let einschränkung = {
  //einschränkung_id: req.body.id,
  einschränkung_name: req.body.Name}

  const result = Joi.validate(req.body, einschränkungValidate);
  if(result.error){
    res.status(400).send(result.error.details[0].message);
    return;
  }

  db.updateEinschränkung(id,eid, einschränkung, res);
  //res.status(200).send("Benutzer geupdated");
});


module.exports = router;
