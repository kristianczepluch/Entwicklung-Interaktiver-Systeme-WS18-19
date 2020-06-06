const express = require("express");
const app = express();
app.use(express.json());
const user = require("./Routes/User");
const angebote = require("./Routes/Angebote");
app.use('/User', user);
app.use('/Angebote', angebote)
var admin = require("firebase-admin");
var serviceAccount = require("./fooduse-64206-firebase-adminsdk-gf18j-448ef01663.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://fooduse-64206.firebaseio.com"
});

// Server Port konfigurieren und server starten
const settings = {
  port: process.env.PORT || 3001
};

app.listen(settings.port, function() {
  console.log("App läuft auf Port " + settings.port);
})
