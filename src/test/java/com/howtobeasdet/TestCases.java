package com.howtobeasdet;

import org.testng.annotations.Test;

import static com.howtobeasdet.base.BaseTest.reportLogger;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TestCases {

    @Test
    public void primeraPrueba(){
        reportLogger.info("Hola Mundo");
        assertEquals("Hola Mundo", "Hola Mundo");
    }

    @Test
    public void segundaPrueba(){
        reportLogger.info("Hola Mundo desde la segunda prueba");
        assertEquals("Hola Mundo", "Hola_Mundo");
    }

    @Test
    public void terceraPrueba(){
        reportLogger.info("Validando que 2+2==4");
        assertTrue(2+2==4);
        reportLogger.info("Validando que 4+2==6");
        assertTrue(4+2==6);
        reportLogger.info("Validando que 6+2==8");
        assertTrue(6+2==8);
    }
}
