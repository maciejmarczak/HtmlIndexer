package pl.edu.agh.ki.bd.htmlIndexer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.edu.agh.ki.bd.htmlIndexer.model.ProcessedUrl;
import pl.edu.agh.ki.bd.htmlIndexer.model.Sentence;
import pl.edu.agh.ki.bd.htmlIndexer.model.Word;
import pl.edu.agh.ki.bd.htmlIndexer.persistence.HibernateUtils;

public class Index {
	public void indexWebPage(String url) throws IOException {

		Document doc = Jsoup.connect(url).get();
		Elements elements = doc.body().select("*");

		ProcessedUrl processedUrl = new ProcessedUrl(url, new Date());
		List<Sentence> sentences = new LinkedList<>();

		Session session = HibernateUtils.getSession();
		Transaction transaction = session.beginTransaction();
		
		session.save(processedUrl);

		for (Element element : elements) {
			if (element.ownText().trim().length() > 1) {
				for (String sentenceContent : element.ownText().split("\\. ")) {
					Set<Word> words = buildWords(session, sentenceContent);
					Sentence sentence = buildSentence(session, sentenceContent, words, processedUrl);
					sentences.add(sentence);
				}
			}
		}

		processedUrl.setSentences(sentences);

		session.update(processedUrl);

		transaction.commit();
		session.close();
	}

	public List<Object[]> findSentencesByWords(String words) {
		Session session = HibernateUtils.getSession();
		Transaction transaction = session.beginTransaction();

		List<String> tokens = Arrays.asList(words.split("\\s+"));
		
		String query = "select s.content, count(w.content) as wc, s.processedUrl.url from Word w join w.sentences s where w.content in (:tokens) group by s.content order by wc desc";
		
		@SuppressWarnings("unchecked")
		List<Object[]> result = session.createQuery(query).setParameterList("tokens", tokens).getResultList();

		transaction.commit();
		session.close();

		return result;
	}

	public List<String> findSentenceByLength(int length) {
		Session session = HibernateUtils.getSession();
		Transaction transaction = session.beginTransaction();

		List<String> result = session
				.createQuery("select s.content from Sentence s where LENGTH(s.content) > :length", String.class)
				.setParameter("length", length).getResultList();

		transaction.commit();
		session.close();

		return result;
	}

	public List<Object[]> findUrlsBySentenceNumber() {
		Session session = HibernateUtils.getSession();
		Transaction transaction = session.beginTransaction();

		String hql = "select u.url, u.sentences.size as s from ProcessedUrl u order by s desc";

		@SuppressWarnings("unchecked")
		List<Object[]> result = session.createQuery(hql).getResultList();

		transaction.commit();
		session.close();

		return result;
	}
	
	public int countWordOccurences(String word) {
		int occurences = 0;
		
		Session session = HibernateUtils.getSession();
		Transaction transaction = session.beginTransaction();
		
		Word w = session.get(Word.class, word.toLowerCase());
		occurences = w.getSentences().size();
		
		transaction.commit();
		session.close();
		
		return occurences;
	}

	private Set<Word> buildWords(Session session, String sentence) {
		Set<Word> result = new HashSet<>();

		for (String wordContent : sentence.split("\\s+")) {

			// Adjust the character class
			wordContent = wordContent.replaceAll("[^\\w]", "");

			Word word;
			try {
				word = session.createQuery("from Word w where w.content like :wordContent", Word.class).setParameter("wordContent", wordContent).getSingleResult();
			} catch (NoResultException nre) {
				word = new Word(wordContent);
				session.persist(word);
			}

			result.add(word);
		}
		return result;
	}
	
	private Sentence buildSentence(Session session, String sentenceContent, Set<Word> words, ProcessedUrl processedUrl) {
		Sentence sentence;
		
		try {
			sentence = session.createQuery("from Sentence s where s.content = :sentenceContent", Sentence.class).setParameter("sentenceContent", sentenceContent).getSingleResult();
		} catch (NoResultException nre) {
			sentence = new Sentence(sentenceContent, words, processedUrl);
			session.persist(sentence);
		}
		
		return sentence;
	}

}
