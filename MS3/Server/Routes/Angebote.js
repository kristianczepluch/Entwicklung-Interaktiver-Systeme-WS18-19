const express = require("express");
const router = express.Router();
const http = require('http');
const https = require('https');
const request = require('request');
const Joi = require('joi');
const bodyParser = require('body-parser');
var _ = require('underscore');
const db = require("./db.js");
var arraySort = require('array-sort');

var angebote_anzahl = 0;
var angebote_liste = [];

const angebotValidate = {
  Uid: Joi.string().required(),
  Status: Joi.string().min(3).required(),
  Titel: Joi.string().min(3).required(),
  Abholzeitpunkt: Joi.string().required(),
  Lebensmittel: Joi.string().min(3).required(),
  Ablaufdatum: Joi.string().required(),
  Beschreibung: Joi.string().min(3).required(),
  Längengrad: Joi.number().required(),
  Breitengrad: Joi.number().required(),
  Bild: Joi.string()
};

function getLebensmittelInfos(name){
  let basicURL = "https://api.edamam.com/api/nutrition-data?app_id=968e6a55&app_key=ebee017a4a0623311eef50fcf13b3a21&ingr=";
  let gesuchtes_lebensmittel = name;
  let url = basicURL + name;

  return new Promise(function(resolve, reject) {
  request.get(url, function(err, response, body) {
    body = JSON.parse(body);
    var lebensmittel_daten = body;
    if (lebensmittel_daten) {
      resolve(lebensmittel_daten.healthLabels);
    } else reject("Keine Lebensmitteldaten erhalten");
  });
});
};
function getDistanz(lat1, lon1, lat2, lon2){
    var dx = 71.5 * (lon1 - lon2);
    var dy = 111.3 * (lat1 - lat2);
    var distanz = Math.sqrt(dx * dx + dy * dy);
    return distanz;
}
function getWetter(lat,lon) {
  let basic_url = "http://api.openweathermap.org/data/2.5/weather?";
  let latiude_part = "lat=" + lat;
  let longitude_part = "&lon=" + lon;
  let url_appid = "&APPID=bbc1799890cd5d0045b1c3cc5fdbac7c";
  let url = basic_url + latiude_part + longitude_part + url_appid;
  console.log(url);
  return new Promise(function(resolve, reject) {
    request.get(url, function(err, response, body) {
      body = JSON.parse(body);
      var wetter_daten = body;
      if (wetter_daten) {
        resolve(wetter_daten.weather[0].id);
      } else reject("Keine Wetterdaten erhalten");
    });
  });
}

router.post('/', (req, res) => {
  let angebot = {
  //angebot_id: req.body.Id,
  angebot_uid: req.body.Uid,
  angebot_status :req.body.Status,
  angebot_titel: req.body.Titel,
  angebot_abholzeitpunkt: req.body.Abholzeitpunkt,
  angebot_lebensmittel: req.body.Lebensmittel,
  angebot_ablaufdatum: req.body.Ablaufdatum,
  angebot_beschreibung: req.body.Beschreibung,
  angebot_bild: req.body.Bild,
  angebot_längengrad: req.body.Längengrad,
  angebot_breitengrad: req.body.Breitengrad,
   };

   let p = getLebensmittelInfos(angebot.angebot_lebensmittel);
   p.then(function(einsch){
     console.log(einsch);
     const result = Joi.validate(req.body, angebotValidate);
     if(result.error){
       res.status(400).send(result.error.details[0].message);
       return;
     }
     db.createAngebot(angebot, res, einsch);
   });
});

router.delete("/:id",(req,res)=>{
  let id = req.params.id;
  db.removeAngebot(id, res);
});

router.get("/:id",(req,res)=>{
    if(req.params.uid){
      let angebot = db.getUserAngebote(id, res);
    }else{
      let id = req.params.id;
      let angebot = db.getAngebot(id, res);
    }
});

router.get("/User/:id",(req,res)=>{
    let id = req.params.id;
    console.log(id);
    let angebot = db.getUserAngebote(id, res);
});

