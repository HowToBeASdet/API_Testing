package com.howtobeasdet.todolistapi;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.howtobeasdet.base.BaseTest.reportLogger;
import static io.restassured.RestAssured.given;

public class Task {

    private String name = null;
    private String userName = null;
    private String token = null;
    private String userToken = null;
    private String userId = null;
    private String taskId = null;

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = "https://api-nodejs-todolist.herokuapp.com";
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
    @Test(priority = 2)
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

    @Test(priority = 3)
    public void addTaskTest() {
        Response response = given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", userToken))
                .body("{\n" +
                        "\t\"description\": \"Tender la cama\"\n" +
                        "}")
                .post("/task")
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        JsonPath path = response.jsonPath();
        Assert.assertEquals(path.get("success"), true, "Se esperaba que el resultado de la operacion fuera success");
        Assert.assertEquals(path.get("data.completed"), false, "Se esperaba que el valor de 'data.completed' fuera falso");
        Assert.assertEquals(path.get("data.description").toString(), "Tender la cama", "Se esperaba que el valor de 'data.description' fuera 'Tender la cama'");
        taskId = path.get("data._id").toString();
    }

    @Test(priority = 4)
    public void getAllTaskTest() {
        Response response = given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", userToken))
                .get("/task")
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        JsonPath path = response.jsonPath();
        Assert.assertEquals(path.get("data[0].completed"), false, "Se esperaba que el valor de 'data.completed' fuera falso");
        Assert.assertEquals(path.get("data[0].description").toString(), "Tender la cama", "Se esperaba que el valor de 'data.description' fuera 'Tender la cama'");
    }

    @Test(priority = 5)
    public void addASecondTaskTest() {
        Response response = given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", userToken))
                .body("{\n" +
                        "\t\"description\": \"Bañarnos\"\n" +
                        "}")
                .post("/task")
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        JsonPath path = response.jsonPath();
        Assert.assertEquals(path.get("success"), true, "Se esperaba que el resultado de la operacion fuera success");
        Assert.assertEquals(path.get("data.completed"), false, "Se esperaba que el valor de 'data.completed' fuera falso");
        Assert.assertEquals(path.get("data.description").toString(), "Bañarnos", "Se esperaba que el valor de 'data.description' fuera 'Bañarnos'");
    }

    @Test(priority = 6)
    public void getAllTaskAgainTest() {
        Response response = given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", userToken))
                .get("/task")
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        JsonPath path = response.jsonPath();
        Assert.assertEquals(path.get("data[0].completed"), false, "Se esperaba que el valor de 'data.completed' fuera falso");
        Assert.assertEquals(path.get("data[0].description").toString(), "Tender la cama", "Se esperaba que el valor de 'data.description' fuera 'Tender la cama'");

        Assert.assertEquals(path.get("data[1].completed"), false, "Se esperaba que el valor de 'data.completed' fuera falso");
        Assert.assertEquals(path.get("data[1].description").toString(), "Bañarnos", "Se esperaba que el valor de 'data.description' fuera 'Bañarnos'");
    }

    @Test(priority = 7)
    public void getTaskByIdTest() {
        Response response = given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", userToken))
                .get("/task".concat("/").concat(taskId))
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        JsonPath path = response.jsonPath();
        Assert.assertEquals(path.get("success"), true, "Se esperaba que el resultado de la operacion fuera success");
        Assert.assertEquals(path.get("data.completed"), false, "Se esperaba que el valor de 'data.completed' fuera falso");
        Assert.assertEquals(path.get("data.description").toString(), "Tender la cama", "Se esperaba que el valor de 'data.description' fuera 'Tender la cama'");
    }

    @Test(priority = 8)
    public void getTaskByCompletedTask() {
        Response response = given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", userToken))
                .get("/task".concat("?").concat("completed=true"))
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        JsonPath path = response.jsonPath();
        Assert.assertEquals((Integer) path.get("count"), 0, "Se esperaba que el valor de count fuera igual a 0");
        //Assert.assertEquals(path.get("data"), new Object()[], "Se esperaba que el valor de 'data.completed' fuera falso");
    }

    @Test(priority = 9)
    public void getTaskByPaginationTest() {
        Response response = given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", userToken))
                .get("/task".concat("?").concat("limit=1").concat("&").concat("skip=0"))
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        JsonPath path = response.jsonPath();
        Assert.assertEquals(path.get("data[0].completed"), false, "Se esperaba que el valor de 'data.completed' fuera falso");
        Assert.assertEquals(path.get("data[0].description").toString(), "Tender la cama", "Se esperaba que el valor de 'data.description' fuera 'Tender la cama'");
    }

    @Test(priority = 10)
    public void getTaskByPaginationAgainTest() {
        Response response = given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", userToken))
                .get("/task".concat("?").concat("limit=2").concat("&").concat("skip=0"))
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        JsonPath path = response.jsonPath();
        Assert.assertEquals(path.get("data[0].completed"), false, "Se esperaba que el valor de 'data.completed' fuera falso");
        Assert.assertEquals(path.get("data[0].description").toString(), "Tender la cama", "Se esperaba que el valor de 'data.description' fuera 'Tender la cama'");

        Assert.assertEquals(path.get("data[1].completed"), false, "Se esperaba que el valor de 'data.completed' fuera falso");
        Assert.assertEquals(path.get("data[1].description").toString(), "Bañarnos", "Se esperaba que el valor de 'data.description' fuera 'Bañarnos'");
    }

    @Test(priority = 11)
    public void updateTaskByIdTest() {
        Response response = given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", userToken))
                .body("{\n" +
                        "\t\"completed\": true\n" +
                        "}")
                .put("/task".concat("/").concat(taskId))
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        JsonPath path = response.jsonPath();
        Assert.assertEquals(path.get("success"), true, "Se esperaba que el resultado de la operacion fuera success");
        Assert.assertEquals(path.get("data.completed"), true, "Se esperaba que el valor de 'data.completed' fuera falso");
        Assert.assertEquals(path.get("data.description").toString(), "Tender la cama", "Se esperaba que el valor de 'data.description' fuera 'Tender la cama'");
    }

    @Test(priority = 12)
    public void getTaskByCompletedAgainTest() {
        Response response = given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", userToken))
                .get("/task".concat("?").concat("completed=true"))
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        JsonPath path = response.jsonPath();
        Assert.assertEquals((Integer) path.get("count"), 1, "Se esperaba que el valor de count fuera igual a 1");
        Assert.assertEquals(path.get("data[0].completed"), true, "Se esperaba que el valor de 'data.completed' fuera falso");
        Assert.assertEquals(path.get("data[0].description").toString(), "Tender la cama", "Se esperaba que el valor de 'data.description' fuera 'Tender la cama'");
    }

    @Test(priority = 13)
    public void deleteTaskByIdTest() {
        Response response = given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", userToken))
                .delete("/task".concat("/").concat(taskId))
                .thenReturn();

        reportLogger.info(response.getBody().asString());

        JsonPath path = response.jsonPath();
        Assert.assertEquals(path.get("success"), true, "Se esperaba que el resultado de la operacion fuera success");
    }
}
