
// Im Folgenden ist der vorrübergehende Stand der Implentierung der Mongo DB Datenbank zu finden.Da uns die Einbindung einer Datenbank
//neu war, stellte diese ein großes Hindernis dar, welches zu Beginn beseitigt werden sollte.

const mongoose = require('mongoose');
const express = require('express');
const db = express.Router();
const Joi = require('joi');
mongoose.connect('mongodb://localhost/datenbank', { useNewUrlParser: true })
.then (() => console.log('Connected to MongoDB.'))
.catch(err => console.error('Could not connect to MongoDB', err));


const eintragSchema = new mongoose.Schema({
  Name: String,
  Anzahl : Number
});

const Eintrag = mongoose.model('Eintrag', eintragSchema);


const tokenSchema = new mongoose.Schema({
  Token: String,
  Uid : String

});


const Token = mongoose.model('Token', tokenSchema);

const einschränkungSchema = new mongoose.Schema({
  //Id: Number,
  Name: String
});

const Einschränkung = mongoose.model('Einschränkung', einschränkungSchema);

const Reservierung = mongoose.model('Reservierung', new mongoose.Schema({
//  Id: Number,
  Aid: Number,
  Uid: String,
  Eid: String,
  Zeit : {type: Date, default: Date.now},
  Straße: String,
  Hausnummer: Number,
  Stadt: String

}));



const nachrichtSchema = new mongoose.Schema({
//  Id: Number,
  Inhalt: String,
  Zeit : {type: Date, default: Date.now},
  Sender: String,
  Empfänger: String,
  Type: String

});

const Nachricht = mongoose.model('Nachricht', nachrichtSchema);

const User = mongoose.model('User', new mongoose.Schema({
  Uid: String,
  Vorname: String,
  Nachname :String,
  Längengrad: Number,
  Breitengrad: Number,
  Straße: String,
  Hausnummer: Number,
  Stadt: String,
  Email: String,
  Einträge: [eintragSchema],
  Einschränkungen: [einschränkungSchema],
  Token: tokenSchema,
  Nachrichten: [nachrichtSchema],
  Bild: String
}));

const Angebot = mongoose.model('Angebot', new mongoose.Schema({
//  Id: Number,
  Uid: String,
  Status: String,
  Zeit : {type: Date, default: Date.now},
  Titel: String,
  Abholzeitpunkt: String,
  Lebensmittel: String,
  Ablaufdatum: String,
  Beschreibung: String,
  Bild: String,
  Längengrad: Number,
  Breitengrad: Number,
  Einschränkungen: new Array()
}));

