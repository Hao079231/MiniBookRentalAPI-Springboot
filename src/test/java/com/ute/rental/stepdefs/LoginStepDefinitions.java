package com.ute.rental.stepdefs;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;

import io.cucumber.java.Before;
import io.cucumber.java.vi.Cho;
import io.cucumber.java.vi.Khi;
import io.cucumber.java.vi.Thì;
import io.cucumber.java.vi.Và;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class LoginStepDefinitions {

  private String apiUrl;
  private Response response;

  @Before
  public void setup() {
    RestAssured.baseURI = "http://localhost:8383";
  }

  @Cho("API {string} đang sẵn sàng")
  public void apiIsReady(String path) {
    this.apiUrl = path;
    if (this.apiUrl == null || this.apiUrl.trim().isEmpty()) {
      throw new IllegalArgumentException("API path không được null hoặc rỗng");
    }
  }

  @Khi("client gửi yêu cầu POST với nội dung JSON:")
  public void clientSendsPostRequestWithJson(String body) {
    if (apiUrl == null) {
      throw new IllegalStateException("API URL chưa được gán. Hãy chắc rằng bạn có bước 'Given API ... đang sẵn sàng'");
    }

    response = given()
        .contentType("application/json")
        .body(body)
        .when()
        .post(apiUrl)
        .then()
        .extract()
        .response();
  }

  @Thì("API trả về mã trạng thái {int}")
  public void apiReturnsStatusCode(int statusCode) {
    assertThat("Status code không đúng", response.getStatusCode(), is(statusCode));
  }

  @Và("phản hồi chứa {string}")
  public void responseContains(String message) {
    String body = response.getBody().asString();
    assertThat("Phản hồi không chứa nội dung mong đợi", body, containsString(message));
  }

  @Và("phản hồi có trường {string}")
  public void responseHasField(String field) {
    String[] parts = field.split("\\.");
    if (parts.length == 2) {
      assertThat(response.jsonPath().getMap(parts[0]), hasKey(parts[1]));
    } else {
      assertThat(response.jsonPath().get(), hasKey(field));
    }
  }
}
