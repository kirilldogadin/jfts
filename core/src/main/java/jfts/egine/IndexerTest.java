package jfts.egine;

import jfts.index.base.Index;
import jfts.index.base.TreeIndex;
import jfts.index.base.TreeSetIndex;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public class IndexerTest {
    public static void main(String[] args) {
        IndexerImpl engine = new IndexerImpl();
        Index index = createIndex();
        engine.index("123","123 123",index);

        System.out.println(index.get("123"));
    }

    static Index createIndex(){
        TreeIndex<String,Set<String>,String> index = new TreeSetIndex<>();
        HashSet<String> strings = new HashSet<>(asList("азс дом улица","азс улица"));
        index.put("азс",strings);
        return index;
    }
}
