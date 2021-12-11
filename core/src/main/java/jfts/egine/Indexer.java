package jfts.egine;

import jfts.index.base.Index;

/**
 * Операции НАД индексом.
 *
 * @param <K> ключ поиска
 * @param <V> возвращаемое значение
 * @param <S> индексируемое значение, которое хотим положить.
 *            S Может не совпадать с V. Например S - string, а V - Set<String>
 */
public interface Indexer<K, S, V> {
    void index(K tokenKey, S phraseValue, Index<K, V> index);

}
