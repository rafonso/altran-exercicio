package rafael.altran.exercicio.carrinhocomprasbackend.controllers;

import com.mongodb.MongoClientURI;
import com.mongodb.WriteConcern;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.UserRepository;

@Configuration
class MongoConfiguration implements InitializingBean, DisposableBean {

    MongodExecutable executable;

    @Override
    public void afterPropertiesSet() throws Exception {
        String host = "localhost";
        int port = 27019;

        IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                .net(new Net(host, port, Network.localhostIsIPv6()))
                .build();

        MongodStarter starter = MongodStarter.getDefaultInstance();
        executable = starter.prepare(mongodConfig);
        executable.start();
    }


    @Bean
    public MongoDbFactory factory() {
        MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(new MongoClientURI("mongodb://localhost:27019/test_db"));
        return mongoDbFactory;
    }


    @Bean
    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory) {
        MongoTemplate template = new MongoTemplate(mongoDbFactory);
        template.setWriteConcern(WriteConcern.ACKNOWLEDGED);
        return template;
    }

    @Bean
    public MongoRepositoryFactoryBean mongoFactoryRepositoryBean(MongoTemplate template) {
        MongoRepositoryFactoryBean mongoDbFactoryBean = new MongoRepositoryFactoryBean(UserRepository.class);
        mongoDbFactoryBean.setMongoOperations(template);

        return mongoDbFactoryBean;
    }

    @Override
    public void destroy() throws Exception {
        executable.stop();
    }
}
