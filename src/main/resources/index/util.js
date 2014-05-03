/**
    Кроссбраузерное получение XMLHttpRequest
*/
function getXmlHttp(){
  var xmlhttp;
  try {
    xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
  } catch (e) {
    try {
      xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    } catch (E) {
      xmlhttp = false;
    }
  }
  if (!xmlhttp && typeof XMLHttpRequest!='undefined') {
    xmlhttp = new XMLHttpRequest();
  }
  return xmlhttp;
}

/**
    Экранирует html-специфичные символы
*/
String.prototype.escape = function() {
    var tagsToReplace = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;'
    };
    return this.replace(/[&<>]/g, function(tag) {
        return tagsToReplace[tag] || tag;
    });
};

/**
    Декодирование json-объектов
*/
function decodeObj(obj){
    var newObj = {};
    for(var key in obj){
        newObj[key] = decodeURIComponent(atob(obj[key]));
    }
    return newObj;
}