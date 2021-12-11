package jfts.egine;

import jfts.index.base.Index;

import java.util.Set;

public interface Engine<K,S,V> {
//    //TODO разделить операции добавления в Store хранилище и в Index

    //TODO разделить операции добавления в Store и в Index
    public void index(K tokenKey, V phraseValue, Index<K, V> index);


    /**
     * добавляет список токенов в индекс для одного значения(фразы)
     * @param tokenKeySet
     * @param phraseValue
     * @param index
     */
    public default void addTokenListForOnePhraseInStore(Set<K> tokenKeySet, V phraseValue, Index<K, V> index) {
        tokenKeySet.forEach(token ->
                index(token, phraseValue, index));
    }
}




