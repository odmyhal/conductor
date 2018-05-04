package com.odmyhal.conductor.words;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service("wordsKeeper")
public class WordsKeeper implements WordsService{

    private final AtomicInteger counter = new AtomicInteger();

    /**
     * Map each word to Integer in order to shrink memory usage
     */
    private final ConcurrentHashMap<String, Integer> words = new ConcurrentHashMap<String, Integer>();

    /**
     * backward mapping from int to initial word
     */
    private final ConcurrentHashMap<Integer, String> indexes = new ConcurrentHashMap<>();

    /**
     * store of entire documents for each key
     */
    private final ConcurrentHashMap<Integer, String> documents = new ConcurrentHashMap<>();

    /**
     * For each word-key-index is associated set of indexes each representing document comprising current key
     */
    private final ConcurrentHashMap<Integer, Set<Integer>> wordIndexDocument = new ConcurrentHashMap<>();

    public void putDocument(String key, String document) {
        int keyIndex = wordNumber(key.trim());
        for(String w : document.trim().split(" ")){
            int index = wordNumber(w.trim());
            Set<Integer> wordDocuments = wordIndexDocument.computeIfAbsent(index, i -> new HashSet());
            synchronized (wordDocuments){
                wordDocuments.add(keyIndex);
            }
        }
        documents.put(keyIndex, document);
    }

    public String getDocument(String key) {
        return documents.get(wordNumber(key.trim()));
    }

    public Set<String> searchDocument(String... word) {
        Set<Integer> scope = new HashSet();
        for(String w: word){
            scope = getWordScopedDocuments(scope, wordNumber(w.trim()));
            if(scope.isEmpty()){
                return new HashSet();
            }
        }

        return scope.stream().map(indexes::get).collect(Collectors.toSet());
    }

    /**
     * Method is not thread safe. provided for tests reason only
     */
    @Override
    public void reset() {
        words.clear();
        indexes.clear();
        documents.clear();
        wordIndexDocument.clear();
    }

    private int wordNumber(String word){
        return words.computeIfAbsent(word, k -> {
            int index = counter.incrementAndGet();
            indexes.put(index, word);
            return index;
        });
    }

    private Set<Integer> getWordScopedDocuments(Set<Integer> scope, Integer word){
        Set<Integer> wd = wordIndexDocument.getOrDefault(word, Collections.emptySet());
        if(wd.isEmpty()) {
            return wd;
        } else {
            synchronized (wd) {
                if(scope.isEmpty()){
                    return new HashSet<>(wd);
                }else{
                    scope.retainAll(wd);
                }
            }
            return scope;
        }
    }
}
