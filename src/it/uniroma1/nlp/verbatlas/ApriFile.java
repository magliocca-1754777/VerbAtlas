package it.uniroma1.nlp.verbatlas;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * classe di supporto per l'apertura dei file
 * @author Federica Magliocca
 *
 */

public class ApriFile {
	/**
	 * mappe di supporto per memorizzare i file letti
	 */
	private static HashMap<String,HashMap<String,String>> map=new HashMap<>();
	private static HashMap<String,HashMap<String,String>> mapReverse=new HashMap<>();
	private static HashMap<String,List<String>> map2List=new HashMap<>();
	/**
	 * legge un file 
	 * @param file
	 * @return la mappa che ha come chiave il primo
	 * elemento delle righe del file e come valore il secondo elemento
	 * separati per \t+
	 */
	public static HashMap<String,String> file2Map(String file){
		Set<String> chiavi=map.keySet();
		List<String> list=new ArrayList<>();
		HashMap<String,String> mappa=new HashMap<>();
		if (!chiavi.contains(file)) {
			URI URI;
			try {
				URI = ClassLoader.getSystemResource(file).toURI();
				try(BufferedReader br=Files.newBufferedReader(Paths.get(URI))){
			    	while(br.ready()) {
			    		String line=br.readLine();
			    		if (!line.startsWith("#")) list.add(line);
			    	}
			    	mappa=list.stream()
			    			.collect(Collectors.toMap(x->x.split("\t+")[0].trim(),x->{
			    														if (x.split("\t+| ",2).length==1)return "";//fatto perche c'e un errore nel file, un elemento non ha ruolo
			    														return x.split("\t+| ",2)[1].trim();
			    																	},(x,y)->x+" "+y,HashMap::new));
			    	map.put(file,mappa);
			    }
			    catch(IOException e) {
			    	System.out.print("File inesistente");
			    }
			}
		    catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
		}
		else mappa=map.get(file);
		return mappa;
		
	}
	/**
	 * legge un file
	 * @param file
	 * @return  la mappa che ha come chiave il secondo
	 * elemento delle righe del file e come valore il primo elemento
	 * separati per \t+
	 */
	public static HashMap<String,String> file2MapReverse(String file){
		Set<String> chiavi=mapReverse.keySet();
		List<String> list=new ArrayList<>();
		HashMap<String,String> mappa=new HashMap<>();
		if (!chiavi.contains(file)) {
			URI URI;
			try {
				URI = ClassLoader.getSystemResource(file).toURI();
				try(BufferedReader br=Files.newBufferedReader(Paths.get(URI))){
			    	while(br.ready()) {
			    		String line=br.readLine();
			    		if (!line.startsWith("#")) list.add(line);
			    	}
			    	mappa=list.stream()
			    			.collect(Collectors.toMap(x->x.split("\t+")[1].trim(),x->x.split("\t+",2)[0].trim(),(x,y)->x+" "+y,HashMap::new));
			    	mapReverse.put(file,mappa);
			    }
			    catch(IOException e) {
			    	System.out.print("File inesistente");
			    }
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
		}
		else mappa=mapReverse.get(file);
		return mappa;
		
	}
	/**
	 * legge un file
	 * @param file
	 * @return la lista delle righe del file
	 */
	public static List<String> file2List(String file){
		Set<String> chiavi=map2List.keySet();
		List<String> list=new ArrayList<>();
		if (!chiavi.contains(file)) {
			URI URI;
			try {
				URI = ClassLoader.getSystemResource(file).toURI();
				 try(BufferedReader br=Files.newBufferedReader(Paths.get(URI))){
			        	while(br.ready()) {
			        		String line=br.readLine();
			    		    if (!line.startsWith("#")) list.add(line);
			    	    }
			    	    map2List.put(file,list);
			        }
			        catch(IOException e) {
			        	System.out.print("File inesistente");
			        }
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
	    }
		else list=map2List.get(file);
	    return list;
	}
	
}
