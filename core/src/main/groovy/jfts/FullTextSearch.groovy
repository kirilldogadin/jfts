package jfts

//TODO вынести в класс Store или хранилище - Индекс (токены - id фраз), а будет также Хранилище самих фраз(id фраз - фразы)
Map<String, List<String>> token2Phrase = new HashMap<String, List<String>>()

TreeMap<String, Object> token2PhraseTreeMap = new TreeMap<String, List<String>>();
//0.выгрузить строки/объекты в нужном формате из бд, нужно только 2 поля - фраза и id
//1. получить список фраз НЕ разбитых по словам
//2. разбить список фраз на слова лексемы
//на токены - т.е. каноническая форма - для русского языка
//3. "проиндексировать" - т.е. создать в мапе отношения слово-СПИОСОК фраз
//4. "проиндексировать" - т.е. создать в мапе отношения слово-СПИОСОК id фраз
//TODO Хранить не токены, а их хеши?
//TODO Хранитть не фразы, а id фраз
//TODO фразы не дублировать, а хранить в set , причем не их самих,а их id!
//1 по ID брать из базы
//2 Хранить в Мапе, или массиве
//0
//TODO использовать Trie структуру для полнотекстового поиска

//1. Брать по одному токену ЕСТЬ
//2. Брать по префиксу
//3.БРАТЬ ПО пересечению токенов (несколько поисковых слов) точное совпадение

//TODO благодаря тому, что структура внешняя, можем описать блокировки как ReadWrite или Stomp?

//2
/**
 *
 * @param phrase 1 фраза которую надо разбить на лексемы
 */

//region API SAVE PUT
def getLexemes(String phrase) {
    phrase.tokenize()
}
/**
 * Добавить один токен-ключ для одной фразы-значения
 * @param tokenKey
 * @param phraseValue
 * @param map
 */
//2
//TODO лист заменить на SET фраз, т.к. они повторяются
def addTokenInStore(String tokenKey, String phraseValue, Map<String, List<String>> map) {

    if (!map.containsKey(tokenKey)) { //ключ-токен существует?
        def phraseList = [] // если нет создаем пустой список
        if (!phraseList.contains(phraseValue)) { //если значения еще не дублируется
            //TODO нет проверки на тот же самый объект? объекты дублируюся тут или сет или доп проверку
            phraseList.add phraseValue   //добавляем к существующему списку значение
            map.put(tokenKey, phraseList)
        }
    } else {
        map[tokenKey].add phraseValue
    }
}

/**
 * Добавить список токенов для одной фразы значения
 * @param tokenKeyList список токенов-ключей для фразы
 * @param phraseValue сама фраза
 * @param map мапа-хранилище токен - фраза
 */
//2
def addTokenListForOnePhraseInStore(List<String> tokenKeyList, String phraseValue, Map<String, List<String>> map) {
    tokenKeyList.forEach { token ->
        addTokenInStore(token, phraseValue, map)
    }
}

/**
 * Разбирает исходную фразу на лексемы и добавляет в словарь по токенам
 * @param phrase
 * @param map
 * @return
 */
def indexingPhrase(String phrase, Map map) {
    addTokenListForOnePhraseInStore(getLexemes(phrase), phrase, map)
}
//endregion

//API GET
/**
 * Точное совпадение для одного токена
 * @param token ключе для поиска
 * @param map структура в которой ищем
 * @return список фраз ответов
 */
List<String> fullMatchSearchByToken(String token, Map<String, List<String>> map) {
    return map.get(token);
}

/**
 *  Функция возвращает список фраз-значений по по списку токенов-ключей
 *  //TODO заменить это просто на SET чтобы не тратить время на проверку уникальности ТО ЕСТЬ ПРОСТО ОБЪЕДИНЯЕМ СЕТЫ
 * @param tokens
 * @param map
 * @return
 */
List<String> fullMatchSearchByTokenList(List<String> tokens, Map<String, List<String>> map) {
    //TODO вынести это место? как коолюек через кложур
    List<List<String>> allPhrasesWithAllTokens = tokens.collect { token ->
        fullMatchSearchByToken(token, map)
    }
    //TODO нет проверки на NPE как коллбер чере кложур
    //TODO вынести это место?
    return allPhrasesWithAllTokens
            .inject(allPhrasesWithAllTokens[0], //reduce так сказать
            { listOfListAllTokens, listOfOneToken -> //listOfListAllTokens - предыдущее состояние  (temp)
                listOfListAllTokens = listOfListAllTokens.intersect(listOfOneToken)
            })


}

