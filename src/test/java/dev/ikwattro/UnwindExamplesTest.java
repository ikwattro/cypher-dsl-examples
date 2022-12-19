package dev.ikwattro;

import org.junit.jupiter.api.Test;
import org.neo4j.cypherdsl.core.Cypher;

import static org.assertj.core.api.Assertions.assertThat;

public class UnwindExamplesTest extends AbstractCypherDSLTest {

    @Test
    void unwind_nodes_from_ids_list() {
        var node = Cypher.node("Person").named("person");
        var statement = Cypher.unwind(Cypher.parameter("ids")).as("id")
                .match(node).where(node.internalId().isEqualTo(Cypher.name("id")))
                .returning(node)
                .build();

        var cypher = generateQuery(statement);
        String expected = """
                UNWIND $ids AS id MATCH (person:`Person`)
                WHERE id(person) = id
                RETURN person
                """;
        assertThat(cypher).isEqualTo(expected.trim());
    }
}