router.get("/",(req,res)=>{
  if(req.query.uid){
    let id = req.params.uid;
    console.log(req.query.uid);
    let angebot = db.getUserAngebote(req.query.uid, res);
  }else{
    console.log("triggered");
    let angebote = db.getAngebote(res);
  }
});

router.get("/status/:status",(req,res)=>{
    let status = req.params.status;
    let angebote = db.getOffeneAngebote(status, res);
});

router.put("/:id",(req,res)=>{
  let id = req.params.id;

  let angebot = {
    angebot_id: req.body.Id,
    angebot_uid: req.body.Uid,
    angebot_status :req.body.Status,
    angebot_titel: req.body.Titel,
    angebot_abholzeitpunkt: req.body.Abholzeitpunkt,
    angebot_lebensmittel: req.body.Lebensmittel,
    angebot_ablaufdatum: req.body.Ablaufdatum,
    angebot_beschreibung: req.body.Beschreibung,
    angebot_bild: req.body.Bild,
    angebot_längengrad: req.body.Längengrad,
    angebot_breitengrad: req.body.Breitengrad,
    angebot_entfernung: req.body.Entfernung,
    angebot_abholwert: req.body.Abholwert,
   };

   const result = Joi.validate(req.body, angebotValidate);
   if(result.error){
     res.status(400).send(result.error.details[0].message);
     return;
   }

  db.updateAngebot(angebot, id, res);
});

/*
Die folgenden 4 Funktionen sind nötig, um die Daten für die Anwendungslogik aus der datenbankank
abzufragen. Da der auslese Vorgang in der Regel etwas Zeit benötigt wurde hier mit asynchronen Funktionen
gearbeitet.
*/
async function benutzerAbfragen(id){
  return await db.getUsersAnwendungslogik(id);
}

async function angeboteAbfragen(){
  return await db.getOffeneAngeboteAnwendungslogik();
}

async function einträgeAbfragen(id){
  return await db.getUserEinträgeAnwendungslogik(id);
}

async function einschränkungenAbfragen(id){
  return await db.getUserEinschränkungenAnwendungslogik(id);
}

function compareIndexFound(a, b) {
  return a.Abholwert - b.Abholwert;
}

/*
Diese Route ist das Kernelement der Prototyps
An dieser Stelle werden dem Benutzer alle Angebote in der Nähe ausgegeben und für jeden wird automatisch
Der Abholwert berechnet
*/

