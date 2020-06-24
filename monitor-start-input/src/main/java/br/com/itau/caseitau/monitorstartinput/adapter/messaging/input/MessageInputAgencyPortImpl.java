package br.com.itau.caseitau.monitorstartinput.adapter.messaging.input;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.itau.caseitau.monitorstartinput.adapter.mongo.io.entities.Application;
import br.com.itau.caseitau.monitorstartinput.core.ApplicationCore;
import lombok.extern.slf4j.Slf4j;


/***
 *
 * Classe responsavel por conectar a fila que recebe os registro de transacoes 
 * de agencia
 * 
 */
@Slf4j
@Service
public class MessageInputAgencyPortImpl {

	private static final String MSG_PATTERN = "Mensagem Recebida - [Fila: %s] # Conteudo: %s";

	@Value("${application.fl.monitor.channel.agency.input}")
	private String queueNew;
	
	@Autowired
	private ApplicationCore applicationCore;
	

	@JmsListener(destination = "${application.fl.monitor.channel.agency.input}")
	public void newInputLogAgency(String message) {

		log.info("newInputLogAgency");
		log.info(String.format(MSG_PATTERN, queueNew, message));

		
		applicationCore.processMessageMainframe(getObjectPlace(message));
	}
	
	
	private Application getObjectPlace(String json) {

		ObjectMapper objectMapper = new ObjectMapper();

		Application application = null;
		
		try {
			application = objectMapper.readValue(json, Application.class);
		} catch (JsonParseException e) {
			log.error(e.getMessage());
		} catch (JsonMappingException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		return application;
	}

}
