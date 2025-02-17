package com.masbytes.miblog.exception;

/**
 * Excepción lanzada cuando se intenta crear un post que ya existe en el sistema.
 * Esta excepción se usa para indicar que ya hay un post con los mismos atributos únicos (por ejemplo, el título).
 */
public class PostAlreadyExistsException extends RuntimeException{	
	
	private static final long serialVersionUID = 1L;

	/**
     * Constructor para crear una excepción con un mensaje específico.
     * 
     * @param message El mensaje detallado que describe el motivo de la excepción.
     */
	public PostAlreadyExistsException (String message) {
		super(message);
	}

}
