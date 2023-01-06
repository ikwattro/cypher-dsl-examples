package dev.ikwattro;

import org.junit.jupiter.api.Test;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Expressions;

import static org.assertj.core.api.Assertions.assertThat;

public class CountExpressionExamplesTest extends AbstractCypherDSLTest {

    @Test
    void count_expression_replacing_size() {
        var person = Cypher.node("Person").named("person");
        var cypher = Cypher.match(person)
                .returning(
                        person.as("person"),
                        Expressions.count(person.relationshipTo(Cypher.anyNode(), "ACTED_IN")).as("acted_in_rel_count")
                ).build();

        var query = generateQuery(cypher);
        var expected = """
                MATCH (person:`Person`)
                RETURN person AS person, COUNT { (person)-[:`ACTED_IN`]->()
                } AS acted_in_rel_count
                """;
        assertThat(query).isEqualTo(expected.trim());
    }
}
