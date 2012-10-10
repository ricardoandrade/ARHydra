package br.unb.unbiquitous.configuration;

import org.apache.log4j.Level;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * Classe que configura o Log4j para o LogCat do Android.
 * @author ricardoandrade
 *
 */
public class ConfigLog4j {

	/**
	 * Método responsável por configurar o monitor do LogCat
	 * para apresentar as mensagens da classe Logger. 
	 */
	public static void configure() {	
			
			final LogConfigurator logConfigurator = new LogConfigurator();
			logConfigurator.setUseFileAppender(false);
	        logConfigurator.setUseLogCatAppender(true);
	        logConfigurator.setRootLevel(Level.DEBUG);
	        logConfigurator.setLevel("org.apache", Level.ERROR);
	        logConfigurator.configure();

	}

}