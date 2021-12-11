package jfts.index.base;

import java.util.List;

/**
 * List означает list как хранимое значение контейнера. т.е. значения могут повторяться
 * Хранимое значение - множество, т.е. значения не дублируются
 * @param <K>
 * @param <V>
 */
public class TreeListIndex<K extends Comparable<K>,V extends List<S>,S> extends TreeIndex<K,V,S> {
    @Override
    public void index(K tokenKey, S phraseValue) {

    }
}
