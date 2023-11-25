package com.volasoftware.tinder.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.dtos.AccountDTO;
import com.volasoftware.tinder.dtos.RegisterRequest;
import com.volasoftware.tinder.services.contracts.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthenticationController.class)
@ComponentScan(basePackages = "com.volasoftware.tinder")
@TestPropertySource(locations = "classpath:test.properties")
@AutoConfigureMockMvc(addFilters = false)

public class AuthenticationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  @MockBean
  private AccountService accountService;


//  TODO fix the test method - Consider defining a bean of type 'javax.sql.DataSource' in your configuration.
  @Test
  void givenRegistrationDetailsWhenEmailNotExistThenCreatedAccount() throws Exception {

    //Given
    RegisterRequest registerRequest = new RegisterRequest(
        "John",
        "Doe",
        "john@gmail.com",
        "password",
        Gender.MALE);
    AccountDTO accountDTO = new AccountDTO(
        "John",
        "Doe",
        "john@gmail.com",
        Gender.MALE);

    // When
    given(accountService.register(any(RegisterRequest.class))).willReturn(accountDTO);

    mockMvc.perform(post("/api/v1/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(registerRequest))) // Attach the object to the request
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.firstName").value("John"))
        .andExpect(jsonPath("$.data.email").value("john@gmail.com"));

    // Then
    verify(accountService, times(1)).register(any(RegisterRequest.class));
  }

  private static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
