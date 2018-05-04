package com.odmyhal.conductor.words;

import java.util.Set;

public interface WordsService {

    void putDocument(String key, String document);

    String getDocument(String key);

    Set<String> searchDocument(String ... word);

    void reset();
}
