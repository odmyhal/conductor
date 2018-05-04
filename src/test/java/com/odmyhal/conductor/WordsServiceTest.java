package com.odmyhal.conductor;

import com.odmyhal.conductor.words.WordsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class})
public class WordsServiceTest {

    @Autowired
    @Qualifier("wordsKeeper")
    private WordsService wordsService;

    @Before
    public void enrich(){
        wordsService.reset();
        wordsService.putDocument("one", "Hello world");
        wordsService.putDocument("two", "The world is beautiful");
        wordsService.putDocument("three", "Hello my dear friend");
        wordsService.putDocument("fore", "The weather is nice");
        wordsService.putDocument("five", "This is my world and");
        wordsService.putDocument("six", "The sun");
        wordsService.putDocument("seven", "My sun is huge");
    }

    @Test
    public void testMy(){
        Set<String> myDocuments = wordsService.searchDocument("my");
        assertEquals(2, myDocuments.size());
        assertTrue(myDocuments.contains("three"));
        assertTrue(myDocuments.contains("five"));
    }

    @Test
    public void testHello(){
        Set<String> myDocuments = wordsService.searchDocument("Hello");
        assertEquals(2, myDocuments.size());
        assertTrue(myDocuments.contains("three"));
        assertTrue(myDocuments.contains("one"));
    }

    @Test
    public void testHelloMy(){
        Set<String> myDocuments = wordsService.searchDocument("Hello", "my");
        assertEquals(1, myDocuments.size());
        assertTrue(myDocuments.contains("three"));
    }

    @Test
    public void testTheIs(){
        Set<String> myDocuments = wordsService.searchDocument("The", "is");
        assertEquals(2, myDocuments.size());
        assertTrue(myDocuments.contains("two"));
        assertTrue(myDocuments.contains("fore"));
    }

    @Test
    public void emptyTest(){
        Set<String> myDocuments = wordsService.searchDocument("empty");
        assertTrue(myDocuments.isEmpty());
    }

    @Test
    public void getTest(){
        assertEquals("The sun", wordsService.getDocument("six"));
        assertEquals("Hello my dear friend", wordsService.getDocument("three"));
    }

    @Test
    public void emptySunTest(){
        Set<String> myDocuments = wordsService.searchDocument("sun", "is", "my");
        assertTrue(myDocuments.isEmpty());
    }

    @Test
    public void worldTest(){
        Set<String> myDocuments = wordsService.searchDocument("world", "is", "my");
        assertEquals(1, myDocuments.size());
        assertTrue(myDocuments.contains("five"));
    }
}
