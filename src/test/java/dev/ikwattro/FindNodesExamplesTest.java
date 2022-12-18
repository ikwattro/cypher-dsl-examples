package dev.ikwattro;

import org.junit.jupiter.api.Test;
import org.neo4j.cypherdsl.core.Cypher;

import static org.assertj.core.api.Assertions.assertThat;

public class FindNodesExamplesTest extends AbstractCypherDSLTest {

    @Test
    void nodes_by_label() {
        var node = Cypher.node("Person").named("person");
        var statement = Cypher.match(node)
                .returning(node)
                .build();

        var cypher = generateQuery(statement);
        String expected = """
                MATCH (person:`Person`)
                RETURN person
                """;
        assertThat(cypher).isEqualTo(expected.trim());
    }

    @Test
    void nodes_by_label_and_props() {
        var node = Cypher.node("Person").named("person");
        var statement = Cypher.match(node)
                .where(node.property("name").isEqualTo(Cypher.literalOf("The Matrix")))
                .returning(node)
                .build();

        var cypher = generateQuery(statement);
        String expected = """
                MATCH (person:`Person`)
                WHERE person.name = 'The Matrix'
                RETURN person
                """;
        assertThat(cypher).isEqualTo(expected.trim());
    }

    @Test
    void nodes_by_label_limit_result() {
        var node = Cypher.node("Person").named("person");
        var statement = Cypher.match(node)
                .returning(node)
                .limit(10)
                .build();

        var cypher = generateQuery(statement);
        String expected = """
                MATCH (person:`Person`)
                RETURN person LIMIT 10
                """;
        assertThat(cypher).isEqualTo(expected.trim());
    }

    @Test
    void find_nodes_by_id() {
        var node = Cypher.node("Person").named("person");
        var statement = Cypher.match(node)
                .where(node.internalId().isEqualTo(Cypher.literalOf(1)))
                .returning(node)
                .build();

        var cypher = generateQuery(statement);
        String expected = """
                MATCH (person:`Person`)
                WHERE id(person) = 1
                RETURN person
                """;
        assertThat(cypher).isEqualTo(expected.trim());
    }

    @Test
    void find_nodes_by_element_id() {
        var node = Cypher.node("Person").named("person");
        var statement = Cypher.match(node)
                .where(node.elementId().isEqualTo(Cypher.literalOf("4:2ddd1f4f-0cfd-409d-8fb8-006a6ad2dabc:1")))
                .returning(node)
                .build();

        var cypher = generateQuery(statement);
        String expected = """
                MATCH (person:`Person`)
                WHERE elementId(person) = '4:2ddd1f4f-0cfd-409d-8fb8-006a6ad2dabc:1'
                RETURN person
                """;
        assertThat(cypher).isEqualTo(expected.trim());
    }

    @Test
    void find_nodes_in_given_id_list() {
        var node = Cypher.node("Person").named("person");
        var statement = Cypher.match(node)
                .where(node.internalId().in(Cypher.parameter("$ids")))
                .returning(node)
                .build();

        var cypher = generateQuery(statement);
        String expected = """
                MATCH (person:`Person`)
                WHERE id(person) IN $ids
                RETURN person
                """;
        assertThat(cypher).isEqualTo(expected.trim());
    }
}
