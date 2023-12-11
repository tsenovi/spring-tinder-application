package com.volasoftware.tinder.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volasoftware.tinder.constants.AccountConstant;
import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.dtos.LoginRequest;
import com.volasoftware.tinder.dtos.RegisterRequest;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.responses.LoginResponse;
import com.volasoftware.tinder.services.contracts.AuthenticationService;
import com.volasoftware.tinder.services.contracts.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.Month;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {

    private static final String REGISTER_URI = "/api/v1/users/register";
    private static final String LOGIN_URI = "/api/v1/users/login";

    public static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(
            2023,
            Month.DECEMBER,
            7,
            12,
            30,
            00,
            50000);
    private static final String FIRST_NAME = "Test";
    private static final String EMAIL = "Test_Test@gmail.com";
    private static final Long ID = 1L;
    private static final String LAST_NAME = "Test";
    private static final String PASSWORD = "password";

    private MockMvc mockMvc;
    @MockBean
    private AuthenticationService authenticationService;
    @Autowired
    private JwtService jwtService;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
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
                Gender.MALE,
                false,
                false);

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

    @Test
    void testLoginWhenAccountVerifiedThenSuccessfulOperation() throws Exception {

        //Given
        LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);
        Account account = generateAccount();
        String jwtToken = jwtService.generateToken(account);
        account.setVerified(true);
        LoginResponse loginResponse = new LoginResponse(jwtToken);

        // When
        given(authenticationService.login(loginRequest)).willReturn(loginResponse);

        // Then
        mockMvc
                .perform(post(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(AccountConstant.LOGGED_IN));
    }

    private Account generateAccount() {
        Account account = new Account();
        account.setId(ID);
        account.setFirstName(FIRST_NAME);
        account.setLastName(LAST_NAME);
        account.setEmail(EMAIL);
        account.setPassword(PASSWORD);
        account.setCreatedDate(LOCAL_DATE_TIME);
        account.setLastModifiedDate(LOCAL_DATE_TIME);
        account.setGender(Gender.MALE);
        return account;
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
