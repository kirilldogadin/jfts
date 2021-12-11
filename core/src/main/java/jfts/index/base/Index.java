package jfts.index.base;

/**
 * ХРАНИЛИЩЕ = индекс -
 * структура для хранения ССЫЛОК ( имеюттся в виду простые джава ссылки) на объекты
 *
 * @param <K> токен todo переименовать?
 * @param <V> фраза
 */
public interface Index<K,V> {

    //todo эти методы не  нужны, добавить метод index
    /**
     * @param key le
     * @return
     */
    V get(K key);

    /**
     *
     * @param key
     * @param value
     */
    void put(K key, V value);




}
