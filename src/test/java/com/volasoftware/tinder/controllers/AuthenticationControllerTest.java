package com.volasoftware.tinder.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volasoftware.tinder.constants.Constants;
import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.dtos.RegisterRequest;
import com.volasoftware.tinder.services.contracts.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {

    private static final String REGISTER_URI = "/api/v1/users/register";
    private static final String VERIFY_URI = "/api/v1/users/verify";
    private MockMvc mockMvc;
    @MockBean
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void givenVerificationTokenWhenEmailIsNotVerifiedThenSuccessfulVerificationMsg() throws Exception {

        //Given
        String token = "73275233-bf2a-4c60-b2c9-10d4d02d8160";

        // When
        given(authenticationService.verify(any(String.class))).willReturn(Constants.VERIFIED);

        // Then
        mockMvc
                .perform(get(VERIFY_URI)
                        .param("token", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").value(Constants.VERIFIED));
    }

    @Test
    void givenRegistrationDetailsWhenEmailNotExistThenCreatedAccount() throws Exception {

        //Given
        RegisterRequest registerRequest = new RegisterRequest(
                "John",
                "Doe",
                "john@gmail.com",
                "password",
                Gender.MALE);
        AccountDto accountDto = new AccountDto(
                "John",
                "Doe",
                "john@gmail.com",
                Gender.MALE);

        // When
        given(authenticationService.register(any(RegisterRequest.class))).willReturn(accountDto);

        // Then
        mockMvc
                .perform(post(REGISTER_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.firstName").value("John"))
                .andExpect(jsonPath("$.body.email").value("john@gmail.com"));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
