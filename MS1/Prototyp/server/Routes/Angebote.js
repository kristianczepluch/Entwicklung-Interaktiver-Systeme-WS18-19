const express = require("express");
const router = express.Router();
const http = require('http');
const https = require('https');
const request = require('request');
const bodyParser = require('body-parser');
var _ = require('underscore');
const user_liste = require("./user_liste");

var angebote_anzahl = 0;
var angebote_liste = [];

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



// Alle Angebote werden ausgegeben wobei jedoch die Parameter entfernung und abholwert neu initialisert werden müssen
// Da Sie ansonsten die Werte der zuletzt getätigten Abfrage haben.
router.get('/', (req, res) => {
    for(let i=0; i<angebote_liste.length; i++){
      angebote_liste[i].entfernung = "Unkown";
      angebote_liste[i].abholwert = "Unkown";
    }
    return res.status(200).json(angebote_liste);
});
// Nachdem ein Angebit mit den untenstehenden Eigenschaften erstellt wurde wird er der liste angebote_liste hinzugefügt.
// Die Informationen wie VEGAN, VEGETARIAN oder ähnliches werden in der Funktion getLebensmittelInfos abgefragt
// und automatisch selbst eingetragen.
router.post('/', (req, res) => {
    let id = angebote_anzahl++;
    var userid = req.body.uid;
    var angebot = {
        id: id,
        ersteller: userid,
        name: req.body.name,
        straße: user_liste.getAll[userid].user_strasse,
        hausnr: user_liste.getAll[userid].user_hausnr,
        stadt : user_liste.getAll[userid].user_stadt,
        breitengrad : req.body.breitengrad,
        laengengrad : req.body.laengengrad,
        abholwert : "unkown",
        entfernung : "unkown"
    }
    let name = req.body.name;

    let lebensmittel_daten = getLebensmittelInfos(name);
    lebensmittel_daten.then(function(result) {

      angebot.einschräkungen = result;
      angebote_liste.push(angebot);
      console.log("Angebot wurde erstellt!");
      console.log(angebot);
      return res.status(200).json(angebot);
   });

});

// Diese Funktion ist das Kernelement der Prototyps
// An dieser Stelle werden dem Benutzer alle Angebote in der Nähe ausgegeben und für jeden wird automatisch
// Der Abholwert berechnet

router.get('User/:uid/:latitude/:longitude/:mobility', (req, res) => {

    // Parameter aus der URL Abfragen
    let breitengrad = req.params.latitude;
    let laengengrad = req.params.longitude;
    let uid = req.params.uid;
    let mob = req.params.mobility;

    // Informationen über den Benutzer abfragen
    // let user = getBenutzerMitDerId(uid);
    // let einschränkungen = user.einschränkungen;
    // let eintraege = user.eintraege;

    // let angebote = getAlleAngeboteMitStatusOffen();
    // 1.) Wetter informationen Abfragen, wenn diese da sind, kann weiter gearbeitet werden
    // 2.) Alle Angebote durchgehen und nur die relevanten drin lassen wie in der Schleife unten schon
    //     Nur die Entfernung erstmal eintragen
    // 3.) Alle Angebote durchgehen und mit den Einschraenkungen vergleichen, falls sie nicht passen, dann direkt auf 0 setzen
    //     und in eine tempräre Liste aussortieren
    // 4.) Für alle anderen Angebote Abholwert berechnen also:
    //     erst Entfernungpunkte unter Berücksichtigung der Mobilität
    //     dann Vorlienen anhand der Einträge und zum Schluss Wetter aufaddieren.
    // 5.) Abholwert der Ressource hinzufügen und der Ausgabeliste hinzufügen
    // 6.) Tmpliste der Ausgabeliste hinzufügen
    // 7.) Antwort senden


    let user = user_liste.getAll[uid];
    let user_einschränkungen = user_liste.getAll[uid].user_einschränkungen;
    let user_vorlieben = user_liste.getAll[uid].user_vorlieben;

    let wetter_wert;
    let wetter_daten = getWetter(breitengrad,laengengrad);
    // Wetterinformationen werden abgefragt und die Punkte werden eingetragen.
    wetter_daten.then(function(result) {
      console.log(result);
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
        angebote_liste[i].abholwert=wetter_wert;
      }


    let angebote_für_benutzer = [];

    // An dieser Stelle wird für alle Benutzer der Distanzwert ermittelt um die Entfernungpunkte einzutragen.
    // Angebote, welche weiter als 15 km entfernt sind werden garnicht erst in die Liste aufgenommen.
    for(let i=0; i<angebote_liste.length; i++){
      let breitengrad_angebot = angebote_liste[i].laengengrad;
      let laengengrad_angebot = angebote_liste[i].breitengrad;

      let distanz = getDistanz(breitengrad,laengengrad,breitengrad_angebot,laengengrad_angebot);
      angebote_liste[i].entfernung = distanz;
      if(distanz<1){
        angebote_liste[i].abholwert += 3;
        angebote_für_benutzer.push(angebote_liste[i]);

      } else if(distanz<3){
        angebote_liste[i].abholwert += 2;
        angebote_für_benutzer.push(angebote_liste[i]);
        console.log(angebote_liste[i]);

      } else if(distanz<5){
        angebote_liste[i].abholwert += 1;
        angebote_für_benutzer.push(angebote_liste[i]);

      } else if(distanz<15){
        angebote_liste[i].abholwert = 0;
        angebote_für_benutzer.push(angebote_liste[i]);

      }
      // An dieser Stelle wird geschaut ob das Angebot in den Vorlieben vorhanden ist und wenn ja wie oft.
      // Abhängig davon werden die Vorliebenspunkte vergeben.
      var vorlieben_wert;
      if (user_vorlieben.some(e => e.name === angebote_liste[i].name)) {
        var result = user_vorlieben.filter(obj => {
        return obj.name === angebote_liste[i].name;
        });
        if(result[0].anzahl<=5){
          vorlieben_wert = 1;
        } else if(result[0].anzahl>5) {
          vorlieben_wert = 2;
        }
  } else vorlieben_wert = 0;
  angebote_liste[i].abholwert+=vorlieben_wert;

  // Zum Schluss wird der Einschränkungswert berechnet. Es wird geschaut ob die Labels wie VEGAN oder VEGETARIAN in
  // dem Angebot vorhanden ist oder nicht.Wenn ja ist das Angebot valide, ansonsten nicht.
  let einschränkungswert;
    if(_.intersection(user_einschränkungen,angebote_liste[i].einschräkungen).length == user_einschränkungen.length){
      einschränkungswert = 1;
    } else einschränkungswert = 0;
    console.log("Einschränkungswert" + einschränkungswert);
    angebote_liste[i].abholwert*=einschränkungswert;
  }


      console.log("Wetterwert: " + wetter_wert);
      console.log("Vorliebenwer" + vorlieben_wert);
      return res.status(200).json(angebote_für_benutzer);
      });
   });

   module.exports = router;
