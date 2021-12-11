package jfts.egine;

import jfts.index.base.Index;

import java.util.HashSet;
import java.util.Set;

public class IndexerImpl {
//    //TODO разделить операции добавления в Store хранилище и в Index

    //TODO разделить операции добавления в Store и в Index
    public void index(String tokenKey, String phraseValue, Index<String, Set<String>> index) {
        //TODO переписать логику на computeIfAbsent?

        if ((index.get(tokenKey) == null)) { //ключ-токен существует?
            HashSet<String> phraseSet = new HashSet<>();
            phraseSet.add(phraseValue);
            index.put(tokenKey, phraseSet);
            return;
        }

        if (index.get(tokenKey)!=null) { //если значения еще не дублируется  //TODO сделать эту проверку опциональной
            //todo log (дублирование или не надо?)
        } else
            //todo NPE
            index.get(tokenKey).add(phraseValue); //добавляем к существующему списку значение
    }

    public void addTokenListForOneKeyInStore(Set<String> tokenKeySet, String phraseValue, Index<String, Set<String>> index) {
        tokenKeySet.forEach(token ->
                index(token, phraseValue, index));
    }
}




