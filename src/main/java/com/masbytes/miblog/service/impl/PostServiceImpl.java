package com.masbytes.miblog.service.impl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.masbytes.miblog.entity.Post;
import com.masbytes.miblog.exception.PostAlreadyExistsException;
import com.masbytes.miblog.exception.PostInvalidDataException;
import com.masbytes.miblog.exception.PostNotFoundException;
import com.masbytes.miblog.repo.PostRepo;
import com.masbytes.miblog.service.PostService;

/**
 * Implementación del servicio para gestionar los posts.
 * Proporciona la lógica de negocio para la creación, actualización, eliminación y búsqueda de posts.
 */
@Service
public class PostServiceImpl implements PostService {

	private final PostRepo postRepo;

	public PostServiceImpl(PostRepo postRepo) {
		this.postRepo = postRepo;
	}

	/**
     * Crea un nuevo post en el sistema.
     * 
     * @param post El post que se desea crear.
     * @return El post recién creado.
     * @throws PostAlreadyExistsException Si ya existe un post con el mismo título.
     * @throws PostInvalidDataException Si los datos proporcionados no son válidos.
     */
	@Override
	public Post createPost(Post post) throws PostAlreadyExistsException, PostInvalidDataException {

		// Validar que el título no esté repetido
		Optional<Post> existingPost = postRepo.findByTitle(post.getTitle());
		if (existingPost.isPresent()) {
			throw new PostAlreadyExistsException("Ya existe un post con el título: " + post.getTitle());
		}

		// Validar datos obligatorios
		if (post.getTitle() == null || post.getTitle().isBlank() || post.getPdfUrl() == null
				|| post.getPdfUrl().isBlank() || post.getSummary() == null || post.getSummary().isBlank()) {
			throw new PostInvalidDataException("El título, resumen y URL del PDF son obligatorios.");
		}

		// Asignar fechas de creación y modificación
		post.setCreatedAt(LocalDateTime.now());
		post.setModifiedAt(LocalDateTime.now());

		// Guardar en la base de datos
		Post savedPost = postRepo.save(post);

		// Retornar el post creado
		return savedPost;
	}

	/**
     * Obtiene un post por su ID.
     * 
     * @param id El ID del post que se desea obtener.
     * @return El post correspondiente al ID proporcionado.
     * @throws PostNotFoundException Si no se encuentra un post con el ID dado.
     */
	@Override
	public Post getPostById(String id) throws PostNotFoundException {
		// Buscar el post por su ID
		return postRepo.findById(id)
				.orElseThrow(() -> new PostNotFoundException("Post no encontrado con el ID: " + id));
	}

	/**
     * Busca posts que contienen una palabra clave en su título, resumen o etiquetas.
     * 
     * @param keyword La palabra clave que se desea buscar.
     * @return Una lista de posts que contienen la palabra clave.
     */
	@Override
	public List<Post> searchPostByKeyword(String keyword) {
		// Construir expresión regular insensible a mayúsculas/minúsculas
		String regex = ".*" + keyword.toLowerCase() + ".*";

		// Buscar por título, resumen o etiquetas
		return postRepo.findByTitleRegexOrSummaryRegexOrTagsIn(regex, regex, List.of(keyword.toLowerCase()));
	}

	/**
     * Busca posts que contienen al menos una de las etiquetas proporcionadas.
     * 
     * @param tags Una lista de etiquetas para buscar en los posts.
     * @return Una lista de posts que contienen al menos una de las etiquetas.
     */
	@Override
	public List<Post> searchPostsByTags(List<String> tags) {
		if (tags == null || tags.isEmpty()) {
			return Collections.emptyList(); // Retornar lista vacía si las etiquetas están vacías
		}
		
		/*
		// Convertir todas las etiquetas a minúsculas
	    List<String> lowercaseTags = tags.stream()
	                                     .map(String::toLowerCase)
	                                     .toList();
	                                     */
	    
		// Buscar posts que contengan al menos una etiqueta de la lista proporcionada
		return postRepo.findByTagsIn(tags);
	}

	/**
     * Busca posts creados después de una fecha específica.
     * Si no se proporciona una fecha, se buscan los posts de los últimos 30 días.
     * 
     * @param fromDate La fecha a partir de la cual se deben buscar los posts.
     * @return Una lista de posts creados después de la fecha proporcionada.
     */
	@Override
	public List<Post> getRecentPosts(LocalDateTime fromDate) {
	    // Si la fecha es nula, tomamos los últimos 30 días
	    LocalDateTime startOfRange = (fromDate == null)
	        ? LocalDateTime.now().minusDays(30).toLocalDate().atStartOfDay()
	        : fromDate.toLocalDate().atStartOfDay();
	    
	    LocalDateTime endOfRange = (fromDate == null)
	        ? LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX)
	        : fromDate.toLocalDate().atTime(LocalTime.MAX);

	    // Realizamos la búsqueda estrictamente en el rango de fechas
	    return postRepo.findByCreatedAtBetween(startOfRange, endOfRange);
	}

	/**
     * Actualiza un post existente.
     * 
     * @param id El ID del post que se desea actualizar.
     * @param post El post con los nuevos datos para actualizar.
     * @return El post actualizado.
     * @throws PostNotFoundException Si no se encuentra un post con el ID proporcionado.
     * @throws PostInvalidDataException Si los datos proporcionados no son válidos.
     */
	@Override
	public Post updatePost(String id, Post post) throws PostInvalidDataException, PostNotFoundException {
		// Verificar si el post existe
		Post existingPost = postRepo.findById(id).orElseThrow(() -> new PostNotFoundException("Post not found"));

		// Validar los datos del post
		if (post.getTitle() == null || post.getTitle().trim().isEmpty()) {
			throw new PostInvalidDataException("Title is required");
		}
		if (post.getSummary() == null || post.getSummary().trim().isEmpty()) {
			throw new PostInvalidDataException("Summary is required");
		}

		// Actualizar los campos del post
		existingPost.setTitle(post.getTitle());
		existingPost.setSummary(post.getSummary());
		existingPost.setTags(post.getTags());
		existingPost.setPdfUrl(post.getPdfUrl());
		existingPost.setVisible(post.isVisible());

		// Guardar el post actualizado
		return postRepo.save(existingPost);
	}

	/**
     * Actualiza la visibilidad de un post.
     * 
     * @param id El ID del post cuya visibilidad se desea cambiar.
     * @param visible El nuevo estado de visibilidad (true o false).
     * @throws PostNotFoundException Si no se encuentra un post con el ID proporcionado.
     */
	@Override
	public void setPostVisibility(String id, boolean visible) throws PostNotFoundException {
		// Verificar si el post existe
		Post existingPost = postRepo.findById(id).orElseThrow(() -> new PostNotFoundException("Post not found"));

		// Actualizar la visibilidad
		existingPost.setVisible(visible);

		// Guardar el post con la nueva visibilidad
		postRepo.save(existingPost);
	}

}