router.get('/User/:uid/:laengengrad/:breitengrad/:mobilitaet/:radius', (req, res) => {
    let breitengrad = req.params.breitengrad;
    let laengengrad = req.params.laengengrad;
    let radius = req.params.radius;
    let uid = req.params.uid;
    let mobilitaet= req.params.mobilitaet;

    let benutzerPromise = benutzerAbfragen(uid);
    let angebotPromise = angeboteAbfragen();
    let einträgePromise = einträgeAbfragen(uid);
    let einschränkungenPromise = einschränkungenAbfragen(uid);
    let wetter_wert=0;
    let distanzwert=0;
    let einschränkungswert=0;
    let alleEinträge = new Array();

    Promise.all([benutzerPromise,einschränkungenPromise, einträgePromise, angebotPromise]).then(values =>{
      // Sobald alle Daten aus der Datenbank für die Berechnung ausgelesen sind kann die Anwendungslogik beginnnen
      // Überprüfen ob es sich um eine gütige UserId handelt:
      let user = JSON.parse(JSON.stringify(values[0]));
      if(user.length==0) return res.status(404).json("User was not Found");

      // Falls Benutzer vorhanden ist kann mit der Suche nach geeigneten Angeboten begonnen werden
      //Alle Einschränkungen eines Benutzers in ein Array schreiben, um Sie auswerten zu können.
      var objEinsch = JSON.parse(JSON.stringify(values[1]));
      let alleEinschränkungen = new Array();
      for(let i=0; i<objEinsch[0].Einschränkungen.length; ++i){
        alleEinschränkungen.push(objEinsch[0].Einschränkungen[i].Name)
      }

      //Alle Einträge eines Benutzers in ein Array schreiben, um Sie auswerten zu können.
      var objEintr = JSON.parse(JSON.stringify(values[2]));
      for(let j=0; j<objEintr[0].Einträge.length; ++j){
        alleEinträge.push(objEintr[0].Einträge[j])
      }
      var angebote_liste = JSON.parse(JSON.stringify(values[3]));
      let wetter_daten = getWetter(breitengrad,laengengrad);
      console.log(angebote_liste.length);


      // Als erstes wird der Wetterwert ermittelt, da dieser von einem externen Dienstgeber abhängig ist.
      wetter_daten.then(function(result) {
        if(result<800 ){
          // alle ids unter 800 bedeuten regen,schnee oder ähniches
          wetter_wert=-1;
        }else if(result==800){
          // id 800 bedeutet klarer Himmel
          wetter_wert=1;
        } else if(result>800){
          // alle ids über 800 bedeutet bewölkt
          wetter_wert=0;
        }
        // Da Alle in der Nähe sind gilt dies für alle Angebote. Dies macht so erstmal keinen unterschied, wird
        // jedoch bei der asynchronen Kommunkation, welche durch eine Punktegrenze ausgelöst wird interessant.
        for(let i=0; i<angebote_liste.length; i++){
          angebote_liste[i].Abholwert=wetter_wert;
        }

        /*
        Nach diesem Schritt gilt es die Entfernung zu den Angeboten zu Berechnen.
        Hierfür wird eine Schleife durchgelaufen, welche innherlab des angegebenen Radius
        alle Angebote herauschreibt und direkt mit dem Attribute Entfernung verseht und
        gleichzeitig die Punkte nach dem Punkteschema in der Dokumentation einträgt
        */

        let angebote_für_benutzer = new Array();
        for(let i=0; i<angebote_liste.length; i++){
          let breitengrad_angebot = angebote_liste[i].Breitengrad;
          let laengengrad_angebot = angebote_liste[i].Längengrad;


          /* Mit der Funktion getDistanz wird die Entfernung zwischen dem Benutzer und dem Angebot berechnet,
             wobei hier die Formel, welche in der Dokumentation spezifizert wurde, verwendet wird */
          console.log("Längengrad/Breitengrad Benutzer: " + laengengrad + "/" + breitengrad);
          console.log("Längengrad/Breitengrad Angebot: " + laengengrad_angebot + "/" + breitengrad_angebot);
          let distanz = getDistanz(breitengrad,laengengrad,breitengrad_angebot,laengengrad_angebot);
          angebote_liste[i].Entfernung = distanz;

          // Geeignete Angebote werden herausgefilter und nach dem Punktesystem bewertet
          if(distanz>radius){
            /*Alle Angebote welche sich nicht innerhalb des in der URL spezifizierten Angebots befinden
            werden nicht in die Liste für die Ausgabe aufgenommen.*/
          }else{
          if(mobilitaet == "Fuss"){
            if(distanz<0.5){
              distanzwert=3;
              angebote_liste[i].Abholwert += distanzwert;
              angebote_für_benutzer.push(angebote_liste[i]);

            } else if(distanz<1){
              distanzwert=2;
              angebote_liste[i].Abholwert += distanzwert;
              angebote_für_benutzer.push(angebote_liste[i]);

            } else if(distanz<1.5){
              distanzwert=1;
              angebote_liste[i].Abholwert += distanzwert;
              angebote_für_benutzer.push(angebote_liste[i]);

            } else if(distanz>1.5){
              console.log("1");
              angebote_liste[i].Abholwert += 0;
              angebote_für_benutzer.push(angebote_liste[i]);
            }
          }
          else if(mobilitaet == "Fahrrad"){

            if(distanz<1){
              angebote_liste[i].Abholwert += 3;
              angebote_für_benutzer.push(angebote_liste[i]);

            } else if(distanz<1.5){
              angebote_liste[i].Abholwert += 2;
              angebote_für_benutzer.push(angebote_liste[i]);

            } else if(distanz<2){
              angebote_liste[i].Abholwert += 1;
              angebote_für_benutzer.push(angebote_liste[i]);

            } else if(distanz>2){
              angebote_liste[i].Abholwert += 0;
              angebote_für_benutzer.push(angebote_liste[i]);
              }
            }
            else if(mobilitaet == "Auto"){

              if(distanz<1.5){
                angebote_liste[i].Abholwert += 3;
                angebote_für_benutzer.push(angebote_liste[i]);

              } else if(distanz<2.5){
                angebote_liste[i].Abholwert += 2;
                angebote_für_benutzer.push(angebote_liste[i]);

              } else if(distanz<3.5){
                angebote_liste[i].Abholwert += 1;
                angebote_für_benutzer.push(angebote_liste[i]);

              } else if(distanz>3.5){
                angebote_liste[i].Abholwert += 0;
                angebote_für_benutzer.push(angebote_liste[i]);
              }
            }else{
              return res.status(400).json("Choose between: Fuss, Fahrrad or Auto at field Mobility!");
          }
        }
        }
            /* Im nächsten Schritt gilt es die Vorlieben, aus den Einträgen des Benutzers zu ermitteln
            Da Einträge mit Name und Anzahl spezifizert sind, muss geschaut werden ob ein Eintrag
            mit dem Lebensmittelnamen vorhanden ist und die Anzahl aufaddiert werden. Im Anschluss
            können die Vorliebenpunkte nach der Tabelle eingetragen werden. */
            for(let z = 0; z<angebote_für_benutzer.length; z++){
            let tmpEinträge = new Array();
            let anzahl_der_einträge = 0;

            for(let a=0 ; a<alleEinträge.length; a++){
              if(alleEinträge[a].Name==angebote_für_benutzer[z].Lebensmittel){
                anzahl_der_einträge+=alleEinträge[a].Anzahl;
              }
            }
            if(anzahl_der_einträge<=6 && anzahl_der_einträge>0){
              vorlieben_wert = 1;
            } else if(anzahl_der_einträge>6) {
              vorlieben_wert = 2;
            }
            else vorlieben_wert = 0;
            angebote_für_benutzer[z].Abholwert += vorlieben_wert;


            /*In diesem Schritt muss geschaut werden, ob auch die Einschränkungen des Angebots und die
            des Benutzer übereinstimmen. An dieser Stelle werden die erst alle in ein Array geschrieben und mit
            einer Intersection wird geprüft, ob alle Elemente aus dem Einschränkungsarray des Benutzers auch
            in dem Einschränkungsarray des Angebots ist.*/
            let tmpArray = new Array();
            for(let k=0; k<angebote_für_benutzer[z].Einschränkungen.length;k++){
              tmpArray.push(angebote_für_benutzer[z].Einschränkungen[k]);
            }

            // Einschränkungen überprüfen
              if(_.intersection(alleEinschränkungen,tmpArray).length == alleEinschränkungen.length){
                einschränkungswert = 1;
              } else einschränkungswert = 0;
              angebote_für_benutzer[z].Abholwert*=einschränkungswert;
              console.log("Einschränk: " + einschränkungswert);
              console.log("wetter: " + wetter_wert);
              console.log("distanz: " + distanzwert);
              console.log("vorlieben: " + vorlieben_wert);
           }
           // Liste wird bei einem Query vor der Ausgabe sortiert, sodass die besten Angebote ganz oben angzeigt werden.
           if(req.query.sortBy == "Abholwert"){
             return res.status(201).json(angebote_für_benutzer.reverse(angebote_für_benutzer.sort(function (l, r) {return l.Abholwert - r.Abholwert;})));
           }else return res.status(200).json(angebote_für_benutzer);
      });
    });
  });

   module.exports = router;
