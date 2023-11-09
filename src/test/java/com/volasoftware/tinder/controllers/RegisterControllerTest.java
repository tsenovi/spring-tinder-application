package com.volasoftware.tinder.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.dtos.AccountDTO;
import com.volasoftware.tinder.dtos.RegisterDTO;
import com.volasoftware.tinder.services.contracts.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RegisterController.class)
public class RegisterControllerTest {

  @Autowired
  private RegisterController registerController;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AccountService accountService;

  @Test
  void givenRegistrationDetailsWhenEmailNotExistThenCreatedAccount() throws Exception {

    //Given
    RegisterDTO registerDTO = new RegisterDTO(
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
    when(accountService.save(any(RegisterDTO.class))).thenReturn(accountDTO);

    mockMvc.perform(post("/api/v1/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(registerDTO))) // Attach the object to the request
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.firstName").value("John"))
        .andExpect(jsonPath("$.data.email").value("john@gmail.com"));

    // Then
    verify(accountService, times(1)).save(any(RegisterDTO.class));
  }

  private static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
