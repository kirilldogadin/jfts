package jfts.index.base;

import jfts.index.base.TreeIndex;
import jfts.index.base.TreeSetIndex;

import java.util.Set;
import java.util.SortedMap;

import static java.util.Arrays.asList;

public class TestTreeSetIndex {

    public static void main(String[] args) {
        TreeSetIndex<String, Set<String>, String> index = new TreeSetIndex<>();


        index.index("123", "123 123");
        index.index("124", "124");
        index.index("125", "125 123");

        System.out.println(index.get("123"));

        index.addTokensForOnePhrase((asList("t1", "t2")), "t1 t2");
        index.addTokensForOnePhrase((asList("t1", "t3", "t4")), "t1 t3 t4");
        index.addTokensForOnePhrase((asList("t3", "t4", "t5")), "t3 t4 t5");

        System.out.println(index.get("t1"));
        System.out.println(index.get("t2"));

        SortedMap<String, Set<String>> search = index.prefixMatchSearch("12", "12" + Character.MAX_VALUE);
        System.out.println(search);

        search = index.prefixMatchSearch("12");
        System.out.println(search);

        search = index.prefixMatchSearch("12");
        System.out.println(search);

        search = index.prefixMatchSearch("t3");
        System.out.println(search);

        System.out.println("Пересечение t1 и t3");
        Set<String> strings = index.interseсtOfPrefixSearch(asList("t1", "t3"));
        System.out.println(strings);

//        index.interseсtOfPrefixSearch(asList("12"))

    }

    static TreeIndex createIndex() {
        return new TreeSetIndex<String, Set<String>, String>();
    }
}
