// уникальный id текущей сессии поиска.
// меняется при каждом запросе.
// необходим для убиения процесса старого поиска при новом поиске или остановке текущего.
var sessionId = null;

function closeOldSearchSession(){
    if(sessionId){
        var xhr = getXmlHttp();
        xhr.open('GET', '/search/stop/'+sessionId, true);
        xhr.setRequestHeader('session-id', sessionId);
        xhr.send(null);
        sessionId = null;
    }
}

// чтение потока объектов
function readNewObject(xhr, param){
    var text = xhr.responseText;

    while(text[param.lastI] != '{' && param.lastI < text.length){
        param.lastI++;
    }
    if(text[param.lastI-1] == '{') param.lastI--;

    var end = param.lastI;
    while(text[end] != '}' && end < text.length){
        end++;
    }

    var chunk = text.substring(param.lastI, end+1);

    if(text[end] == '}'){
        param.lastI = end+2;
        return JSON.parse(chunk);
    } else {
        return null;
    }
}

/**
    Очищает область результатов поиска и возвращает ссылку на нее
*/
function clearResultContainer(){
    var container = document.getElementById("found-files");
    container.innerHTML = '';
    return container;
}

/**
    Отправляет запрос серверу на поиск текста textForSearch в файлах директории dirPath.
    На каждый найденный результат вызывает onResultCallback.
*/
function search(textForSearch, dirPath, onResultCallback){
    var xhr = getXmlHttp();
    xhr.open('GET', '/search/start/'+dirPath + '/' + textForSearch, true);
    xhr.setRequestHeader('session-id', sessionId);

    var parseParam = {lastI:0};
    xhr.onreadystatechange = function() {
        if (xhr.readyState != 2 && xhr.readyState != 3 && xhr.readyState != 4
            || xhr.readyState == 3 && xhr.status != 200){
            return;
        }
        var text = xhr.responseText;
        if(text.length != 0){
            var obj;
            while((obj = readNewObject(xhr, parseParam))!=null){
                // если приходят данные результаты устаревшего запроса, игнорируем их
                // Если obj.sessionId is undefined, следовательно это сообщение об ошибке или иное информационное

                if(obj.sessionId && obj.sessionId != sessionId){
                    return;
                }
                onResultCallback(decodeObj(obj));

                if(parseParam.lastI >= text.indexOf(']')){
                    return;
                }
            }
        }
    };
    xhr.send(null);
}

/**
    Создает DOM-объект - представление найденного файла
*/
function createItem(obj){
    var item = document.createElement('div');
    if(obj.error){
        item.style.color = 'red';
        item.innerHTML = obj.error;
    } else {
        var fileName = document.createElement('div');
        fileName.classList.add("file-name");
        fileName.innerHTML = obj.fileName;

        var fragment = document.createElement('div');
        fragment.classList.add("fragment");
        fragment.innerHTML = obj.fragment.escape();

        item.appendChild(fileName);
        item.appendChild(fragment);
    }
    return item;
}

/**
    Собирает данные для поиска и запускает его
*/
function searchAction(){
    var text = document.getElementById("text-to-search").value;
    var path = document.getElementById("path-to-search").value;
    if(text == "" || path == "") return;

    closeOldSearchSession();
    sessionId = btoa(encodeURIComponent(Math.random() + ""));

    var itemContainer = clearResultContainer();

    search(text, path, function(obj){
        itemContainer.appendChild( createItem(obj) );
    });
}

// Запускаем поиск при каждом изменении поискового запроса
var textField = document.getElementById("text-to-search");
textField.onkeyup  = function(e){
    aaaE = e;
    if(!(e.key == 'Backspace' || e.keyCode >= 32)) return;

    if(textField.value.length != 0){
        searchAction();
    } else {
        closeOldSearchSession();
        clearResultContainer();
    }
}

document.getElementById("start-search-btn").onclick = function(){
    searchAction();
}
document.getElementById("stop-search-btn").onclick = function(){
    closeOldSearchSession();
}