package com.masbytes.miblog.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Representa una publicación en el blog.
 * Cada post contiene un título, una URL de un archivo PDF o TXT, un resumen, 
 * marcas de tiempo de creación y modificación, visibilidad y etiquetas.
 */
@Data
@Document(collection = "posts")
public class Post {
	
	/**
     * Identificador único del post en la base de datos.
     */
	@Id
	private String id;	
	
	/**
     * Título del post. Este campo es obligatorio y está indexado para búsqueda de texto.
     */
	@NotNull
	@TextIndexed
	private String title;
	
	/**
     * URL del archivo PDF que contiene el contenido del post.
     * Este campo es obligatorio.
     */
	@NotNull
	private String pdfUrl;
	
	/**
     * Resumen del post utilizado para búsquedas textuales.
     * Tiene mayor peso en la indexación para mejorar la relevancia de las búsquedas.
     */
	@NotNull
	@TextIndexed(weight = 2)
	private String summary;	
	
	/**
     * Fecha y hora en que el post fue creado.
     * Se almacena automáticamente y se indexa en orden descendente.
     */
	@CreatedDate
	@Field("created_at")
	@Indexed(direction = IndexDirection.DESCENDING)
	private LocalDateTime createdAt;
	
	/**
     * Fecha y hora en que el post fue modificado por última vez.
     * Se actualiza automáticamente con cada cambio en el post.
     */
	@LastModifiedDate
	@Field("modified_at")
	private LocalDateTime modifiedAt;
	
	/**
     * Indica si el post es visible o no. Por defecto, es true.
     */
	private boolean visible = true;
	
	/**
     * Lista de etiquetas asociadas al post. 
     * Debe contener entre 3 y 20 caracteres y está indexada para optimizar las búsquedas.
     */
	@Size(min = 3, max = 20)
	@Indexed
	private List<String> tags;

}
