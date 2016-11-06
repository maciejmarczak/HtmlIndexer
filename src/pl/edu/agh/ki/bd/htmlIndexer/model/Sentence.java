package pl.edu.agh.ki.bd.htmlIndexer.model;

import java.util.Set;

public class Sentence {

	private long id;
	private String content;
	Set<Word> words;
	private ProcessedUrl processedUrl;

	public Sentence() {
	}

	public Sentence(String content, Set<Word> words, ProcessedUrl processedUrl) {
		this.setContent(content);
		this.setWords(words);
		this.setProcessedUrl(processedUrl);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Set<Word> getWords() {
		return words;
	}

	public void setWords(Set<Word> words) {
		this.words = words;
	}

	public ProcessedUrl getProcessedUrl() {
		return processedUrl;
	}

	public void setProcessedUrl(ProcessedUrl processedUrl) {
		this.processedUrl = processedUrl;
	}

}
