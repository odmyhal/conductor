package com.odmyhal.conductor.controllers;

import com.odmyhal.conductor.words.WordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WordServiceController {
/*
    @PostConstruct
    public void enrich(){
        wordsService.putDocument("one", "Hello world");
        wordsService.putDocument("two", "The world is beautiful");
        wordsService.putDocument("three", "Hello my dear friend");
        wordsService.putDocument("fore", "The weather is nice");
        wordsService.putDocument("five", "This is my world and");
        wordsService.putDocument("six", "The sun");
        wordsService.putDocument("seven", "My sun is huge");
    }
*/
    @Autowired
    @Qualifier("wordsKeeper")
    private WordsService wordsService;

    @PutMapping(value="/putempty", produces = "application/json", consumes = MediaType.ALL_VALUE)
    public Object putDocumentEmpty(){
        System.out.println("Putting document: enpty");
        Map<String, String> response = new HashMap();
        response.put("ok", "empty document written");
        return response;
    }

    @PutMapping(value="/put", produces = "application/json", consumes = MediaType.ALL_VALUE)
    public Object putDocument(@RequestBody Map<String, String> body){
        String key = body.get("key");
        String document = body.get("document");
        check(key);
        check(document);
        Map<String, String> response = new HashMap();
        wordsService.putDocument(key, document);
        response.put("ok", "Document is written");
        return response;
    }

    private void check(String value){
        if(value == null || value.trim().equals("")){
            throw new RuntimeException("value is empty");
        }
    }

    @GetMapping("/get")
    public Object getDocument(@RequestParam String key){
        check(key);
        String document = wordsService.getDocument(key);
        check(document);
        Map<String, String> response = new HashMap();
        response.put("document", document);
        return response;
    }

    @PostMapping("/search")
    public Object searchDocuments(@RequestBody Map<String, String> body){
        String words = body.get("words");
        check(words);
        return wordsService.searchDocument(words.trim().split(" "));
    }

    @DeleteMapping("/reset")
    public Object resetDocuments(){
        wordsService.reset();
        Map<String, String> response = new HashMap();
        response.put("reset", "ok");
        return response;
    }
}
