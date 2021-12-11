package jfts.index.type;

import jfts.index.base.Index;

public interface ContainedValueIndex<K,V,S> extends Index<K,V> {

    public void index(K tokenKey, S phraseValue, Index<K, V> index);

}