const db_export = {

async createUser(b, res, ) {
  let user = new User ({
    Uid: b.id,
    Vorname: b.user_vorname,
    Nachname: b.user_nachname,
    Längengrad: b.user_längengrad,
    Breitengrad: b.user_breitengrad,
    Straße: b.user_straße,
    Hausnummer: b.user_hausnummer,
    Stadt: b.user_stadt,
    Email: b.user_email,
    Einträge: new Array(),
    Einschränkungen: new Array(),
    Nachrichten: new Array(),
    Bild: b.user_bild
});

if(b.user_token){
    let mToken = new Token ({Token: b.user_token.Token, Uid: b.user_token.Uid});
    user.Token = mToken;
    mToken.save();
}

if(b.user_einschränkungen){
  let einschränkungsliste;
  for(let i=0; i<b.user_einschränkungen.length; i++){
    let einsch =  new Einschränkung({Name : b.user_einschränkungen[i].Name })
    einsch.save();
    user.Einschränkungen.push(einsch);
  }
}

user.save(function(err,user){
  if(err) return res.status(500).json("Server Error");
  return res.status(201).json(user);
});
},

async createAngebot(b, res,einsch) {
  const angebot = new Angebot ({
//  Id: b.angebot_id,
  Uid: b.angebot_uid,
  Status: b.angebot_status,
  Titel: b.angebot_titel,
  Abholzeitpunkt: b.angebot_abholzeitpunkt,
  Lebensmittel: b.angebot_lebensmittel,
  Ablaufdatum: b.angebot_ablaufdatum,
  Beschreibung: b.angebot_beschreibung,
  Bild: b.angebot_bild,
  Längengrad: b.angebot_längengrad,
  Breitengrad: b.angebot_breitengrad,
  Einschränkungen : einsch
});

const angebot_result = await angebot.save();
return res.status(201).json(angebot_result);
console.log(angebot_result);
},

async createEintrag(b, res) {
  let eintrag = new Eintrag ({
//  Id: b.eintrag_id,
  Name: b.eintrag_name,
  Anzahl: b.eintrag_anzahl
});

const eintrag_result = await eintrag.save();
console.log(eintrag_result);
res.status(201).json(eintrag_result);
},



async createEinschränkung(b, res) {
  let einschränkung = new Einschränkung ({
  Name: b.einschränkung_name
  });
  console.log(einschränkung);
  einschränkung.save(function(err,b){
  if(err) res.status(500).json("Server error");
  return res.status(201).json(b);
});
},



async createToken(b, res) {
  const token = new Token ({
  Token: b.token_token,
  Uid: b.token_uid
});

const token_result = await token.save();
console.log(token_result);
res.status(201).json(token_result);
},

async createNachricht(b, res) {
  const nachricht = new Nachricht ({
    //Id: b.nachricht_id,
    Inhalt: b.nachricht_inhalt,
    Zeit : {type: Date, default: Date.now},
    Sender: b.nachricht_sender,
    Empfänger: b.nachricht_empfänger,
    Type: b.nachricht_type
});

const nachricht_result = await nachricht.save();
console.log(nachricht_result);
res.status(201).json(nachricht_result);
},



async createReservierung(b, res) {
  const reservierung = new Reservierung ({
    Aid: b.reservierung_aid,
    Uid: b.reservierung_uid,
    Eid: b.reservierung_eid,
    Straße: b.reservierung_straße,
    Hausnummer: b.reservierung_hausnummer,
    Stadt: b.reservierung_stadt

});

const reservierung_result = await reservierung.save();
console.log(reservierung_result);
res.status(201).json(reservierung_result);
},


async getUsersAlle(res){
  const users = await User
  .find()
  .populate('Einträge' , 'Einschränkungen');
  return res.status(200).json(users);
},


async getUsers(id, res){
  const users = await User
  .find({Uid: id})
  console.log(users);
  if(users.length==0) return res.status(404).json("User was not found");
  return res.status(200).json(users);
},

async getUsersAnwendungslogik(id, res){
  return await User.find({Uid: id})
},

async getAngebote(res){
  const angebote = await Angebot
  .find()
  console.log(angebote);
  res.status(200).json(angebote);
},

async getAngebot(id, res){
  const angebote = await Angebot
  .find({_id: id})
  if(!angebote) return res.status(404).json("Angebot was not found");
  return res.status(200).json(angebote);
},


async getUserAngebote(id,res){
  const angebote = await Angebot
  .find({Uid: id})
  //.populate('Einschränkungen')
  console.log(angebote);
  if(!angebote) res.status(404).json("Angebot was not found");
  res.status(200).json(angebote);
},

async getAngebotAbholwert(uid, res){
  const angebote = await getAngebote(res);
  const user = await getUsers(uid, res);
  const einträge= await getUserEinträge(uid, res);
  const einschränkungen= await getUserEinschränkungen(uid, res);
  console.log(angebote + user + einträge + einschränkungen)

},

async getOffeneAngebote(state, res){
  const angebote = await Angebot
  .find({Status: state})
  .populate('Einschränkungen')
  console.log(angebote);
  if(angebote.length==0) res.status(404).json("Keine offenen Angebote");
  res.status(200).json(angebote);
},

async getOffeneAngeboteAnwendungslogik(){
  return await Angebot
  .find({Status: "offen"})
},



async getGetätigteReservierungen(id, res){
  const reservierungen = await Reservierung
  .find({Uid: id})
  console.log(reservierungen);
  if(reservierungen.length==0) res.status(404).json("Reservierungen not found");
  res.status(200).json(reservierungen);
},

async getErhalteneReservierungen(id, res){
  const reservierungen = await Reservierung
  .find({Eid: id})
  console.log(reservierungen);
  if(reservierungen.length==0) res.status(404).json("Reservierungen not found");
  res.status(200).json(reservierungen);
},

async getUserEinschränkungen(id, res){
  const users = await User
  .find({Uid: id})
  .populate('Einträge','Einschränkungen')
  .select({Einschränkungen: 1, Einträge: 0, _id: 0})
  let ausgabe = (JSON.parse(JSON.stringify(users))[0].Einschränkungen);
  ausgabe.push({"User":"http://localhost:3002/User/" + id});
  if(users.length==0) res.status(404).json("User was not found");
  return res.status(200).json(ausgabe);
},

async getUserEinschränkungenAnwendungslogik(id){
  return await User.find({Uid: id})
  .populate('Einträge','Einschränkungen')
  .select('Einschränkungen')
},


async getUserEinträge(id, res){
  const user = await User
  .find({Uid: id})
  .populate('Einschränkungen', 'Einträge')
  .select({Einträge: 1, Einschränkungen: 0, _id: 0})
  let ausgabe = (JSON.parse(JSON.stringify(user))[0].Einträge);
  ausgabe.push({"User":"http://localhost:3002/User/" + id});
  if(user.length==0) return res.status(404).json("User was not found");
  return res.status(200).json(ausgabe);
},

async getUserEinträgeAnwendungslogik(id){
  return await User.find({Uid: id}).populate('Einschränkungen', 'Einträge').select('Einträge')

},

async getUserNachrichten(id, res){
  const user = await User
  .find({Uid: id})
  .populate('Einträge', 'Nachrichten')
  .select({Nachrichten: 1, Einträge: 0, _id: 0})
  if(user.length==0) return res.status(404).json("User was not found");
  let ausgabe = (JSON.parse(JSON.stringify(user))[0].Nachrichten);
  ausgabe.push({"User":"http://localhost:3002/User/" + id});
  return res.status(200).json(ausgabe);
},



async getUserNachrichtVerlauf(id, sid, res){
  const nachrichten = await Nachricht
  .find({Empfänger: id, Sender: sid});
  console.log(nachrichten);
  res.status(200).json(nachrichten);
},


async getUserToken(id, res){
  const user = await User
  .find({Uid: id})
  .select('Token')
  console.log(user);
  let ausgabe = JSON.parse(JSON.stringify(user));
  let tmpString =
  ausgabe[0].Token.User = "http://localhost:3002/User/" + id;
  if(user.length==0) return res.status(404).json("User was not found");
  return res.status(200).json(ausgabe[0].Token);
},


async getReservierungen(res){
  const reservierungen = await Reservierung
  .find()
  console.log(reservierungen);
  res.status(200).json(reservierungen);
},


async getReservierung(id, res){
 const reservierungen = await Reservierung
 .findById(id)
 console.log(reservierungen);
 if(!reservierungen) res.status(404).json("Reservierung was not found");
 res.status(200).json(reservierungen);
},


async getUserReservierungen(id, res){
  const reservierungen = await Reservierung
  .find({Eid: id})
  console.log(reservierungen);
  res.status(200).json(reservierungen);
},

async removeUser(id, res){
  const user = await User.findOne({Uid: id})
  if(!user) return res.status(404).json("User was not found");
  const result = await User.deleteOne(user);
  if(result.n==0) return   res.status(404).json("User was not found");
  if(result.ok==0) return res.status(500).json("Server Error");
  return res.status(200).json(user);
},

async removeUserEinschränkung(id, eid, res){
  let user = await User.findOne({Uid: id});
  let index = -1;
  for(let i=0; i<user.Einschränkungen.length; i++){
    if(user.Einschränkungen[i]._id == eid){
      index=i;
    }
  }
  if(index==-1){
    console.log("Einschränkung konnte nicht gefunden werden!");
    return res.status(404).json("Einschränkung was not found");
  }
  user.Einschränkungen.splice(index,1);
  user.save(function(err,e){
    if(err) return res.status(500).json("Server err");
    return res.status(200).json("Einschränkung deleted");
  });
},

async removeUserNachricht(id, nid, res){
  let user = await User.findOne({Uid: id});
  let index = -1;
  for(let i=0; i<user.Nachrichten.length; i++){
    if(user.Nachrichten[i]._id == nid){
      index=i;
    }
  }
  if(index==-1){
    console.log("Nachricht konnte nicht gefunden werden!");
    res.status(404).json("Messsage was not found!");
  }
  user.Nachrichten.splice(index,1);
  user.save(function(err){
    if(err) return res.status(500).json("Server error");
    return res.status(200).json("Message deleted");
  });
},

async removeToken(id, res){
  let user = await User.findOne({Uid:id});
  const result = await Token.deleteOne({Uid: id})
  user.Token = undefined;
  user.save();
  console.log(result);
  res.status(200).json(result);
},


async removeAngebot(id, res){
  const angebot = await Angebot.findById(id)
  if(!angebot) res.status(404).json("Angebot was not found");
  console.log("Das Angebot das gefunden wurde ist " + angebot.Titel);
  const result = await Angebot.deleteOne(angebot);
  if(result.n==0) return res.status(404).json("Angebot was not Found");
  if(result.ok==0) return res.status(500).json("Server error");
  return res.status(200).json("Angebot deleted");
},

async removeReservierung(id, res){
  const reservierung = await Reservierung.findOne({Id: id})
  if(!reservierung) res.status(404).json("Reservierung was not found");
  console.log("Die Reservierung die gefunden wurde ist " + reservierung.Id);
  const result = await Reservierung.deleteOne(reservierung);
  console.log(result);
  res.status(200).json("Reservierung deleted");
},


async addUserEintrag(userid, eintrag, res){
      let user = await User.findOne({Uid: userid});
      let NeuerEintrag = new Eintrag({Name : eintrag.eintrag_name,  Anzahl : eintrag.eintrag_anzahl});
      user.Einträge.push(NeuerEintrag);
      NeuerEintrag.save();
      user.save(function(err,user){
        if(err) return res.status(500).json("Server error");
        return res.status(200).json(NeuerEintrag);
      });
},

async addUserEinschränkung(userid, einschränkung, res){
  let user = await User.findOne({Uid: userid});
  let NeueEinschränkung = new Einschränkung({Name : einschränkung.einschränkung_name, /*Id : einschränkung.einschränkung_id*/});
  user.Einschränkungen.push(NeueEinschränkung)
  NeueEinschränkung.save();
  user.save(function(err,einschränkung){
    if (err) return res.status(500).json("Server error")
    return res.status(200).json(NeueEinschränkung);
  });



},

async addUserNachricht(userid, nachricht, res){
  let user = await User.findOne({Uid: userid});
  let NeueNachricht = new Nachricht({
  Inhalt: nachricht.nachricht_inhalt,
  Zeit : {type: Date, default: Date.now},
  Sender: nachricht.nachricht_sender,
  Empfänger: nachricht.nachricht_empfänger,
  Type: nachricht.nachricht_type });
  user.Nachrichten.push(NeueNachricht);
  NeueNachricht.save();
  user.save(function(err,user){
    if(err) return res.status(500).json("Server err");
    res.status(200).json(NeueNachricht);
  });
},

async addUserToken(userid, token, res){
  let user = await User.findOne({Uid: userid});
  if(user == undefined) return res.status(404).json("User was not found");
  console.log("Der Benutzer der gefunden wurde ist: " + user.Vorname + userid);
  let NeuesToken = new Token({
    Token: token.token_token,
    Uid: token.token_uid});
  user.Token = NeuesToken;
  NeuesToken.save();
  user.save(function(err){
    if(err) return res.status(500).json("Server error");
    res.status(200).json(NeuesToken);
  });
},

async updateEinschränkung(uid, id, b, res){
  const result = await Einschränkung.update({Uid: uid, _id: id}, {
    $set: {
      //"Einschränkungen.$.Id": b.einschränkung_id,
      "Einschränkungen.$.Name": b.einschränkung_name
    }
  })
  res.status(200).json(result);

},
async updateUser(b, id, res){
  const result = await User.updateOne({Uid: id}, {
    $set: {
      Uid: b.id,
      Vorname: b.user_vorname,
      Nachname: b.user_nachname,
      Längengrad: b.user_längengrad,
      Breitengrad: b.user_breitengrad,
      Straße: b.user_straße,
      Hausnummer: b.user_hausnummer,
      Stadt: b.user_stadt,
      Email: b.user_email
    }
  });
  if(result.n==0) return res.status(404).json("User was not Found");
  if(result.ok==0) return res.status(500).json("Server error");
  return res.status(200).json(b);
},


async updateAngebot(b, id, res){
  const result = await Angebot.update({_id: id}, {
    $set: {
      Id: b.angebot_id,
      Uid: b.angebot_uid,
      Status: b.angebot_status,
      Titel: b.angebot_titel,
      Abholzeitpunkt: b.angebot_abholzeitpunkt,
      Lebensmittel: b.angebot_lebensmittel,
      Ablaufdatum: b.angebot_ablaufdatum,
      Beschreibung: b.angebot_beschreibung,
      Bild: b.angebot_bild,
      Längengrad: b.angebot_längengrad,
      Breitengrad: b.angebot_breitengrad,
      Entfernung: b.angebot_entfernung,
      Einschränkungen: [],
      Abholwert: b.angebot_abholwert
    }
  });
  if(result.n==0) return res.status(404).json("Angebot was not Found");
  if(result.ok==0) return res.status(500).json("Server error");
  return res.status(200).json(b);

},

async updateReservierung(b, id, res){
  const result = await Reservierung.updateOne({_id: id}, {
    $set: {
      Id: b.reservierung_id,
      Aid: b.reservierung_aid,
      Uid: b.reservierung_uid,
      Eid: b.reservierung_eid,
      Straße: b.reservierung_straße,
      Hausnummer: b.reservierung_hausnummer,
      Stadt: b.reservierung_stadt
    }
  });
   console.log(result);
   res.status(200).json(b);
},

}

module.exports = db_export;
