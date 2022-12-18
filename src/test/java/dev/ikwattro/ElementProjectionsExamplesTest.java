package dev.ikwattro;

import org.junit.jupiter.api.Test;
import org.neo4j.cypherdsl.core.Cypher;

import static org.assertj.core.api.Assertions.assertThat;

public class ElementProjectionsExamplesTest extends AbstractCypherDSLTest {

    @Test
    void return_nodes_projection() {
        var node = Cypher.node("Person").named("person");
        var statement = Cypher.match(node)
                .where(node.internalId().in(Cypher.parameter("$ids")))
                .returning(node
                        .project("name", node.property("name"), "__labels", node.labels(), "__id", node.internalId())
                        .as("__node__"))
                .build();

        var cypher = generateQuery(statement);
        String expected = """
                MATCH (person:`Person`)
                WHERE id(person) IN $ids
                RETURN person {
                  name: person.name,
                  __labels: labels(person),
                  __id: id(person)
                } AS __node__
                """;
        assertThat(cypher).isEqualTo(expected.trim());
    }
}
