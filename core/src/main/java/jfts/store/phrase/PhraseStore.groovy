package jfts.store.phrase

import jfts.store.Store

interface PhraseStore<K,V> extends Store{
    void put(K key, V value)
    V get(K key)
}