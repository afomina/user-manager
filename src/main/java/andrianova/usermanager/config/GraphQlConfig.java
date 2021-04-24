package andrianova.usermanager.config;

import andrianova.usermanager.service.UserService;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphQlConfig {

    @Bean
    public GraphQLSchema graphQLSchema(UserService userService) {
        return new GraphQLSchemaGenerator()
                .withBasePackages("andrianova.usermanager")
                .withOperationsFromSingleton(userService)
                .generate();
    }

    @Bean
    public GraphQL graphQL(GraphQLSchema graphQLSchema) {
        return new GraphQL.Builder(graphQLSchema)
                .build();
    }

}
