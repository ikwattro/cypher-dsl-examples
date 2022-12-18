package dev.ikwattro;

import org.junit.jupiter.api.Test;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Functions;

import static org.assertj.core.api.Assertions.assertThat;

public class RelationshipTraversalExamplesTest extends AbstractCypherDSLTest {

    @Test
    void traverse_relationship_from_node() {
        var nodeFrom = Cypher.node("Person").named("person");
        var movieNode = Cypher.anyNode("movie");
        var statement = Cypher.match(nodeFrom.relationshipTo(movieNode, "ACTED_IN"))
                .returning(nodeFrom, movieNode)
                .build();

        var cypher = generateQuery(statement);
        String expected = """
                MATCH (person:`Person`)-[:`ACTED_IN`]->(movie)
                RETURN person, movie
                """;
        assertThat(cypher).isEqualTo(expected.trim());
    }

    @Test
    void traverse_relationship_and_return_rel() {
        var nodeFrom = Cypher.node("Person").named("person");
        var movieNode = Cypher.anyNode("movie");
        var actedInRel = nodeFrom.relationshipTo(movieNode, "ACTED_IN").named("r");
        var statement = Cypher.match(actedInRel)
                .returning(nodeFrom, movieNode, actedInRel)
                .build();

        var cypher = generateQuery(statement);
        String expected = """
                MATCH (person:`Person`)-[r:`ACTED_IN`]->(movie)
                RETURN person, movie, r
                """;
        assertThat(cypher).isEqualTo(expected.trim());
    }

    @Test
    void traverse_relationship_with_multi_types() {
        var nodeFrom = Cypher.node("Person").named("person");
        var movieNode = Cypher.anyNode("movie");
        var actedInRel = nodeFrom.relationshipTo(movieNode, "ACTED_IN", "PRODUCED").named("r");
        var statement = Cypher.match(actedInRel)
                .returning(nodeFrom, movieNode, actedInRel)
                .build();

        var cypher = generateQuery(statement);
        String expected = """
                MATCH (person:`Person`)-[r:`ACTED_IN`|`PRODUCED`]->(movie)
                RETURN person, movie, r
                """;
        assertThat(cypher).isEqualTo(expected.trim());
    }

    @Test
    void traverse_relationships_any_type_and_return_type_counts() {
        var nodeFrom = Cypher.node("Person").named("person");
        var movieNode = Cypher.anyNode("movie");
        var actedInRel = nodeFrom.relationshipTo(movieNode).named("r");
        var count = Functions.count(Cypher.asterisk()).as("count");
        var statement = Cypher.match(actedInRel)
                .returning(
                        nodeFrom.getRequiredSymbolicName(),
                        movieNode.getRequiredSymbolicName(),
                        Functions.type(actedInRel),
                        count
                )
                .build();

        var cypher = generateQuery(statement);
        String expected = """
                MATCH (person:`Person`)-[r]->(movie)
                RETURN person, movie, type(r), count(*) AS count
                """;
        assertThat(cypher).isEqualTo(expected.trim());
    }

    @Test
    void traverse_relationships_undirected() {
        var nodeFrom = Cypher.node("Person").named("person");
        var movieNode = Cypher.anyNode("movie");
        var actedInRel = nodeFrom.relationshipBetween(movieNode, "ACTED_IN", "PRODUCED").named("r");
        var statement = Cypher.match(actedInRel)
                .returning(nodeFrom, movieNode, actedInRel)
                .build();

        var cypher = generateQuery(statement);
        String expected = """
                MATCH (person:`Person`)-[r:`ACTED_IN`|`PRODUCED`]-(movie)
                RETURN person, movie, r
                """;
        assertThat(cypher).isEqualTo(expected.trim());
    }

    @Test
    void traverse_relationships_undirected_with_length() {
        var nodeFrom = Cypher.node("Person").named("person");
        var movieNode = Cypher.anyNode("movie");
        var actedInRel = nodeFrom.relationshipTo(movieNode, "ACTED_IN", "PRODUCED").named("r").length(1, 4);
        var statement = Cypher.match(actedInRel)
                .returning(nodeFrom, movieNode, actedInRel)
                .build();

        var cypher = generateQuery(statement);
        String expected = """
                MATCH (person:`Person`)-[r:`ACTED_IN`|`PRODUCED`*1..4]->(movie)
                RETURN person, movie, r
                """;
        assertThat(cypher).isEqualTo(expected.trim());
    }
}
