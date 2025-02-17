package com.masbytes.miblog.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.masbytes.miblog.entity.Post;
import com.masbytes.miblog.exception.PostAlreadyExistsException;
import com.masbytes.miblog.exception.PostInvalidDataException;
import com.masbytes.miblog.exception.PostNotFoundException;
import com.masbytes.miblog.repo.PostRepo;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

	@Mock
	private PostRepo postRepo;

	@InjectMocks
	private PostServiceImpl postService;

	// Testing: Ok
	@Test
	void createPost_Successful() {
		// Datos de prueba
		Post post = new Post();
		post.setTitle("Teoria de la Relatividad");
		post.setPdfUrl("https://misitio.com/relatividad.pdf");
		post.setSummary("Un resumen sobre la teoria de Einstein.");
		post.setTags(List.of("Ciencia", "Fisica"));

		// Simulacion del repositorio
		when(postRepo.findByTitle(post.getTitle())).thenReturn(Optional.empty());
		when(postRepo.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Ejecutar el metodo
		Post savedPost = postService.createPost(post);

		// Verificaciones
		assertNotNull(savedPost);
		assertEquals("Teoria de la Relatividad", savedPost.getTitle());
		assertNotNull(savedPost.getCreatedAt());
		verify(postRepo, times(1)).save(any(Post.class));
	}

	// Testing: Ok
	@Test
	void createPost_InvalidData() {
		// Datos de prueba con un título nulo y resumen vacío
		Post post = new Post();
		post.setTitle(null); // Título nulo
		post.setPdfUrl("https://misitio.com/relatividad.pdf");
		post.setSummary(""); // Resumen vacío
		post.setTags(List.of("ciencia", "física"));

		// Simulación del repositorio
		when(postRepo.findByTitle(post.getTitle())).thenReturn(Optional.empty());

		// Ejecutar el método y verificar que lanza la excepción
		assertThrows(PostInvalidDataException.class, () -> postService.createPost(post));

		// Verificaciones
		verify(postRepo, never()).save(any(Post.class)); // No debe guardar en la base de datos
	}

	// Testing: Ok
	@Test
	void createPost_TitleAlreadyExists() {
		// Datos de prueba para un post con un título que ya existe
		Post post = new Post();
		post.setTitle("Teoría de la Relatividad");
		post.setPdfUrl("https://misitio.com/relatividad2.pdf");
		post.setSummary("Un nuevo resumen sobre la teoría de Einstein.");
		post.setTags(List.of("ciencia", "física"));

		// Simulación del repositorio: ya existe un post con el mismo título
		Post existingPost = new Post();
		existingPost.setTitle("Teoría de la Relatividad");
		when(postRepo.findByTitle(post.getTitle())).thenReturn(Optional.of(existingPost));

		// Ejecutar el método y verificar que lanza la excepción
		assertThrows(PostAlreadyExistsException.class, () -> postService.createPost(post));

		// Verificaciones
		verify(postRepo, never()).save(any(Post.class)); // No debe guardar en la base de datos
	}

	// Testing: Ok
	@Test
	void getPostById_Successful() throws PostNotFoundException {
		// Datos de prueba
		Post post = new Post();
		post.setId("123");
		post.setTitle("Teoría de la Relatividad");
		post.setPdfUrl("https://misitio.com/relatividad.pdf");
		post.setSummary("Einstein propuso la teoría de la relatividad...");
		post.setTags(List.of("ciencia", "física"));

		// Simulación del repositorio: el post existe
		when(postRepo.findById(post.getId())).thenReturn(Optional.of(post));

		// Ejecutar el método
		Post result = postService.getPostById(post.getId());

		// Verificación de los resultados
		assertNotNull(result);
		assertEquals(post.getId(), result.getId());
		assertEquals(post.getTitle(), result.getTitle());
		verify(postRepo, times(1)).findById(post.getId()); // Verificar que findById fue llamado una vez
	}

	// Testing: Ok
	@Test
	void getPostById_NotFound() {
		// Simulación del repositorio: el post no existe
		String invalidId = "999";
		when(postRepo.findById(invalidId)).thenReturn(Optional.empty());

		// Ejecutar el método y verificar que se lanza la excepción
		assertThrows(PostNotFoundException.class, () -> postService.getPostById(invalidId));

		// Verificación de que el repositorio fue consultado
		verify(postRepo, times(1)).findById(invalidId);
	}

	// Testing: Ok
	@Test
	void searchPostByKeyword_TitleMatch() {
		// Datos de prueba
		Post post = new Post();
		post.setTitle("Teoría de la Relatividad");
		post.setSummary("Einstein propuso la teoría...");
		post.setTags(List.of("ciencia", "física"));
		post.setPdfUrl("https://misitio.com/relatividad.pdf");

		// Simulación del repositorio: el post existe
		when(postRepo.findByTitleRegexOrSummaryRegexOrTagsIn(anyString(), anyString(), anyList()))
				.thenReturn(List.of(post));

		// Ejecutar el método
		List<Post> result = postService.searchPostByKeyword("relatividad");

		// Verificación de los resultados
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(post.getTitle(), result.get(0).getTitle());
		verify(postRepo, times(1)).findByTitleRegexOrSummaryRegexOrTagsIn(anyString(), anyString(), anyList());
	}

	// Testing: Ok
	@Test
	void searchPostByKeyword_SummaryMatch() {
		// Datos de prueba
		Post post = new Post();
		post.setTitle("Teoría de la Relatividad");
		post.setSummary("La teoría de la relatividad de Einstein...");
		post.setTags(List.of("ciencia", "física"));
		post.setPdfUrl("https://misitio.com/relatividad.pdf");

		// Simulación del repositorio: el post existe
		when(postRepo.findByTitleRegexOrSummaryRegexOrTagsIn(anyString(), anyString(), anyList()))
				.thenReturn(List.of(post));

		// Ejecutar el método
		List<Post> result = postService.searchPostByKeyword("relatividad");

		// Verificación de los resultados
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(post.getSummary(), result.get(0).getSummary());
		verify(postRepo, times(1)).findByTitleRegexOrSummaryRegexOrTagsIn(anyString(), anyString(), anyList());
	}

	// Testing: Ok
	@Test
	void searchPostByKeyword_TagsMatch() {
		// Datos de prueba
		Post post = new Post();
		post.setTitle("Teoría de la Relatividad");
		post.setSummary("Einstein propuso la teoría...");
		post.setTags(List.of("ciencia", "relatividad"));
		post.setPdfUrl("https://misitio.com/relatividad.pdf");

		// Simulación del repositorio: el post existe
		when(postRepo.findByTitleRegexOrSummaryRegexOrTagsIn(anyString(), anyString(), anyList()))
				.thenReturn(List.of(post));

		// Ejecutar el método
		List<Post> result = postService.searchPostByKeyword("relatividad");

		// Verificación de los resultados
		assertNotNull(result);
		assertEquals(1, result.size());
		assertTrue(result.get(0).getTags().contains("relatividad"));
		verify(postRepo, times(1)).findByTitleRegexOrSummaryRegexOrTagsIn(anyString(), anyString(), anyList());
	}

	// Testing: Ok
	@Test
	void searchPostByKeyword_NoMatch() {
		// Simulación del repositorio: no se encuentra ningún post
		when(postRepo.findByTitleRegexOrSummaryRegexOrTagsIn(anyString(), anyString(), anyList()))
				.thenReturn(Collections.emptyList());

		// Ejecutar el método
		List<Post> result = postService.searchPostByKeyword("noencontrado");

		// Verificación de los resultados
		assertNotNull(result);
		assertTrue(result.isEmpty());
		verify(postRepo, times(1)).findByTitleRegexOrSummaryRegexOrTagsIn(anyString(), anyString(), anyList());
	}

	// Testing: Ok
	@Test
	void searchPostsByTags_AtLeastOneTagMatch() {
		// Datos de prueba
		Post post = new Post();
		post.setTitle("Teoría de la Relatividad");
		post.setSummary("Einstein propuso la teoría...");
		post.setTags(List.of("ciencia", "física"));
		post.setPdfUrl("https://misitio.com/relatividad.pdf");

		// Simulación del repositorio: el post existe
		when(postRepo.findByTagsIn(anyList())).thenReturn(List.of(post));

		// Ejecutar el método
		List<Post> result = postService.searchPostsByTags(List.of("ciencia"));

		// Verificación de los resultados
		assertNotNull(result);
		assertEquals(1, result.size());
		assertTrue(result.get(0).getTags().contains("ciencia"));
		verify(postRepo, times(1)).findByTagsIn(anyList());
	}

	// Testing: Ok
	@Test
	void searchPostsByTags_NoMatch() {
		// Datos de prueba
		Post post = new Post();
		post.setTitle("Teoría de la Relatividad");
		post.setSummary("Einstein propuso la teoría...");
		post.setTags(List.of("ciencia", "física"));
		post.setPdfUrl("https://misitio.com/relatividad.pdf");

		// Simulación del repositorio: no se encuentra ningún post que coincida
		when(postRepo.findByTagsIn(anyList())).thenReturn(Collections.emptyList());

		// Ejecutar el método
		List<Post> result = postService.searchPostsByTags(List.of("astronomía"));

		// Verificación de los resultados
		assertNotNull(result);
		assertTrue(result.isEmpty());
		verify(postRepo, times(1)).findByTagsIn(anyList());
	}

	//	Testing: Ok
	@Test
	void searchPostsByTags_EmptyTagsList() {
	    // Ejecutar el método con una lista vacía
	    List<Post> result = postService.searchPostsByTags(Collections.emptyList());

	    // Verificación de los resultados
	    assertNotNull(result);
	    assertTrue(result.isEmpty());

	    // Verificamos que no se haya invocado el repositorio en caso de lista vacía
	    verify(postRepo, times(0)).findByTagsIn(anyList());
	}
	
	//	Testing: Ok
	@Test
	void getRecentPosts_ValidDate() {
	    // Datos de prueba
	    LocalDateTime postDate = LocalDateTime.of(2025, 2, 15, 10, 0, 0, 0);
	    Post post = new Post();
	    post.setTitle("Teoría de la Relatividad");
	    post.setSummary("Einstein propuso la teoría...");
	    post.setCreatedAt(postDate);
	    post.setTags(List.of("ciencia", "física"));
	    post.setPdfUrl("https://misitio.com/relatividad.pdf");

	    // Simulación del repositorio: el post está dentro del rango de fecha
	    when(postRepo.findByCreatedAtGreaterThanEqual(any())).thenReturn(List.of(post));

	    // Ejecutar el método
	    List<Post> result = postService.getRecentPosts(LocalDateTime.of(2025, 2, 14, 0, 0, 0, 0));

	    // Verificación de los resultados
	    assertNotNull(result);
	    assertEquals(1, result.size());
	    assertEquals(post.getTitle(), result.get(0).getTitle());
	    verify(postRepo, times(1)).findByCreatedAtGreaterThanEqual(any());
	}
	
	//	Testing: Ok
	@Test
	void getRecentPosts_NoPostsAfterDate() {
	    // Datos de prueba
	    LocalDateTime fromDate = LocalDateTime.of(2025, 2, 15, 10, 0, 0, 0);

	    // Simulación del repositorio: no se encuentran posts después de la fecha
	    when(postRepo.findByCreatedAtGreaterThanEqual(any())).thenReturn(Collections.emptyList());

	    // Ejecutar el método
	    List<Post> result = postService.getRecentPosts(fromDate);

	    // Verificación de los resultados
	    assertNotNull(result);
	    assertTrue(result.isEmpty());
	    verify(postRepo, times(1)).findByCreatedAtGreaterThanEqual(any());
	}
	
	//	Testing: Ok
	@Test
	void getRecentPosts_NullDate() {
	    // Ejecutar el método con fecha nula
	    List<Post> result = postService.getRecentPosts(null);

	    // Verificación de los resultados
	    assertNotNull(result);
	    assertTrue(result.isEmpty());
	    verify(postRepo, times(0)).findByCreatedAtGreaterThanEqual(any());
	}
	
	//	Testing: Ok
	@Test
	void getRecentPosts_FutureDate() {
	    // Datos de prueba
	    LocalDateTime fromDate = LocalDateTime.of(2050, 1, 1, 0, 0, 0, 0);

	    // Simulación del repositorio: no se encuentran posts después de la fecha futura
	    when(postRepo.findByCreatedAtGreaterThanEqual(any())).thenReturn(Collections.emptyList());

	    // Ejecutar el método
	    List<Post> result = postService.getRecentPosts(fromDate);

	    // Verificación de los resultados
	    assertNotNull(result);
	    assertTrue(result.isEmpty());
	    verify(postRepo, times(1)).findByCreatedAtGreaterThanEqual(any());
	}
	
	//	Testing: Ok
	@Test
	void updatePost_Successful() throws PostNotFoundException, PostInvalidDataException {
	    // Datos de prueba
	    Post existingPost = new Post();
	    existingPost.setId("1");
	    existingPost.setTitle("Teoría de la Relatividad");
	    existingPost.setSummary("Einstein propuso la teoría...");
	    existingPost.setTags(List.of("ciencia", "física"));
	    existingPost.setPdfUrl("https://misitio.com/relatividad.pdf");

	    // Simulación del repositorio: se encuentra el post
	    when(postRepo.findById("1")).thenReturn(Optional.of(existingPost));

	    // Datos del post a actualizar
	    Post updatedPost = new Post();
	    updatedPost.setTitle("Relatividad Especial");
	    updatedPost.setSummary("La relatividad especial es una teoría de Einstein...");
	    updatedPost.setTags(List.of("ciencia", "física", "teoría"));
	    updatedPost.setPdfUrl("https://misitio.com/relatividad-especial.pdf");

	    // Simulación del repositorio: el post actualizado se guarda correctamente
	    when(postRepo.save(any(Post.class))).thenReturn(updatedPost);

	    // Ejecutar el método
	    Post result = postService.updatePost("1", updatedPost);

	    // Verificación de los resultados
	    assertNotNull(result);
	    assertEquals("Relatividad Especial", result.getTitle());
	    assertEquals("La relatividad especial es una teoría de Einstein...", result.getSummary());
	    assertEquals(3, result.getTags().size());
	    assertEquals("https://misitio.com/relatividad-especial.pdf", result.getPdfUrl());
	    verify(postRepo, times(1)).findById("1");
	    verify(postRepo, times(1)).save(any(Post.class));
	}
	
	//	Testing: Ok
	@Test
	void updatePost_PostNotFound() {
	    // Datos de prueba
	    Post updatedPost = new Post();
	    updatedPost.setTitle("Relatividad Especial");
	    updatedPost.setSummary("La relatividad especial es una teoría de Einstein...");

	    // Simulación del repositorio: el post no se encuentra
	    when(postRepo.findById("2")).thenReturn(Optional.empty());

	    // Ejecutar el método y verificar la excepción
	    assertThrows(PostNotFoundException.class, () -> postService.updatePost("2", updatedPost));
	    verify(postRepo, times(1)).findById("2");
	}
	
	//	Testing: Ok
	@Test
	void updatePost_InvalidData() {
	    // Datos de prueba
	    Post existingPost = new Post();
	    existingPost.setId("1");
	    existingPost.setTitle("Teoría de la Relatividad");
	    existingPost.setSummary("Einstein propuso la teoría...");

	    // Simulación del repositorio: se encuentra el post
	    when(postRepo.findById("1")).thenReturn(Optional.of(existingPost));

	    // Datos del post a actualizar con datos inválidos (sin título)
	    Post updatedPost = new Post();
	    updatedPost.setTitle("");  // Título vacío
	    updatedPost.setSummary("La relatividad especial es una teoría de Einstein...");

	    // Ejecutar el método y verificar la excepción
	    assertThrows(PostInvalidDataException.class, () -> postService.updatePost("1", updatedPost));
	    verify(postRepo, times(1)).findById("1");
	}
	
	//	Testing: Ok
	@Test
	void setPostVisibility_Successful() throws PostNotFoundException {
	    // Datos de prueba
	    Post existingPost = new Post();
	    existingPost.setId("1");
	    existingPost.setTitle("Teoría de la Relatividad");
	    existingPost.setVisible(true);

	    // Simulación del repositorio: se encuentra el post
	    when(postRepo.findById("1")).thenReturn(Optional.of(existingPost));

	    // Ejecutar el método para cambiar la visibilidad
	    postService.setPostVisibility("1", false);

	    // Verificación de los resultados
	    assertFalse(existingPost.isVisible());
	    verify(postRepo, times(1)).findById("1");
	    verify(postRepo, times(1)).save(existingPost);
	}
	
	//	Testing: Ok
	@Test
	void setPostVisibility_PostNotFound() {
	    // Simulación del repositorio: el post no se encuentra
	    when(postRepo.findById("2")).thenReturn(Optional.empty());

	    // Ejecutar el método y verificar la excepción
	    assertThrows(PostNotFoundException.class, () -> postService.setPostVisibility("2", true));
	    verify(postRepo, times(1)).findById("2");
	}
	
}
