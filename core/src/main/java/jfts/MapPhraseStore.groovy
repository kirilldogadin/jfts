package jfts

import jfts.store.phrase.PhraseStore

class MapPhraseStore implements PhraseStore<Long,Container> {

    Map<Long,Container> store = new HashMap()

    @Override
    void put(Long key, Container value) {
        store.put(key,value)
    }

    @Override
    Container get(Long key) {
        store.get(key)
    }

}
