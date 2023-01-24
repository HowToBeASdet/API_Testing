package com.howtobeasdet.listener;

import com.howtobeasdet.base.BaseTest;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener extends BaseTest implements ITestListener {

    /*
    Este metodo se ejecuta antes de cada prueba, extrae el nombre de la prueba que se esta ejecutando desde el  objeto result.
     */
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getName();
        extent.attachReporter(spark);
        reportLogger = extent.createTest(testName);
        reportLogger.info("Comenzando a ejercutar nuestras pruebas...");
    }

    /*
    Una vez ejecutada la prueba, si el resultado fue positivo, ejecutara el codigo dentro del bloque.
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getName();
        reportLogger.info("Se ejecuto prueba: ".concat(testName));
        reportLogger.pass("Tu test ha funcionado, excelente");
    }

    /*
        Una vez ejecutada la prueba, si el resultado fue negativo, ejecutara el codigo dentro del bloque.
     */
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getName();
        reportLogger.info("Se ejecuto prueba:".concat(testName));
        reportLogger.fail("Tu test ah fallado, deberias revisarlo");
    }

    /*
        Una vez ejecutada la prueba, se ejecutara el codigo dentro del bloque.
     */
    @Override
    public void onFinish(ITestContext context) {
        //Vacia todos los resultados dentro del reporte.
        extent.flush();
    }
}
