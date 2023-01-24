package com.howtobeasdet.base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class BaseTest {
    /*
    Crea el modelo del reporte y lo guarda en la ubicacion especificada
     */
    public static ExtentSparkReporter spark = new ExtentSparkReporter("reportes/Resultados_De_Pruebas.html");
    public static ExtentReports extent = new ExtentReports();
    /*
    Este es nuestro logger para añaadir, comentarios y logs personalizados dentro del reporte.
    */
    public static ExtentTest reportLogger;


}
