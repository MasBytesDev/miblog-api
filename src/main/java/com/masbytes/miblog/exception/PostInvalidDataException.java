package com.masbytes.miblog.exception;

/**
 * Excepción lanzada cuando los datos proporcionados para un post no son válidos.
 * Puede ser utilizada para validar la estructura o contenido del post antes de la creación o actualización.
 */
public class PostInvalidDataException extends RuntimeException{	
	
	private static final long serialVersionUID = 1L;

	/**
     * Constructor para crear una excepción con un mensaje específico.
     * 
     * @param message El mensaje detallado que describe el motivo de la excepción.
     */
	public PostInvalidDataException (String message) {
		super(message);
	}

}
