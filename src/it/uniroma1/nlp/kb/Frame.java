package it.uniroma1.nlp.kb;

import java.util.TreeSet;

import it.uniroma1.nlp.verbatlas.VerbAtlas;
/**
 * interfaccia di un frame
 * @author Federica Magliocca
 *
 */

public interface Frame {
	/**
	 *  
	 * @return il nome del frame
	 */
	String getName();
	/**
	 * 
	 * @return i ruoli del frame
	 */
	TreeSet<VerbAtlas.VerbAtlasFrame.Role> getRoles();
	/**
	 *  
	 * @return l'id del frame
	 */
	VerbAtlasFrameID getId();

}
