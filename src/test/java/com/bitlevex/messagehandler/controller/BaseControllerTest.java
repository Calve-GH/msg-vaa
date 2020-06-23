package com.bitlevex.messagehandler.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAddingItemsSuccess() throws Exception {
        this.mockMvc
                .perform(post(BaseController.REST_URL + "plus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstElement\":2, \"secondElement\":1}"))
                .andExpect(content().string("{\"result\":3}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddIncorrectFirstItem() throws Exception {
        this.mockMvc
                .perform(post(BaseController.REST_URL + "plus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstElement\":\"a\", \"secondElement\":1}"))
                .andExpect(content().string("{\"error\":\"Invalid value of firstElement\"}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testAddIncorrectBothItem() throws Exception {
        this.mockMvc
                .perform(post(BaseController.REST_URL + "plus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstElement\":\"a\", \"secondElement\":\"b\"}"))
                .andExpect(content().string("{\"error\":\"Invalid value of firstElement, Invalid value of secondElement\"}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testAddIllegalArgument() throws Exception {
        this.mockMvc
                .perform(post(BaseController.REST_URL + "plus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstElement\":1.0.0, \"secondElement\":1}"))
                .andExpect(content().string("{\"error\":\"illegal arguments\"}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testDifferenceItemsSuccess() throws Exception {
        this.mockMvc
                .perform(post(BaseController.REST_URL + "minus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstElement\":2, \"secondElement\":1}"))
                .andExpect(content().string("{\"result\":1}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testMultiplyingItemsSuccess() throws Exception {
        this.mockMvc
                .perform(post(BaseController.REST_URL + "multiply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstElement\":2, \"secondElement\":1}"))
                .andExpect(content().string("{\"result\":2}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDividingItemsSuccess() throws Exception {
        this.mockMvc
                .perform(post(BaseController.REST_URL + "divide")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstElement\":2, \"secondElement\":1}"))
                .andExpect(content().string("{\"result\":2}"))
                .andExpect(status().isOk());
    }

}