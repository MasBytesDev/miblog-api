package com.masbytes.miblog.exception;

/**
 * Excepción lanzada cuando no se encuentra un post en el sistema.
 * Se usa cuando el post solicitado no existe en la base de datos.
 */
public class PostNotFoundException extends RuntimeException{	
	
	private static final long serialVersionUID = 1L;

	/**
     * Constructor para crear una excepción con un mensaje específico.
     * 
     * @param message El mensaje detallado que describe el motivo de la excepción.
     */
	public PostNotFoundException (String message) {
		super(message);
	}

}
