package com.bitirme.bitirmeapi.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
public class PersistenceConfig {

    @PersistenceContext
    private final EntityManager entityManager;

    public PersistenceConfig(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public GeometryFactory geometryFactory() { return new GeometryFactory(); }

}
