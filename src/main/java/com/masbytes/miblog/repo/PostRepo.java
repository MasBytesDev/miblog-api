package com.masbytes.miblog.repo;

// import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.masbytes.miblog.entity.Post;

/**
 * Repositorio para interactuar con la colección de posts en la base de datos MongoDB.
 * Proporciona métodos para realizar operaciones de búsqueda personalizadas en los posts.
 */
@Repository
public interface PostRepo extends MongoRepository<Post, String>{

	/**
     * Busca un post por su título.
     * 
     * @param title El título del post que se desea buscar.
     * @return Un Optional que contiene el post si se encuentra, de lo contrario está vacío.
     */
	Optional<Post> findByTitle(String title);

	/**
     * Busca posts que coincidan con el título, resumen o etiquetas utilizando expresiones regulares.
     * 
     * @param regex Expresión regular para buscar en el título de los posts.
     * @param regex2 Expresión regular para buscar en el resumen de los posts.
     * @param of Lista de etiquetas para buscar en los posts.
     * @return Una lista de posts que coinciden con los criterios proporcionados.
     */
	List<Post> findByTitleRegexOrSummaryRegexOrTagsIn(String regex, String regex2, List<String> of);

	/**
     * Busca posts que contengan alguna de las etiquetas especificadas.
     * 
     * @param tags Lista de etiquetas para buscar en los posts.
     * @return Una lista de posts que contienen al menos una de las etiquetas especificadas.
     */
	List<Post> findByTagsIn(List<String> tags);

	//	List<Post> findByCreatedAtGreaterThanEqual(Instant instant);

	/**
     * Busca posts que fueron creados en un rango de fechas.
     * 
     * @param startOfRange La fecha y hora de inicio del rango.
     * @param endOfRange La fecha y hora de fin del rango.
     * @return Una lista de posts que fueron creados dentro del rango de fechas especificado.
     */
	List<Post> findByCreatedAtBetween(LocalDateTime startOfRange, LocalDateTime endOfRange);
			
}
