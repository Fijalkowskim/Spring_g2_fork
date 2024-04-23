package com.example.demo.testExample;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestLifeCycle {
    @BeforeEach
    public void setUp(){
        System.out.println("ustawienia");
        System.out.flush();// wyczyszczenie bufora
    }
    @AfterEach
    public void tearDown(){
        System.out.println("zakończenie");
        System.out.flush();// wyczyszczenie bufora
    }
    @BeforeAll
    public static void setUpClass(){
        System.out.println("ustawienia klasy");
        System.out.flush();
    }
    @AfterAll
    public static void tearDownClass(){
        System.out.println("Zakonczenie klasy");
        System.out.flush();
    }


    @Test
    public void test1(){
        System.out.println("Test1");
        System.out.flush();// wyczyszczenie bufora
    }
    @Test
    public void test2(){
        System.out.println("Test2");
        System.out.flush();// wyczyszczenie bufora
    }
}
