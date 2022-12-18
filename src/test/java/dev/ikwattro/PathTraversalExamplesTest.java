package dev.ikwattro;

import org.junit.jupiter.api.Test;
import org.neo4j.cypherdsl.core.Cypher;

import static org.assertj.core.api.Assertions.assertThat;

public class PathTraversalExamplesTest extends AbstractCypherDSLTest {

    @Test
    void simple_path_traversal() {
        var nodeFrom = Cypher.node("Person").named("person");
        var movieNode = Cypher.anyNode("movie");
        var actedInRel = nodeFrom.relationshipTo(movieNode, "ACTED_IN", "PRODUCED").named("r");
        var path = Cypher.path("path").definedBy(actedInRel);
        var statement = Cypher.match(path)
                .returning(path)
                .build();

        var cypher = generateQuery(statement);
        String expected = """
                MATCH path = (person:`Person`)-[r:`ACTED_IN`|`PRODUCED`]->(movie)
                RETURN path
                """;
        assertThat(cypher).isEqualTo(expected.trim());
    }

    @Test
    void shortest_path() {
        var nodeFrom = Cypher.node("Person").named("person");
        var movieNode = Cypher.anyNode("movie");
        var actedInRel = nodeFrom.relationshipTo(movieNode, "ACTED_IN", "PRODUCED").named("r");
        var path = Cypher.shortestPath("path").definedBy(actedInRel);
        var statement = Cypher.match(path)
                .returning(path)
                .build();

        var cypher = generateQuery(statement);
        String expected = """
                MATCH path = shortestPath((person:`Person`)-[r:`ACTED_IN`|`PRODUCED`]->(movie))
                RETURN path
                """;
        assertThat(cypher).isEqualTo(expected.trim());
    }

    @Test
    void all_shortest_paths() {
//        Is it possible yet ?
//        var nodeFrom = Cypher.node("Person").named("person");
//        var movieNode = Cypher.anyNode("movie");
//        var actedInRel = nodeFrom.relationshipTo(movieNode, "ACTED_IN", "PRODUCED").named("r");
//        var path = Cypher.path("path").definedBy(actedInRel);
//        var statement = Cypher.match(path)
//                .returning(path)
//                .build();
//
//        var cypher = generateQuery(statement);
//        String expected = """
//                MATCH path = (person:`Person`)-[r:`ACTED_IN`|`PRODUCED`]->(movie)
//                RETURN path
//                """;
//        assertThat(cypher).isEqualTo(expected.trim());
    }
}
