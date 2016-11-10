package pl.edu.agh.ki.bd.htmlIndexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import pl.edu.agh.ki.bd.htmlIndexer.persistence.HibernateUtils;

public class HtmlIndexerApp {

	public static void main(String[] args) throws IOException {
		HibernateUtils.getSession().close();

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		Index indexer = new NativeSqlDrivenIndex();

		while (true) {
			System.out.println("\nHtmlIndexer [? for help] > : ");
			String command = bufferedReader.readLine();
			long startAt = new Date().getTime();

			if (command.startsWith("?")) {
				System.out.println("'?'      	- print this help");
				System.out.println("'x'      	- exit HtmlIndexer");
				System.out.println("'i URLs'  	- index URLs, space separated");
				System.out.println("'f WORDS'	- find sentences containing all WORDs, space separated");
			} else if (command.startsWith("x")) {
				System.out.println("HtmlIndexer terminated.");
				HibernateUtils.shutdown();
				break;
			} else if (command.startsWith("i ")) {
				for (String url : command.substring(2).split(" ")) {
					try {
						indexer.indexWebPage(url);
						System.out.println("Indexed: " + url);
					} catch (Exception e) {
						System.out.println("Error indexing: " + e.getMessage());
					}
				}
			} else if (command.startsWith("f ")) {
				for (Object[] row: indexer.findSentencesByWords(command.substring(2))) {
					System.out.println("Found in sentence: " + row[0] + "\nMatched words: " + row[1] + "\nFrom URL: " + row[2] + "\n\n");
				}
			} else if (command.startsWith("l ")) {
				for (Object sentence : indexer.findSentenceByLength(Integer.parseInt(command.substring(2)))) {
					System.out.println(sentence);
				}
			} else if (command.equals("print")) {
				for (Object[] row : indexer.findUrlsBySentenceNumber()) {
					System.out.println("URL: " + row[0] + "\tNUMBER OF SENTENCES: " + row[1]);
				}
			} else if (command.startsWith("count ")) {
				String word = command.substring(6);
				int occurences = indexer.countWordOccurences(word);
				System.out.println("WORD: " + word + "\t\tOCCURENCES: " + occurences + "\n");
			}

			System.out.println("took " + (new Date().getTime() - startAt) + " ms");

		}

	}

}
