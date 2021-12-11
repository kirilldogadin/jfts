package jfts.index.base;

import java.util.Collection;
import java.util.HashSet;
import java.util.TreeMap;

/**
 * Индекс на основе дерева. Сложность доступа log(n)
 *
 * @param <K> ключ extends Comparable, because для Tree важно иметь сравнение. todo добавить возможность передавать как компаратор
 * @param <V> значение возвращаемое по ключу. Может быть, например, список
 * @param <S> сохраняемое значение
 */
//TODO вынести V, сузить
public abstract class TreeIndex<K extends Comparable<? extends K>, V extends Collection<S>, S> implements Index<K, V> {

    protected final TreeMap<K, V> treeMap;

    /**
     * @param treeMap можно передать ConcurrentHashMap
     */
    public TreeIndex(TreeMap<K, V> treeMap) {
        this.treeMap = treeMap;
    }

    public TreeIndex() {
        treeMap = new TreeMap<>();
    }

    @Override
    public V get(K key) {
        return treeMap.get(key);
    }

    @Override
    public void put(K key, V value) {
        treeMap.put(key, value);
    }

    /**
     *
     * @param tokenKey
     * @param phraseValue
     */
    abstract public void index(K tokenKey, S phraseValue) ; //добавляем к существующему списку значение


    public void addTokensForOnePhrase(Collection<K> tokens, S phraseValue) {
        tokens.forEach(token ->
                index(token, phraseValue));
    }

    //TODO использовать в потомках
    protected V getContainer() {
        return (V) new HashSet();
    }

    /**
     * TODO для String второй аргуент вшить
     * поиск основван на Treemap и и методе Compare
     *
     * @param tokenPrefix    поиск ОТ
     * @param maxValueOfType поиск ДО
     * @return субМапа ОТ ДО
     */


};
