package pl.edu.agh.ki.bd.htmlIndexer.model;

import java.util.Date;
import java.util.List;

public class ProcessedUrl {

	private long id;
	private String url;
	private Date date;
	private List<Sentence> sentences;

	public ProcessedUrl() {

	}

	public ProcessedUrl(String url, Date date) {
		this.url = url;
		this.date = date;
	}

	public ProcessedUrl(String url, Date date, List<Sentence> sentences) {
		this(url, date);
		this.sentences = sentences;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<Sentence> getSentences() {
		return sentences;
	}

	public void setSentences(List<Sentence> sentences) {
		this.sentences = sentences;
	}

}
