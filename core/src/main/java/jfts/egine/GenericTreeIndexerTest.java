package jfts.egine;

import jfts.index.base.Index;
import jfts.index.base.TreeSetIndex;
import jfts.lexer.Lexer;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public class GenericTreeIndexerTest {
    public static void main(String[] args) {
        GenericTreeIndexer<String ,Set<String>,String> engine = new GenericTreeIndexer<>();
        Index index = createIndex();
        engine.addValueByKey("123","123 123",index);

        System.out.println(index.get("123"));

        engine.addTokenListForOneKeyInStore(new HashSet(asList("t1","t2")),"t1 t2",index);

        System.out.println(index.get("t1"));
        System.out.println(index.get("t2"));

        Lexer lexer = new Lexer();

        engine.indexPhrase("салам как сам",index,s -> lexer.extractLexemes(s));
        System.out.println(engine.fullMatchSearchByToken("салам",index));
        System.out.println(engine.fullMatchSearchByToken("сам",index));


        Set<String> result = engine.fullMatchSearchByTokenList(asList("not exist token"), index);
        System.out.println(result);

        result = engine.fullMatchSearchByTokenList(asList("сам","салам"), index);
        System.out.println(result);
    }

    static Index createIndex(){
        return new TreeSetIndex<String, Set<String>, String>();
    }


}
