package jfts.egine;

import jfts.index.base.Index;

import java.util.*;
import java.util.function.Function;

/**
 * Операции НАД индексом.
 *
 * @param <K> ключ поиска
 * @param <V> возвращаемое значение
 * @param <S> индексируемое значение, которое хотим положить.
 *            S Может не совпадать с V. Например S - string, а V - Set<String>
 */
public class GenericTreeIndexer<K, V extends Set<S>, S> {

    /**
     * индексация - основная операция.
     * Создает сопоставление
     *
     * @param tokenKey
     * @param phraseValue
     * @param index
     */
    public void addValueByKey(K tokenKey, S phraseValue, Index<K, Set<S>> index) {
        //TODO переписать логику на computeIfAbsent?

        if ((index.get(tokenKey) == null)) { //ключ-токен существует?
            HashSet<S> phraseSet = new HashSet<>();
            phraseSet.add(phraseValue);
            index.put(tokenKey, phraseSet);
            return;
        }

        //!!!!TODO НЕПРАВИЛЬНАЯ ПРОВЕРКА
        if (index.get(tokenKey) != null) { //если значения еще не дублируется  //TODO сделать эту проверку опциональной
            //todo log или изменить проверку
        } else
            index.get(tokenKey).add(phraseValue); //добавляем к существующему списку значение
    }

    public void addTokenListForOneKeyInStore(Set<K> tokenKeySet, S phraseValue, Index<K, Set<S>> index) {
        tokenKeySet.forEach(token ->
                addValueByKey(token, phraseValue, index));
    }

    /**
     * TODO должно ли быть тут?
     * индексирует фразу, т.е. строит обратный индекс:
     * разбивает на токены, каждому токену соответсвует эта фраза
     *
     * @param phrase
     * @param index
     * @param lexer
     */
    public void indexPhrase(S phrase, Index<K, Set<S>> index, Function<S, Set<K>> lexer) {
        Set<K> lexemes = lexer.apply(phrase);
        addTokenListForOneKeyInStore(lexemes, phrase, index);
    }

    //region API GET todo вынести в другую сущность

    /**
     * Точное совпадение для одного токена
     *
     * @param token ключе для поиска
     * @param index структура в которой ищем
     * @return множество фраз ответов todo null или set пустой
     */
    public Set<S> fullMatchSearchByToken(K token, Index<K, Set<S>> index) {
        Set<S> s = index.get(token);
        if (Objects.isNull(s))
            s = Collections.<S>emptySet();
        return s;

    }


    public Set<S> fullMatchSearchByTokenList(Collection<K> tokens, Index<K, Set<S>> index) {
        return tokens.stream()
                .map(k -> fullMatchSearchByToken(k, index))
                .reduce((s, s2) -> {
                    s2.retainAll(s);
                    return s2;
                })
                .orElse(Collections.<S>emptySet());
    }





    //TODO 2 метода с триМап и ВСЁ!


    //endregion
}