/**
 *  * TODO искать префикс только среди имеющего смысл подмножества,т.е. пересекающихся множеств от пред. поиска
 *  ИЩЕТ среди всех слов входящих фразу (токенов) по префиксу
 * @param tokenPrefix
 * @param map
 * @param tokenByPrefixClosure функция возвращает список токенов для префикса
 * @param phrasesByTokensClosure функция возвращает список фраз для токена
 * @return
 */
static List<String> getPhraseListByTokenPrefixMatch(String tokenPrefix, Map<String, List<String>> map, Closure tokenByPrefixClosure, Closure phrasesByTokensClosure) {
    phrasesByTokensClosure((tokenByPrefixClosure.call(tokenPrefix, map)), map)
}

//TODO оптимизации
/**
 * //TODO CALBACK HELL
 * ИЩЕТ по всем полным словам + последнее слово по префиксу
 * @param tokens
 * @param map
 * @param phrasesByFullMatchTokenClosure - функция которая ищет по полному совпадению
 * @param phrasesByPrefixMatchTokenClosure - функция которая ищет по префиксу для последнего токена
 * @return
 */
def getPhraseListByTokenListPrefixMatchLastToken(List<String> tokens, Map<String, List<String>> map, Closure phrasesByFullMatchTokenClosure, Closure phrasesByPrefixMatchTokenClosure) {
    (phrasesByFullMatchTokenClosure.call(tokens, map)).add(phrasesByPrefixMatchTokenClosure.call(tokens.last(), map));
    //найти все слова кроме последнего по полному , а последнее по префиксу
}

List<String> getPhraseListByTokenListPrefixMatchDummyAlgorithm(List<String> tokens, Map<String, List<String>> map) {
    //TODO вынести это место? как коолюек через кложур
    tokens = tokens.collect { token ->
        getTokenByTokenPrefixDummyAlgorithm(token, map)
    }.flatten()
    List<List<String>> allPhrasesWithAllTokens = tokens.collect { token ->
        fullMatchSearchByToken(token, map)
    }
    //TODO нет проверки на NPE как коллбер чере кложур
    //TODO заменить это просто на SET чтобы не тратить время на проверку уникальности
    //TODO вынести это место?
    return allPhrasesWithAllTokens
            .inject(allPhrasesWithAllTokens[0], //reduce так сказать
            { listOfListAllTokens, listOfOneToken -> //listOfListAllTokens - предыдущее состояние  (temp)
                listOfListAllTokens = listOfListAllTokens.intersect(listOfOneToken)
            })


}

/**
 * TODO переписать на эффективный 1 список искат по первым буквам токена 2 структура Trie
 * @param token
 * @param map
 * @return
 */
List<String> getTokenByTokenPrefixDummyAlgorithm(String tokenPrefix, Map<String, List<String>> map) {
    map.keySet()
            .findAll { token ->
        token.startsWith(tokenPrefix)
    }.toList()
}

/**
 *
 * Суть метода в сравнеии строк - Поразрядно.
 * Пример:
 * (14 16 8) и (14 16 1)
 * таким образом все числа prefix + lastChar (максимальное число) всегда больше,
 * т.е. сравнение всегда включит вхождения по фрефиксу
 * пример     (14 16 1) и (14 16 1 33 4) первое число будет больше
 * String str1 = "Мося";
 * String str2 = "Мосыывавыаываы";
 *
 *
 * @see java.lang.String#compareTo
 *
 *
 * println str1 > str2
 * @param tokenPrefix
 * @param treeMap
 * @return
 */
SortedMap<String, Object> prefixMatchSearchTreeAlgorithm(String tokenPrefix, NavigableMap<String, List<String>> treeMap) {
    return treeMap.subMap(tokenPrefix, tokenPrefix + Character.MAX_VALUE)
//    "".compareTo()
}


