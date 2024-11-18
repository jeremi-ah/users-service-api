package com.example.demo;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.controller.UserController;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

@WebMvcTest(UserController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@ExtendWith(RestDocumentationExtension.class)
public class UserControllerDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "Jeremiah, wafula", "jeremiah@example.com");
    }

    @Test
    void createUser() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated()) // Ensure API returns 201 Created for POST
                .andDo(document("create-user",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("id").description("The user's ID"),
                                fieldWithPath("name").description("The user's name"),
                                fieldWithPath("email").description("The user's email")
                        ),
                        responseFields(
                                fieldWithPath("id").description("The user's ID"),
                                fieldWithPath("name").description("The user's name"),
                                fieldWithPath("email").description("The user's email")
                        )));
    }

    @Test
    void getUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andDo(document("get-user",
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("The user's ID")
                        ),
                        responseFields(
                                fieldWithPath("id").description("The user's ID"),
                                fieldWithPath("name").description("The user's name"),
                                fieldWithPath("email").description("The user's email")
                        )));
    }

    @Test
    void getAllUsers() throws Exception {
        User user2 = new User(2L, "Jane Doe", "jane@example.com");
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser, user2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andDo(document("get-all-users",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[]").description("Array of users"),
                                fieldWithPath("[].id").description("The user's ID"),
                                fieldWithPath("[].name").description("The user's name"),
                                fieldWithPath("[].email").description("The user's email")
                        )));
    }

    @Test
    void updateUser() throws Exception {
        User updatedUser = new User(1L, "John Updated", "john.updated@example.com");
        String updatedUserJson=objectMapper.writeValueAsString(updatedUser);
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson))
                .andExpect(status().isOk())
                .andDo(document("update-user",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("The user's ID")
                        ),
                        requestFields(
                                fieldWithPath("id").description("The user's ID"),
                                fieldWithPath("name").description("The user's updated name"),
                                fieldWithPath("email").description("The user's updated email")
                        ),
                        responseFields(
                                fieldWithPath("id").description("The user's ID"),
                                fieldWithPath("name").description("The user's updated name"),
                                fieldWithPath("email").description("The user's updated email")
                        )));
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isNoContent()) // Adjust status based on API
                .andDo(document("delete-user",
                        preprocessRequest(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("The user's ID to delete")
                        )));
    }
}
