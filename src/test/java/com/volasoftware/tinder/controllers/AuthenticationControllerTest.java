package com.volasoftware.tinder.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.dtos.AccountDTO;
import com.volasoftware.tinder.dtos.RegisterRequest;
import com.volasoftware.tinder.services.contracts.AccountService;
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
    private MockMvc mockMvc;
    @MockBean
    private AccountService accountService;

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
        AccountDTO accountDTO = new AccountDTO(
                "John",
                "Doe",
                "john@gmail.com",
                Gender.MALE);

        // When
        given(accountService.register(any(RegisterRequest.class))).willReturn(accountDTO);

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
