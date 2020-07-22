package com.assignment.go.ipservice.init;

import com.assignment.go.ipservice.entity.IPPool;
import com.assignment.go.ipservice.error.InfrastructureException;
import com.assignment.go.ipservice.repository.IPPoolRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Component
public class DataLoader {

	private static final Logger LOG = LoggerFactory.getLogger(DataLoader.class);

	private static final String DATA_JSON = "data.json";

	private final ObjectMapper objectMapper;
	private final IPPoolRepository ipPoolRepository;
	private final TransactionTemplate transactionTemplate;

	public DataLoader(ObjectMapper objectMapper, IPPoolRepository ipPoolRepository, TransactionTemplate transactionTemplate) {
		this.objectMapper = objectMapper;
		this.ipPoolRepository = ipPoolRepository;
		this.transactionTemplate = transactionTemplate;
	}

	@PostConstruct
	void init() {

		try {
			URL resource = this.getClass().getClassLoader().getResource(DATA_JSON);

			Path path = Paths.get(Objects.requireNonNull(resource).toURI());

			List<String> lines = Files.readAllLines(path);

			String allContent = String.join("", lines);

			List<IPPool> ipPools = objectMapper.readValue(allContent, new TypeReference<List<IPPool>>() {

			});

			ipPools.forEach(IPPool::initCapacities);

			transactionTemplate.execute(new TransactionCallbackWithoutResult() {

				@Override protected void doInTransactionWithoutResult(TransactionStatus status) {
					ipPoolRepository.saveAll(ipPools);
				}
			});

			LOG.debug("predefined ip pools saved");

		} catch (IOException | URISyntaxException e) {
			LOG.error("error occurred during loading of predefined ip pools, message: " + e.getMessage(), e);
			throw new InfrastructureException(e);
		}
	}

}