Set<String> getPhraseListByTokenPrefixListTreeAlgo(List<String> tokenPrefixList, TreeMap<String, List<String>> map) {
    List<List<String>> listPhrases = tokenPrefixList.collect {
        prefixMatchSearchTreeAlgorithm(it, map).collect {
            it.value
        }.flatten()
    }
    return listPhrases
            .inject(listPhrases[0], //reduce так сказать
            { listOfListAllTokens, listOfOneToken -> //listOfListAllTokens - предыдущее состояние  (temp)
                listOfListAllTokens = listOfListAllTokens.intersect(listOfOneToken)
            })


}

//endregion

//region TESTS context
/**
 * Для тестов
 */
class PhraseProducer {

    static List<String> phrase = ["шарикоподшипниковая ул дом 5 Москва все", 'Краснопресненская 8 Москва все', 'Пролетарская 11 все ул Москва']

    static String getPhraseOne() {
        return phrase[0]
    }

    static String getPhraseTwo() {
        return phrase[1]
    }

    static String getPhraseThree() {
        return phrase[2]
    }
}

// ТЕСТЫ МАТЬ ВАШУ
println "!Test of Lexemizer extractLexemes() method"
println getLexemes(PhraseProducer.getPhraseOne())


println "!Test addTokenInStore() method"
token2Phrase = [:]
addTokenInStore(getLexemes(PhraseProducer.getPhraseOne()).getAt(0), PhraseProducer.getPhraseOne(), token2Phrase)

token2Phrase.each { key, value ->
    println "$key - $value"
}


println "!Test of add in Store addTokenListForOnePhraseInStore() method"
token2Phrase = [:]
addTokenListForOnePhraseInStore(getLexemes(PhraseProducer.getPhraseOne()), PhraseProducer.getPhraseOne(), token2Phrase)

token2Phrase.each { key, value ->
    println "$key - $value"
}

println()
println "!Test addTokenListForOnePhraseInStore() method тот же токен, но другая фраза и совпадающий токен Москва"
addTokenListForOnePhraseInStore(getLexemes(PhraseProducer.getPhraseTwo()), PhraseProducer.getPhraseTwo(), token2Phrase)

token2Phrase.each { key, value ->
    println "$key - $value"
}

println()
println "!method indexingPhrase() 1ая фраза со словом москва"
token2Phrase = [:]
indexingPhrase(PhraseProducer.getPhraseOne(), token2Phrase)
token2Phrase.each { key, value ->
    println "$key - $value"
}

println()
println("!method indexingPhrase() 2ая фраза со словом москва")
indexingPhrase(PhraseProducer.getPhraseTwo(), token2Phrase)
token2Phrase.each { key, value ->
    println "$key - $value "
}

println()
println("!method fullMatchSearchByToken()")
def token = "Москва"
fullMatchSearchByToken(token, token2Phrase).each {
    println it
}

//endregion TESTS context

//region Test GET
println()
println "!method indexingPhrase() 1ая фраза со словом москва"
token2Phrase = [:]
indexingPhrase(PhraseProducer.getPhraseOne(), token2Phrase)
token2Phrase.each { key, value ->
    println "$key - $value"
}

println()
println("!method indexingPhrase() 2ая фраза со словом москва")
indexingPhrase(PhraseProducer.getPhraseTwo(), token2Phrase)
token2Phrase.each { key, value ->
    println "$key - $value "
}

println()
println("!method fullMatchSearchByToken() для двуз токенов и двух фраз")
indexingPhrase(PhraseProducer.getPhraseThree(), token2Phrase)
def token2 = "ул"

fullMatchSearchByTokenList([token, token2], token2Phrase).each {
    println it
}

println()
println("!method fullMatchSearchByToken() для трех токенов и двух фраз")
//TODO тут закомментирован косяк - можно добавить ДУБЛЬ значения
//indexingPhrase(getPhraseThree(), token2Phrase)
def token3 = "Пролетарская"

fullMatchSearchByTokenList([token, token2, token3], token2Phrase).each {
    println it
}

println("!Поиск токена по тупому алгоритма getTokenByTokenPrefixDummyAlgorithm")
println getTokenByTokenPrefixDummyAlgorithm("Мос", token2Phrase)

println()
//endregion

