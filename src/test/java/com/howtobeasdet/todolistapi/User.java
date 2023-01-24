package com.howtobeasdet.todolistapi;

import groovyjarjarantlr4.v4.codegen.model.SrcOp;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.UUID;

import static com.howtobeasdet.base.BaseTest.reportLogger;
import static io.restassured.RestAssured.given;

public class User {

    private String name = null;
    private String userName = null;
    private String token = null;
    private String userToken = null;
    private String userId = null;

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = "http://localhost:8080";
        name = "usuario_prueba_".concat(UUID.randomUUID().toString());
        userName = name.concat("@gmail.com");
    }

    /**
     * Registrar un nuevo Usuario
     */
    @Test(priority = 1)
    public void registerUserTest() {
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "\t\"name\":\"" + name + "\",\n" +
                        "\t\"email\":\"" + userName + "\",\n" +
                        "\t\"password\": \"12345678\",\n" +
                        "\t\"age\": 20\n" +
                        "}")
                .post("/user/register")
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        JsonPath path = response.jsonPath();
        Assert.assertEquals(path.get("user.age").toString(), "20", "La edad esperada del usuario es de 20 años");
        Assert.assertEquals(path.get("user.name").toString(), name, "Se esperaba que el nombre del usuario fuera: ".concat(name));
        Assert.assertEquals(path.get("user.email").toString(), userName, "Se esperaba que el email del usuario fuera: ".concat(userName));
        Assert.assertEquals((Integer) path.get("user.__v"), 1, "Se esperaba que la version fuera la numero 1");
        token = "Bearer ".concat(path.get("token").toString());
        userId = path.get("user._id");
    }

    /**
     * Loguearnos como usuario, en la aplicacion
     */
    //@Test(priority = 2)
    public void loginTest() {
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "\t\"email\": \"" + userName + "\",\n" +
                        "\t\"password\": \"12345678\"\n" +
                        "}")
                .post("/user/login")
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        JsonPath path = response.jsonPath();
        Assert.assertEquals(path.get("user.age").toString(), "20", "La edad esperada del usuario es de 20 años");
        Assert.assertEquals(path.get("user.name").toString(), name, "Se esperaba que el nombre del usuario fuera: ".concat(name));
        Assert.assertEquals(path.get("user.email").toString(), userName, "Se esperaba que el email del usuario fuera: ".concat(userName));
        Assert.assertEquals((Integer) path.get("user.__v"), 2, "Se esperaba que la version fuera la numero 2");
        userToken = "Bearer ".concat(path.get("token").toString());
    }

    /**
     * Des loguearnos como usuario, en la apalicacion
     */
    //@Test(priority = 3)
    public void logoutTest() {
        Response response = given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", userToken))
                .post("/user/logout")
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        JsonPath path = response.jsonPath();
        Assert.assertEquals(path.get("success"), true, "Se esperaba un success");
    }

    /**
     * Loguearnos en la aplicacion utilizando nuestro token de usuario, que se obtiene al registrarnos en la aplicacion
     */
    //@Test(priority = 4)
    public void getLoggedInUserViaTokenTest() {
        Response response = given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", token))
                .get("/user/me")
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        JsonPath path = response.jsonPath();
        Assert.assertEquals(path.get("age").toString(), "20", "La edad esperada del usuario es de 20 años");
        Assert.assertEquals(path.get("name").toString(), name, "Se esperaba que el nombre del usuario fuera: ".concat(name));
        Assert.assertEquals(path.get("email").toString(), userName, "Se esperaba que el email del usuario fuera: ".concat(userName));
        Assert.assertEquals((Integer) path.get("__v"), 3, "Se esperaba que la version fuera la numero 3");
    }

    /**
     * Actualizar nuestros datos en la aplicacion
     */
    //@Test(priority = 5)
    public void updateUserProfileTest() {

        name = "usuario_prueba_".concat(UUID.randomUUID().toString());

        Response response = given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", token))
                .body("{\n" +
                        "    \"age\": 30,\n" +
                        "    \"name\": \"" + name + "\"\n" +
                        "}")
                .put("/user/me")
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        JsonPath path = response.jsonPath();
        Assert.assertEquals(path.get("data.age").toString(), "30", "La edad esperada del usuario es de 20 años");
        Assert.assertEquals(path.get("data.name").toString(), name, "Se esperaba que el nombre del usuario fuera: ".concat(name));
        Assert.assertEquals(path.get("data.email").toString(), userName, "Se esperaba que el email del usuario fuera: ".concat(userName));
        Assert.assertEquals((Integer) path.get("data.__v"), 3, "Se esperaba que la version fuera la numero 3");
        Assert.assertEquals(path.get("success"), true, "Se esperaba un success");
    }

    /**
     * Actualizar nuestros avatar/foto de perfil en la aplicacion
     */
    //@Test(priority = 6)
    public void uploadImage() {
        Response response = given()
                .contentType(ContentType.MULTIPART)
                .header(new Header("Authorization", token))
                .multiPart("avatar", new File("./src/test/resources/apple.jpeg"))
                .post("/user/me/avatar")
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        JsonPath path = response.jsonPath();
        Assert.assertEquals(path.get("succes"), true, "Se esperaba un success");
    }

    /**
     * Descargar nuestro imagen de perfil/avatar desde la aplicacion
     */
    //@Test(priority = 7)
    public void getUserImageTest() {
        Response response = given()
                .header(new Header("Authorization", token))
                .get("/user/".concat(userId).concat("/avatar"))
                .thenReturn();

        reportLogger.info(response.getBody().asString());
    }

    /**
     * Borrar nuestro imagen de perfil/avatar de la aplicacion
     */
    //@Test(priority = 8)
    public void deleteImageTest() {
        Response response = given()
                .header(new Header("Authorization", token))
                .delete("/user/me/avatar")
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        JsonPath path = response.jsonPath();
        Assert.assertEquals(path.get("success"), true, "Se esperaba un success");
    }

    /**
     * Borrar nuestro usuario de la aplicacion
     */
    //@Test(priority = 9)
    public void deleteUserTest() {
        Response response = given()
                .header(new Header("Authorization", token))
                .delete("/user/me")
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        JsonPath path = response.jsonPath();
        Assert.assertEquals(path.get("age").toString(), "30", "La edad esperada del usuario es de 20 años");
        Assert.assertEquals(path.get("name").toString(), name, "Se esperaba que el nombre del usuario fuera: ".concat(name));
        Assert.assertEquals(path.get("email").toString(), userName, "Se esperaba que el email del usuario fuera: ".concat(userName));
        Assert.assertEquals((Integer) path.get("__v"), 3, "Se esperaba que la version fuera la numero 3");
    }

    /**
     * Actualizar nuestros datos en la aplicacion, se espera que falle por que nuestro token ya ha expirado
     */
    //@Test(priority = 10)
    public void updateUserProfile_NegativeScenarioTest() {

        name = "usuario_prueba_".concat(UUID.randomUUID().toString());

        Response response = given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", userToken))
                .body("{\n" +
                        "    \"age\": 30,\n" +
                        "    \"name\": \"" + name + "\"\n" +
                        "}")
                .put("/user/me")
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        JsonPath path = response.jsonPath();
        Assert.assertEquals(path.get("error").toString(), "Please authenticate.", "Se esperaba mensaje de: ".concat("Please authenticate."));
    }

    /**
     * Loguearnos como usuario, en la aplicacion, se espera que falle por que nuestro usuario no existe
     */
    //@Test(priority = 11)
    public void login_NegativeScenarioTest() {

        name = "usuario_prueba_".concat(UUID.randomUUID().toString());
        userName = name.concat("@gmail.com");

        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "\t\"email\": \"" + userName + "\",\n" +
                        "\t\"password\": \"12345678\"\n" +
                        "}")
                .post("/user/login")
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        Assert.assertEquals(response.getBody().asString(), "\"Unable to login\"", "Se espera mensaje: Unable to login");
    }
}
