package jfts.index;

import jfts.index.base.Index;
import jfts.index.base.TreeSetIndex;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public class TestTreeIndex {
    public static void main(String[] args) {
        Index<String,Set<String>> index = new TreeSetIndex<>();
        HashSet<String> strings = new HashSet<>(asList("азс дом улица","азс улица"));
        index.put("азс",strings);

        System.out.println(index.get("азс"));

    }
}
