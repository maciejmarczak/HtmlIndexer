package pl.edu.agh.ki.bd.htmlIndexer;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.Transaction;

import pl.edu.agh.ki.bd.htmlIndexer.persistence.HibernateUtils;

public class NativeSqlDrivenIndex extends Index {

	@Override
	public List<Object[]> findSentencesByWords(String words) {
		String inClause = "(" + Arrays.asList(words.split("\\s+")).stream()
								.map(x -> "'" + x + "'")
								.reduce((x, y) -> x + ", " + y).get() + ")";
	
		Session session = HibernateUtils.getSession();
		Transaction transaction = session.beginTransaction();
		
		// I AM AWARE THAT THE LINE BELOW IS VULNERABLE TO
		// SQL INJECTION ATTACKS, IT'S FOR TESTING PURPOSES ONLY
		Query query = session.createNativeQuery("select s.content, count(w.content) as wc, u.url from word w inner join wordsentences ws on ws.content = w.content inner join sentence s on s.sentenceId = ws.sentenceId inner join processedurl u on u.id = s.processedUrlId where w.content in " + inClause + " group by s.content order by wc desc");
		
		@SuppressWarnings("unchecked")
		List<Object[]> result = (List<Object[]>) query.getResultList();
		
		transaction.commit();
		session.close();
		
		return result;
	}

	@Override
	public List<String> findSentenceByLength(int length) {
		Session session = HibernateUtils.getSession();
		Transaction transaction = session.beginTransaction();
		
		Query query = session.createNativeQuery("select s.content from sentence s where LENGTH(s.content) > ?");
		query.setParameter(1, length);
		
		@SuppressWarnings("unchecked")
		List<String> result = (List<String>) query.getResultList();
		
		transaction.commit();
		session.close();
		
		return result;
	}

	@Override
	public List<Object[]> findUrlsBySentenceNumber() {		
		Session session = HibernateUtils.getSession();
		Transaction transaction = session.beginTransaction();
		
		Query query = session.createNativeQuery("select u.url, count(s.sentenceId) as csid from processedurl u inner join sentence s on s.processedUrlId = u.id group by u.url order by csid desc");
		
		@SuppressWarnings("unchecked")
		List<Object[]> result = (List<Object[]>) query.getResultList();
		
		transaction.commit();
		session.close();
		
		return result;
	}

	@Override
	public int countWordOccurences(String word) {
		int occurences = 0;
		
		Session session = HibernateUtils.getSession();
		Transaction transaction = session.beginTransaction();
		
		Query query = session.createNativeQuery("select count(ws.content) from wordsentences ws where ws.content = ?");
		query.setParameter(1, word);
		occurences = ((BigInteger) query.getSingleResult()).intValue();
		
		transaction.commit();
		session.close();
		
		return occurences;
	}
	
	
	
}
