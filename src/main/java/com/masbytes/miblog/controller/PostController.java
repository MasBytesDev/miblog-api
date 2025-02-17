package com.masbytes.miblog.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.masbytes.miblog.entity.Post;
import com.masbytes.miblog.exception.PostAlreadyExistsException;
import com.masbytes.miblog.exception.PostInvalidDataException;
import com.masbytes.miblog.exception.PostNotFoundException;
import com.masbytes.miblog.service.PostService;

/**
 * Controlador REST para la gestión de publicaciones en el blog.
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

	private final PostService postService;

	/**
	 * Constructor para inyectar el servicio de posts.
	 * 
	 * @param postService Servicio que gestiona la lógica de negocio de los posts.
	 */
	public PostController(PostService postService) {
		this.postService = postService;
	}

	/**
	 * Crea un nuevo post en el blog.
	 * 
	 * @param post Datos del post a crear.
	 * @return El post creado con estado 201 (CREATED) si es exitoso, 400 (BAD
	 *         REQUEST) si los datos son inválidos, 409 (CONFLICT) si el post ya
	 *         existe.
	 */
	@PostMapping
	public ResponseEntity<Post> createPost(@RequestBody Post post) {
		try {
			// Llamar al servicio para crear el post
			Post createdPost = postService.createPost(post);

			// Retornar respuesta con el post creado (201)
			return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
		} catch (PostInvalidDataException e) {
			// Si los datos no son válidos (400)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		} catch (PostAlreadyExistsException e) {
			// Si el post ya existe (409)
			return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
		}
	}

	/**
	 * Obtiene un post por su identificador único.
	 * 
	 * @param id Identificador del post.
	 * @return El post encontrado con estado 200 (OK), 404 (NOT FOUND) si no se
	 *         encuentra.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Post> getPostById(@PathVariable String id) {
		try {
			Post post = postService.getPostById(id);
			return ResponseEntity.ok(post);
		} catch (PostNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	/**
	 * Busca posts por una palabra clave en el contenido o título.
	 * 
	 * @param keyword Palabra clave para la búsqueda.
	 * @return Lista de posts coincidentes con estado 200 (OK), 204 (NO CONTENT) si
	 *         no hay coincidencias.
	 */
	@GetMapping("/search")
	public ResponseEntity<List<Post>> searchPosts(@RequestParam String keyword) {
		List<Post> posts = postService.searchPostByKeyword(keyword);
		return posts.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(posts);
	}

	/**
	 * Busca posts por etiquetas específicas.
	 * 
	 * @param tags Lista de etiquetas para filtrar los posts.
	 * @return Lista de posts coincidentes con estado 200 (OK), 204 (NO CONTENT) si
	 *         no hay coincidencias.
	 */
	@GetMapping("/tags")
	public ResponseEntity<List<Post>> searchPostsByTags(@RequestParam List<String> tags) {
		if (tags == null || tags.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		List<Post> posts = postService.searchPostsByTags(tags);
		return posts.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(posts);
	}

	/**
	 * Obtiene los posts recientes desde una fecha específica.
	 * 
	 * @param fromDate (Opcional) Fecha desde la cual buscar los posts.
	 * @return Lista de posts recientes con estado 200 (OK), 204 (NO CONTENT) si no
	 *         hay posts recientes.
	 */
	@GetMapping("/recent")
	public ResponseEntity<List<Post>> getRecentPosts(@RequestParam(required = false) LocalDate fromDate) {
		List<Post> posts = postService.getRecentPosts(fromDate != null ? fromDate.atStartOfDay() : null);

		return posts.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(posts);
	}

	/**
	 * Actualiza un post existente.
	 * 
	 * @param id   Identificador del post a actualizar.
	 * @param post Datos actualizados del post.
	 * @return El post actualizado con estado 200 (OK), 404 (NOT FOUND) si el post
	 *         no existe, 400 (BAD REQUEST) si los datos son inválidos.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Post> updatePost(@PathVariable String id, @RequestBody Post post) {
		try {
			Post updatedPost = postService.updatePost(id, post);
			return ResponseEntity.ok(updatedPost);
		} catch (PostNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (PostInvalidDataException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	/**
	 * Actualiza la visibilidad de un post.
	 * 
	 * @param id          Identificador del post.
	 * @param requestBody Cuerpo de la solicitud con el campo "visible".
	 * @return Estado 200 (OK) si la visibilidad fue actualizada correctamente.
	 */
	@PatchMapping("/{id}/visibility")
	public ResponseEntity<Post> updatePostVisibility(@PathVariable String id,
			@RequestBody Map<String, Boolean> requestBody) {

		boolean visible = requestBody.get("visible");
		postService.setPostVisibility(id, visible);
		return ResponseEntity.ok().build();
	}

}
