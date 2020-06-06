const express = require("express");
const router = express.Router();
const http = require('http');
const https = require('https');
const request = require('request');
const bodyParser = require('body-parser');
var _ = require('underscore');
const user_liste = require("./user_liste");
const db = require("./datenbank.js");
var admin = require("firebase-admin");



var user_anzahl = 0;

// Diese Routen gibt alle angelegten Benutzer zurück
router.get("/",(req,res)=>{
db.getAlles();
res.status(200).json(user_liste.getAll);
});

// Nachdem ein Benutzer mit den untenstehenden Eigenschaften erstellt wurde wird er der liste User_liste hinzugefügt.
router.post('/', (req, res) => {
  let id = user_anzahl++;
  var user = {
  id: id,
  user_vorname: req.body.vorname,
  user_nachname :req.body.nachname,
  user_lägengrad: req.body.lägengrad,
  user_breitengrad: req.body.breitengrad,
  user_strasse: req.body.straße,
  user_hausnr: req.body.hausnr,
  user_stadt: req.body.stadt,
  user_vorlieben: [],
  user_einkäufe: [],
  user_einschränkungen: req.body.einschränkungen,
  user_angebote: [],
  user_image: req.body.bild
   };
   db.createUser(user);
   console.log(user.user_image);
   user_liste.add(user);
   res.status(200).json(user);
});

router.post('/Token', (req, res) => {
  let token = {
    uid: req.body.uid,
    token: req.body.token
  }
  console.log(token);

  let registrationToken = token.token;

  var payload = {
    data: {
      account: "Savings",
      balance: "$3020.25"
    }
  };

     var options = {
      priority: "high",
      timeToLive: 60 * 60 *24
    };

    admin.messaging().sendToDevice(registrationToken, payload, options)
  .then(function(response) {
    console.log("Successfully sent message:", response);
  })
  .catch(function(error) {
    console.log("Error sending message:", error);
  });
  res.status(200).json(token);

});


// Diese Ressource verwaltet die Vorlieben des Benutzers, sollte ein Element dadrin bereits vorhanden sein,
// so wird die anzahl des Elements erhöht. Falls nicht wird ein neues Element erstellt.
router.post('/:id/Vorlieben', (req, res) => {
    var vorliebe = {
      name : req.body.name,
      anzahl: req.body.anzahl
    };

    if (user_liste.getAll[req.params.id].user_vorlieben.some(e => e.name === req.body.name)) {
      var result = user_liste.getAll[req.params.id].user_vorlieben.filter(obj => {
      return obj.name === req.body.name;
      });
      result[0].anzahl+=req.body.anzahl;
      return res.status(200).json(result);
} else{
    user_liste.getAll[req.params.id].user_vorlieben.push(vorliebe);
    return res.status(200).json(vorliebe);
}

});

router.get('/:id/Vorlieben', (req, res) => {
    return res.status(200).send(user_liste.getAll[req.params.id].user_vorlieben);
});

module.exports = router;
