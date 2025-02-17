package com.masbytes.miblog.service;

import java.time.LocalDateTime;
import java.util.List;

import com.masbytes.miblog.entity.Post;
import com.masbytes.miblog.exception.PostAlreadyExistsException;
import com.masbytes.miblog.exception.PostInvalidDataException;
import com.masbytes.miblog.exception.PostNotFoundException;

/**
 * Interfaz para el servicio de gestión de posts.
 * Define los métodos para las operaciones CRUD y búsqueda de posts en el sistema.
 */
public interface PostService {
	
	/**
     * Crea un nuevo post en el sistema.
     * 
     * @param post El post que se desea crear.
     * @return El post recién creado.
     * @throws PostAlreadyExistsException Si ya existe un post con los mismos datos.
     * @throws PostInvalidDataException Si los datos del post no son válidos.
     */
	Post createPost (Post post) throws PostAlreadyExistsException, PostInvalidDataException;
	
	/**
     * Obtiene un post por su ID.
     * 
     * @param id El ID del post que se desea obtener.
     * @return El post correspondiente al ID proporcionado.
     * @throws PostNotFoundException Si no se encuentra un post con el ID dado.
     */
	Post getPostById(String id) throws PostNotFoundException;
	
	/**
     * Busca posts que contienen una palabra clave en su título o resumen.
     * 
     * @param keyword La palabra clave para buscar en los posts.
     * @return Una lista de posts que contienen la palabra clave.
     */
	List<Post> searchPostByKeyword(String keyword);
	
	/**
     * Busca posts que contienen al menos una de las etiquetas especificadas.
     * 
     * @param tags Una lista de etiquetas para buscar en los posts.
     * @return Una lista de posts que contienen las etiquetas especificadas.
     */
	List<Post> searchPostsByTags(List<String> tags);
	
	/**
     * Busca posts que fueron creados después de una fecha específica.
     * 
     * @param fromDate La fecha a partir de la cual se deben buscar los posts.
     * @return Una lista de posts creados después de la fecha indicada.
     */
	List<Post> getRecentPosts(LocalDateTime fromDate);
	
	/**
     * Actualiza un post existente.
     * 
     * @param id El ID del post a actualizar.
     * @param post El post con los nuevos datos.
     * @return El post actualizado.
     * @throws PostNotFoundException Si no se encuentra un post con el ID proporcionado.
     * @throws PostInvalidDataException Si los datos proporcionados no son válidos.
     */
	Post updatePost (String id, Post post) throws PostInvalidDataException, PostNotFoundException;
	
	/**
     * Actualiza la visibilidad de un post (eliminación lógica).
     * 
     * @param id El ID del post cuya visibilidad se desea actualizar.
     * @param visible El estado de visibilidad (true o false).
     * @throws PostNotFoundException Si no se encuentra un post con el ID proporcionado.
     */
	void setPostVisibility(String id, boolean visible) throws PostNotFoundException;

}
