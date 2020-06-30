package io.harness.ng;

import static com.google.inject.Key.get;
import static com.google.inject.name.Names.named;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Injector;

import com.mongodb.MongoClient;
import org.mongodb.morphia.AdvancedDatastore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.guice.annotation.GuiceModule;

import java.util.Collection;

@Configuration
@GuiceModule
@EnableMongoRepositories(mongoTemplateRef = "primary")
@EnableMongoAuditing
public abstract class SpringPersistenceConfig extends AbstractMongoConfiguration {
  private final AdvancedDatastore advancedDatastore;
  private static final Collection<String> BASE_PACKAGES = ImmutableList.of("io.harness");

  @Inject
  public SpringPersistenceConfig(Injector injector) {
    advancedDatastore = injector.getProvider(get(AdvancedDatastore.class, named("primaryDatastore"))).get();
  }

  @Override
  public MongoClient mongoClient() {
    return advancedDatastore.getMongo();
  }

  @Override
  protected String getDatabaseName() {
    return advancedDatastore.getDB().getName();
  }

  @Override
  protected Collection<String> getMappingBasePackages() {
    return BASE_PACKAGES;
  }

  @Bean(name = "primary")
  @Primary
  @Override
  public MongoTemplate mongoTemplate() {
    return new MongoTemplate(mongoClient(), getDatabaseName());
  }
}
