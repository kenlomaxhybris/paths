package com.hybris.paths;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MaterialUtil {

	@Value("${API_KEY}")
	String API_KEY;
	
	public String title = null;
	public String thumb = null;
	public String description = null;		
	public String duration = null;		
	public String publishedAt = null;
	public String url = null;
	public String type ="";
	public int id = -1;
	
	public String get(final String sourceUrl, int id) throws Exception {
		title = null;
		thumb = null;
		description = null;		
		publishedAt = null;
		url = null;
		this.id=id;
		
		if (sourceUrl==null | sourceUrl.isEmpty() )
			throw new Exception("Not an expected URL");
		if (sourceUrl.startsWith("https://www.youtube.com/") )
			return getYouTubeInfo(sourceUrl);
		return getSiteInfo(sourceUrl);
	}
	
	private String getSiteInfo( final String sourceUrl ) throws Exception {			
		// E.g. https://spring.io/guides
		if (sourceUrl==null | sourceUrl.isEmpty()  )
			throw new Exception("Not an expected URL");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(sourceUrl).openStream(), "UTF-8"));
	    for (String line; (line = reader.readLine()) != null;) {
	    		if (line.contains("<title>") && title==null) {
	    			if (line.contains("</title>"))
	    				title =  line.substring(  line.indexOf("<title>")+7, line.indexOf("</title>"));
	    			else {
	    				line = reader.readLine();
	    				if (line.contains("</title>"))
	    					line.substring(  0, line.indexOf("</title>")).trim();
	    				else
	    					title = line.trim();
	    			}
	    		}
	    		
	    		
	    		if (line.contains("favicon.")) {
	    			
	    			int n = line.indexOf("favicon.");
	    			int l=n, r=n;
	    			while ( line.charAt(l)!='"' && line.charAt(l)!='\'')
	    				l--;
	    			while ( line.charAt(r)!='"' && line.charAt(r)!='\'')
	    				r++;
	    			URL urlAbsolute = new URL( new URL(sourceUrl),  line.substring(l+1,r));
	    			
		    		thumb = urlAbsolute.toString();
	    		}
	    		url=sourceUrl;
	    		System.out.println(line);  		    		    	
		}
	    String json = toJson();
	    System.out.println(json);
	    return json;
	}
	
	
	private String getYouTubeInfo( final String youTubeURL ) throws Exception {			
		if (youTubeURL==null | youTubeURL.isEmpty() || !youTubeURL.startsWith("https://www.youtube.com/") || 
			!youTubeURL.contains("v=") &&  !youTubeURL.contains("/channel") &&  !youTubeURL.contains("/user") )
			throw new Exception("Not an expected YoutUbe URL");
		String json ;
		String id;
		URL youTubeEndpoint =null; 
		if (youTubeURL.contains("v=")) { // E.g. https://www.youtube.com/watch?v=1xo-0gCVhTU&t=1191s
			id = youTubeURL.substring( youTubeURL.indexOf("v=")+2);
			if (id.contains("&"))
				id=id.substring(0, id.indexOf("&"));
			youTubeEndpoint = new URL("https://www.googleapis.com/youtube/v3/videos?part=snippet&id="+id+"&key="+API_KEY );
			type = "Video";
			extractDetails(youTubeURL, youTubeEndpoint);
			youTubeEndpoint = new URL("https://www.googleapis.com/youtube/v3/videos?part=contentDetails&id="+id+"&key="+API_KEY );
		}
	
		if (youTubeURL.contains("channel/")) { // Eg https://www.youtube.com/channel/UCwqC1-y3uk5Zfg6F5S_PcVw			
			id=youTubeURL.substring(youTubeURL.indexOf("channel/")+8);
			if (id.contains("/"))
				id=id.substring(0, id.indexOf("/"));
			youTubeEndpoint = new URL("https://www.googleapis.com/youtube/v3/channels?part=snippet&id="+id+"&key="+API_KEY );
			type = "YouTubeChannel";
		}
		if (youTubeURL.contains("user/")) { // Eg https://www.youtube.com/user/derekbanas/playlists
			id=youTubeURL.substring(youTubeURL.indexOf("user/")+5);
			if (id.contains("/"))
				id=id.substring(0, id.indexOf("/"));
			youTubeEndpoint = new URL("https://www.googleapis.com/youtube/v3/channels?part=snippet&forUsername="+id+"&key="+API_KEY );
			type = "YouTubeChannel";
		}
	
		
		json = extractDetails(youTubeURL, youTubeEndpoint);
			
	    return json;
	}

	private String extractDetails(final String youTubeURL, URL youTubeEndpoint)
			throws UnsupportedEncodingException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(youTubeEndpoint.openStream(), "UTF-8"));
	    for (String line; (line = reader.readLine()) != null;) {
	    		if (line.contains("\"title\"") && title==null)
	    			title = line.split("\"")[3].trim();
	    		if (line.contains("\"description\"") && description==null) {
	    			description =line.substring( line.indexOf(": \"") +3 );
	    			description = description.substring(0, description.length() - 2);
	    		}
	    		if (line.contains("\"url\"") )
	    			thumb = line.split("\"")[3].trim();
	    		if (line.contains("\"duration\"") )
	    			duration = line.split("\"")[3].trim().replace("PT", "");
	    		if (line.contains("\"publishedAt\"") && publishedAt==null)
	    			publishedAt = line.split("\"")[3].trim();
	    		url = youTubeURL;
	    		System.out.println(line);      		    	
		}
	    String json = toJson();
	    System.out.println(json);
		return json;
	}
	
	public String toJson() {
		String s = "{ "+
			("\"ID\": \""+	id+"\"")+
			(title == null ? "" : (",\"Title\": \""+	title+"\"") )+
			(description == null ? "" : (",\"Description\": \""+	description+"\"") )+
			(thumb == null ? "" :",\"Thumb\": \""+		thumb+"\" ")+ 
			(duration == null ? "" :",\"Duration\": \""+		duration+"\" ")+ 
			(url == null ? "" :  (",\"Url\": \""+		url+"\" "))+ 
			(publishedAt == null? "" : ( ",\"PublishedAt\": \""+	publishedAt+"\""))+
			(type == null? "" : ( ",\"Type\": \""+	type+"\""))+
		" }";	
		return s;
	}
	
}