package jfts.index.base;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Основной контейнер для FTS поиска. Set означает set как хранимое значение контейнера
 * Хранимое значение - множество, т.е. значения не дублируются
 * @param <K>
 * @param <V>
 */
public class TreeSetIndex<K extends Comparable<? extends K>,V extends Set<S>,S> extends TreeIndex<K,V,S> {

    public void index(K tokenKey, S phraseValue) {
        //TODO переписать логику на computeIfAbsent?
        V value = get(tokenKey);
        if ((value == null)) { //ключ-токен существует?
            V phraseSet = (V) new HashSet<S>();
            phraseSet.add(phraseValue);
            put(tokenKey, phraseSet);
        } else
        value.add(phraseValue); //добавляем к существующему списку значение
    }

    //region FTS API

    /**
     * поиск основван на Treemap и и методе Compare
     *
     * @param tokenPrefix    поиск ОТ
     * @param maxValueOfType поиск ДО
     * @return субМапа ОТ ДО
     */
    public SortedMap<K, V> prefixMatchSearch(K tokenPrefix, K maxValueOfType) {
        //todo переделать на TAOAL
//         return treeMap.tailMap(tokenPrefix,true);
        return treeMap.subMap(tokenPrefix, maxValueOfType);
    }

    /**
     * выводит Макс значение
     * @param tokenPrefix
     * @return
     *///todo вынести обработчик типов?
    public SortedMap<K, V> prefixMatchSearch(K tokenPrefix) {
        //TODO инициализировать ОДИН РАЗ ПРИ СОЗДАНИИ ОБЪЕКТА
        return prefixMatchSearch(tokenPrefix, () -> maxValueForType(tokenPrefix));
    }

    /**
     * @param tokenPrefix
     * @param maxValueForSupplier поставщик
     * @return
     */
    public SortedMap<K, V> prefixMatchSearch(K tokenPrefix, Supplier<K> maxValueForSupplier) {
        return prefixMatchSearch(tokenPrefix, maxValueForSupplier.get());
    }

    /**
     * чую пригодится
     *
     * @param tokenPrefix
     * @param maxValueFunction
     * @return
     */
    public SortedMap<K, V> prefixMatchSearch(K tokenPrefix, Function<K, K> maxValueFunction) {
        return prefixMatchSearch(tokenPrefix, maxValueFunction.apply(tokenPrefix));
    }

    /**
     * НЕоптимальный алгоритм. Т.к. находит полное пересечение списков -?
     *
     * @param tokenPrefixList список префиксов токенов
     * @return пересечение всех префиксов
     */
    public Set<S> interseсtOfPrefixSearch(List<K> tokenPrefixList) {
        return tokenPrefixList.stream()
                .map(k -> prefixMatchSearch(k))
                .map(kvSortedMap -> kvSortedMap.entrySet())
                .flatMap(Set::stream)
                .map(Map.Entry::getValue)
                .reduce((s, s2) -> {
                    s2.retainAll(s);
                    return s2;
                })
                .orElse((V) Collections.<S>emptySet());
    }

    /**
     * TODO инициализировать ОДИН РАЗ ПРИ СОЗДАНИИ ОБЪЕКТА
     * todo можно вынети куда-нибудь, а также хоорошо сюда дложиться стратегия для каждого типа
     * todo то есть чекать для каждого типа, является ли он определенным типом
     * Pattern matching для бедных
     * todo забить для всех типов базовых
     * @param fromValue исходное значение
     * @return
     */
    K maxValueForType(K fromValue) {
        if (fromValue instanceof String) {
            return (K) (((String) fromValue) + Character.MAX_VALUE);
        } else
            return null;
    }

    /**
     * V - v extends Collection<S>
     * @param collection
     * @return
     */
    Set<S> toSet(Collection<V> collection){
        return new HashSet<S>((Collection<? extends S>) collection);
    }

    //endregion
}
