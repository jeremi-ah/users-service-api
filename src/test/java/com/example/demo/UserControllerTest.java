package com.example.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
	
	 @Autowired
	    private MockMvc mockMvc;

	    @Autowired
	    private ObjectMapper objectMapper;
	    
	    @MockBean
	    private UserService userService;

	    @Test
	    void createUser_ShouldCreateUser() throws Exception {
	        // Arrange
	        User user = new User(1L, "John Doe", "john@example.com");
	        when(userService.createUser(any(User.class))).thenReturn(user);
	        String userJson = objectMapper.writeValueAsString(user);

	        // Act & Assert
	        mockMvc.perform(post("/api/users")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(userJson))
	                .andExpect(status().isOk())
	                .andExpect(jsonPath("$.name").value(user.getName()))
	                .andExpect(jsonPath("$.email").value(user.getEmail()));
	        verify(userService).createUser(any(User.class));
	    }
	    
	    @Test
	    void getUserById_ShouldReturnUser() throws Exception {
	        // Arrange
	        User user = new User(1L, "John Doe", "john@example.com");
	        when(userService.getUserById(1L)).thenReturn(user);

	        // Act & Assert
	        mockMvc.perform(get("/api/users/1"))
	                .andExpect(status().isOk())
	                .andExpect(jsonPath("$.name").value(user.getName()))
	                .andExpect(jsonPath("$.email").value(user.getEmail()));

	        verify(userService).getUserById(1L);
	    }
	    
	    @Test
	    void getAllUsers_ShouldReturnAllUsers() throws Exception {
	        // Arrange
	        List<User> users = Arrays.asList(
	            new User(1L, "John Doe", "john@example.com"),
	            new User(2L, "Jane Doe", "jane@example.com")
	        );
	        when(userService.getAllUsers()).thenReturn(users);

	        // Act & Assert
	        mockMvc.perform(get("/api/users"))
	                .andExpect(status().isOk())
	                .andExpect(jsonPath("$.length()").value(2))
	                .andExpect(jsonPath("$[0].name").value("John Doe"))
	                .andExpect(jsonPath("$[1].name").value("Jane Doe"));

	        verify(userService).getAllUsers();
	    }
	    
	    @Test
	    void updateUser_ShouldUpdateUser() throws Exception {
	        // Arrange
	        User updatedUser = new User(1L, "John Updated", "john.updated@example.com");
	        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

	        // Act & Assert
	        mockMvc.perform(put("/api/users/1")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(objectMapper.writeValueAsString(updatedUser)))
	                .andExpect(status().isOk())
	                .andExpect(jsonPath("$.name").value(updatedUser.getName()))
	                .andExpect(jsonPath("$.email").value(updatedUser.getEmail()));

	        verify(userService).updateUser(eq(1L), any(User.class));
	    }
	    
	    @Test
	    void deleteUser_ShouldDeleteUser() throws Exception {
	        // Arrange
	        doNothing().when(userService).deleteUser(1L);

	        // Act & Assert
	        mockMvc.perform(delete("/api/users/1"))
	                .andExpect(status().isOk());

	        verify(userService).deleteUser(1L);
	    }

}
