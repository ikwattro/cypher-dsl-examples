package dev.ikwattro;

import org.junit.jupiter.api.Test;
import org.neo4j.cypherdsl.core.Cypher;

import static org.assertj.core.api.Assertions.assertThat;

public class CompositeDatabaseExamplesTest extends AbstractCypherDSLTest {

    @Test
    void composite_database_query() {
        var statement = Cypher.use("movies.actors",
                Cypher.call(
                        Cypher.match(Cypher.node("Person").named("person")).returning("person").build()
                ).returning("person").build()
        );

        var cypher = generateQuery(statement);
        var expected = """
                USE movies.actors
                CALL {
                  MATCH (person:`Person`)
                  RETURN person
                }
                RETURN person
                """;

        assertThat(cypher).isEqualTo(expected.trim());
    }

    @Test
    void use_inside_call() {
        var matchStatement = Cypher.match(Cypher.node("Person").named("person")).returning("person").build();
        var statement = Cypher.call(
                Cypher.use("movies.actors", matchStatement)
        ).returning("person").build();

        var cypher = generateQuery(statement) ;
        var expected = """
                CALL {
                  USE movies.actors
                  MATCH (person:`Person`)
                  RETURN person
                }
                RETURN person""";
        assertThat(cypher).isEqualTo(expected.trim());
    }
}
