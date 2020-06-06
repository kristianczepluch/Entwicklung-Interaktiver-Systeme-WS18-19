var fs = require('fs');


var user_liste = [];

var add = function (obj) {
  user_liste.push(obj);
}

var getAll = function () {
  return user_liste;
}

var global = {
  add : add,
  getAll : user_liste
};

module.exports = global;
