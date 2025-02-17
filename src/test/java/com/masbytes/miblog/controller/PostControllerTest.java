package com.masbytes.miblog.controller;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.masbytes.miblog.entity.Post;
import com.masbytes.miblog.exception.PostNotFoundException;
import com.masbytes.miblog.service.PostService;

@WebMvcTest(PostController.class)
public class PostControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PostService postService;

	// Testing: Ok
	@Test
	void testGetPostById_Success() throws Exception {
		// Simulamos un post de prueba
		Post post = new Post();
		post.setId("123");
		post.setTitle("Ejemplo Post");

		when(postService.getPostById("123")).thenReturn(post);

		mockMvc.perform(get("/api/posts/123")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value("123"))
				.andExpect(jsonPath("$.title").value("Ejemplo Post"));
	}

	//	Testing: Ok
	@Test
	void testGetPostById_NotFound() throws Exception {
		when(postService.getPostById("999")).thenThrow(new PostNotFoundException("Post no encontrado"));

		mockMvc.perform(get("/api/posts/999")).andExpect(status().isNotFound());
	}
	
	//	Testing: Ok
	@Test
    void searchPosts_KeywordFound() throws Exception {
        Post post = new Post();
        post.setTitle("Teoría de la Relatividad");
        post.setSummary("Einstein propuso la teoría...");
        post.setTags(List.of("ciencia", "física"));
        post.setPdfUrl("https://misitio.com/relatividad.pdf");

        when(postService.searchPostByKeyword("relatividad")).thenReturn(List.of(post));

        mockMvc.perform(get("/api/posts/search")
                .param("keyword", "relatividad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Teoría de la Relatividad"));
    }
	
	//	Testing: Ok
	@Test
    void searchPosts_NoResults() throws Exception {
        when(postService.searchPostByKeyword("noexiste")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/posts/search")
                .param("keyword", "noexiste"))
                .andExpect(status().isNoContent());
    }
	
	//	Testing: Ok
	@Test
    void searchPostsByTags_AtLeastOneTagMatch() throws Exception {
        Post post = new Post();
        post.setTitle("Teoría de la Relatividad");
        post.setSummary("Einstein propuso la teoría...");
        post.setTags(List.of("ciencia", "física"));
        post.setPdfUrl("https://misitio.com/relatividad.pdf");

        when(postService.searchPostsByTags(List.of("ciencia"))).thenReturn(List.of(post));

        mockMvc.perform(get("/api/posts/tags")
                .param("tags", "ciencia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Teoría de la Relatividad"));
    }
	
	//	Testing: Ok
	@Test
    void searchPostsByTags_NoResults() throws Exception {
        when(postService.searchPostsByTags(List.of("astronomía"))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/posts/tags")
                .param("tags", "astronomía"))
                .andExpect(status().isNoContent());
    }
	
	//	Testing: Ok
	@Test
    void searchPostsByTags_EmptyTagsList() throws Exception {
        mockMvc.perform(get("/api/posts/tags")
                .param("tags", ""))
                .andExpect(status().isNoContent());

        verify(postService, times(0)).searchPostsByTags(anyList());
    }

}