//region test конечного Полного Апи
def tokenPrefix = "Мос"
def tokenByPrefixClosure = { _tokenPrefix, map -> getTokenByTokenPrefixDummyAlgorithm(tokenPrefix, map) }
def phraseByTokenClosure = { tokens, map -> fullMatchSearchByTokenList(tokens, map) }
println()
println("!Поиск ВСЕХ ФРАЗ запросе по префиксу одного токена ")
tokenPrefix = "вс"
println getPhraseListByTokenPrefixMatch(tokenPrefix, token2Phrase, tokenByPrefixClosure, phraseByTokenClosure)
tokenPrefix = "ул"
println getPhraseListByTokenPrefixMatch(tokenPrefix, token2Phrase, tokenByPrefixClosure, phraseByTokenClosure)


println()
println "!Поиск ВСЕХ ФРАЗ запросе по префиксу всех токенов"
println getPhraseListByTokenListPrefixMatchDummyAlgorithm(["у", "о"], token2Phrase)
//TODO данный тест выявил, что для несуществующего токена не будет фильтра, т.е. как будто его и не передавали, а НАДО ЗАМЕНИТЬ НА ПУСТОЕ МНОЖЕСТВО
println getPhraseListByTokenListPrefixMatchDummyAlgorithm(["у", "шар"], token2Phrase)

//
////TODO ЭТО КОЛЛБЭК HELL и по ходу смысла в таком методе в принципе нет
//println ("!Поиск ВСЕХ ФРАЗ запросе по точно совпадению всех кроме последнего и префиксу последнего")
//def _tokenByPrefixClosure = { _tokenPrefix, map -> getTokenByTokenPrefixDummyAlgorithm(tokenPrefix, map) }
//def _phraseByTokenClosure = { tokens, map -> fullMatchSearchByTokenList(tokens, map)}
//def fullMatch= { _tokenList,_map -> fullMatchSearchByTokenList(_tokenList,_map)}
//def prefixMatch= { _token,_map -> getPhraseListByTokenPrefixMatch(_tokenList,_map,_tokenByPrefixClosure,_phraseByTokenClosure)}
//println getPhraseListByTokenListPrefixMatchLastToken (["Москва", "вс"],token2Phrase,fullMatch,prefixMatch)

//endregion

//region test полного Апи с оптимизациями
indexingPhrase(PhraseProducer.getPhraseOne(), token2PhraseTreeMap)
indexingPhrase(PhraseProducer.getPhraseTwo(), token2PhraseTreeMap)
indexingPhrase(PhraseProducer.getPhraseThree(), token2PhraseTreeMap)

def tokenPrefix1 = "у"
def tokenPrefix2 = "шари"
def tokenPrefix3 = "Москв"

//TODO сравнить предыдущий поиск и текущий
println "Более быстрый поиск по токенам метод - prefixMatchSearch()"
println prefixMatchSearchTreeAlgorithm(tokenPrefix1, token2PhraseTreeMap)
println prefixMatchSearchTreeAlgorithm(tokenPrefix2, token2PhraseTreeMap)
println prefixMatchSearchTreeAlgorithm(tokenPrefix3, token2PhraseTreeMap)

println ()
println "Метод getPhraseListByTokenPrefixListTreeAlgo() $tokenPrefix2,  $tokenPrefix3"
println getPhraseListByTokenPrefixListTreeAlgo([tokenPrefix2,tokenPrefix3], token2PhraseTreeMap)

println ()
println "Метод getPhraseListByTokenPrefixListTreeAlgo() $tokenPrefix3"
println getPhraseListByTokenPrefixListTreeAlgo([tokenPrefix3], token2PhraseTreeMap)

println ()
TreeMap<String, Object> realDataMap = new TreeMap<String, List<String>>();

//TODO ПОИСК РЕГИСТРОЗАВИСИМЫЙ сделать НЕрегистрозависимым
def realPhrase = "АЗС №484 Воскресенск Воскресенский пересечение ул. Вокзальная и 2-я Заводская";
indexingPhrase(realPhrase,realDataMap)
println "тест поиска для $realPhrase"
println getPhraseListByTokenPrefixListTreeAlgo(["АЗ"],realDataMap)

println "заводская"
println getPhraseListByTokenPrefixListTreeAlgo(["Заводская"],realDataMap)

//endregion