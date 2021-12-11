package jfts.lexer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class Lexer {


    //TODO оптимизировать или переписать на готовые либы?
    public Set<String> extractLexemes(String phrase) {
        StringTokenizer tokenizer =  new StringTokenizer(phrase);
        return new HashSet<String>(Arrays.asList(phrase.split(" ")));

    }

    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        Set<String> lexemes = lexer.extractLexemes(" sdf sdff f  lk dd dd d ddd dd d dddd dddd");
        System.out.println(lexemes);
    }
}
