package com.test.data;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.wordnik.swagger.annotations.ApiModel;
import org.joda.time.DateTime;

/**
 * Book data bean
 */
@Entity
@Cache
@ApiModel("Book object")
public class BookBean {

    @Id
    private Long id;

	@Index 
	private String name;
	
	@Index 
	private String author;
	
	private DateTime releaseDate;
	
	private String genre;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
	
	public String getAuthor(){
		return author;
	}
	
	public void setAuthor(String author){
		this.author = author;
	}
	
	public DateTime getReleaseDate(){
		return releaseDate;
	}
	
	public void setReleaseDate(DateTime releaseDate){
		this.releaseDate = releaseDate;
	}
	
	public String getGenre(){
		return genre;
	}
	
	public void setGenre(String genre){
		this.genre = genre;
	}
}
