package com.volasoftware.tinder.controllers;

import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.dtos.AccountDto;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VerificationControllerTest {

    private static final String VERIFY_URI = "/api/v1/verification/verify";
    private MockMvc mockMvc;
    @MockBean
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void givenVerificationTokenWhenEmailIsNotVerifiedThenSuccessfulVerificationMsg()
        throws Exception {

        //Given
        String token = "73275233-bf2a-4c60-b2c9-10d4d02d8160";

        AccountDto accountDto = new AccountDto(
            "John",
            "Doe",
            "john@gmail.com",
            Gender.MALE);

        // When
        given(authenticationService.verifyAccount(token)).willReturn(accountDto);

        // Then
        mockMvc
            .perform(get(VERIFY_URI)
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.body.email").value("john@gmail.com"));
    }

}